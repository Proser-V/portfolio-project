"use client";
import { useState, useEffect, useRef } from "react";
import { motion } from "framer-motion";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

export default function ConversationClient({ initialMessages, user, otherUser, otherUserName, jwtToken }) {
  const [messages, setMessages] = useState(initialMessages || []);
  const [messageText, setMessageText] = useState("");
  const [file, setFile] = useState(null);
  const [stompClient, setStompClient] = useState(null);
  const messagesEndRef = useRef(null);

  // Connexion WebSocket
  useEffect(() => {
    const socket = new SockJS(`${process.env.NEXT_PUBLIC_API_URL}/ws`);
    const client = Stomp.over(socket);

    client.connect(
      { Authorization: `Bearer ${jwtToken}` },
      () => {
        console.log("✅ WebSocket connecté");

        // S'abonner aux messages
        client.subscribe("/user/queue/messages", (message) => {
          const received = JSON.parse(message.body);
          console.log("Message reçu via WebSocket:", received);
          console.log("TempId reçu:", received.tempId);
          console.log("Timestamp:", received.timestamp);
          console.log("CreatedAt:", received.createdAt);
          console.log("Tous les champs:", Object.keys(received));

          setMessages((prev) => {
            console.log("🔍 Messages actuels:", prev.map(m => ({ id: m.id, tempId: m.tempId, content: m.content })));

            // Cas 1 : Message avec tempId (réponse à notre envoi)
            if (received.tempId) {
              const tempIndex = prev.findIndex(
                (m) => m.tempId === received.tempId
              );

              console.log("🔍 Index du message temporaire:", tempIndex);

              if (tempIndex !== -1) {
                // Remplacer le message temporaire par le vrai
                const updated = [...prev];
                
                // Garder le timestamp du message optimiste si le backend ne renvoie pas de date
                const finalTimestamp = received.createdAt || received.timestamp || prev[tempIndex].timestamp || new Date().toISOString();
                
                updated[tempIndex] = {
                  ...received,
                  messageStatus: "DELIVERED",
                  timestamp: finalTimestamp,
                  createdAt: finalTimestamp
                };
                console.log("✅ Message temporaire remplacé:", updated[tempIndex]);
                console.log("✅ Timestamp final:", finalTimestamp);
                return updated;
              }
            }

            // Cas 2 : Nouveau message reçu d'un autre utilisateur
            // Vérifier si le message existe déjà (éviter les doublons)
            const exists = prev.some((m) => m.id === received.id);
            if (exists) {
              console.log("⚠️ Message déjà existant, ignoré");
              return prev;
            }

            console.log("✅ Nouveau message ajouté");
            return [...prev, received];
          });
        });

        setStompClient(client);
      },
      (error) => {
        console.error("❌ Erreur WebSocket:", error);
      }
    );

    return () => {
      if (client && client.connected) {
        client.disconnect();
      }
    };
  }, [jwtToken]);

  // Auto-scroll
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // Envoyer un message
  const handleSendMessage = async (e) => {
    e.preventDefault();

    if (!messageText.trim() && !file) return;

    // 1️⃣ Créer un message optimiste avec ID temporaire
    const tempId = `temp-${Date.now()}-${Math.random()}`;
    console.log("🚀 Création message optimiste avec tempId:", tempId);

    const optimisticMessage = {
      tempId: tempId,
      id: null,
      senderId: user.id,
      receiverId: otherUser.id,
      content: messageText,
      timestamp: new Date().toISOString(),
      messageStatus: "SENDING",
      attachments: file ? [{ fileUrl: URL.createObjectURL(file), fileType: file.type }] : [],
    };

    // 2️⃣ Ajouter immédiatement à l'UI
    setMessages((prev) => {
      console.log("📝 Ajout du message optimiste");
      return [...prev, optimisticMessage];
    });

    // 3️⃣ Réinitialiser le formulaire
    const messageToSend = messageText;
    const fileToSend = file;
    setMessageText("");
    setFile(null);

    try {
      if (fileToSend) {
        // Envoyer via REST (avec fichier)
        const formData = new FormData();
        formData.append("receiverId", otherUser.id);
        formData.append("content", messageToSend);
        formData.append("file", fileToSend);

        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/messages`, {
          method: "POST",
          headers: {
            Authorization: `Bearer ${jwtToken}`,
          },
          credentials: "include",
          body: formData,
        });

        if (!response.ok) {
          throw new Error("Erreur lors de l'envoi du message avec fichier");
        }

        const savedMessage = await response.json();
        console.log("✅ Message REST sauvegardé:", savedMessage);
        
        // 4️⃣ Remplacer le message optimiste par le vrai
        setMessages((prev) =>
          prev.map((m) => {
            if (m.tempId === tempId) {
              console.log("✅ Remplacement du message optimiste (REST)");
              return { ...savedMessage, messageStatus: "DELIVERED" };
            }
            return m;
          })
        );
      } else {
        // Envoyer via WebSocket (sans fichier)
        if (stompClient && stompClient.connected) {
          console.log("📤 Envoi via WebSocket avec tempId:", tempId);
          
          stompClient.send(
            "/app/chat",
            {},
            JSON.stringify({
              receiverId: otherUser.id,
              content: messageToSend,
              tempId: tempId, // IMPORTANT : Inclure le tempId
            })
          );
        } else {
          throw new Error("WebSocket non connecté");
        }
      }
    } catch (error) {
      console.error("❌ Erreur d'envoi:", error);

      // 5️⃣ Marquer le message comme échoué
      setMessages((prev) =>
        prev.map((m) =>
          m.tempId === tempId ? { ...m, messageStatus: "NOT_SENT" } : m
        )
      );

      alert("Erreur lors de l'envoi du message. Veuillez réessayer.");
    }
  };

  return (
    <div className="flex flex-col h-[80vh]">
      {/* Liste des messages */}
      <MessagesList 
        messages={messages}
        user={user}
        otherUser={otherUser}
        otherUserName={otherUserName}
        messagesEndRef={messagesEndRef}
      />

      {/* Formulaire d'envoi */}
      <div className="border-t pt-4 flex gap-2 items-center">
        <input
          type="text"
          value={messageText}
          onChange={(e) => setMessageText(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
              e.preventDefault();
              handleSendMessage(e);
            }
          }}
          placeholder="Votre message..."
          className="flex-1 border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />

        <label className="cursor-pointer bg-gray-200 hover:bg-gray-300 px-4 py-2 rounded-lg">
          📎 Fichier
          <input
            type="file"
            onChange={(e) => setFile(e.target.files?.[0] || null)}
            className="hidden"
            accept="image/png,image/jpeg,application/pdf"
          />
        </label>

        {file && (
          <span className="text-xs text-gray-600">
            {file.name} ({(file.size / 1024).toFixed(1)} Ko)
          </span>
        )}

        <button
          onClick={handleSendMessage}
          disabled={!messageText.trim() && !file}
          className="bg-blue-500 hover:bg-blue-600 disabled:bg-gray-300 text-white px-6 py-2 rounded-lg transition"
        >
          Envoyer
        </button>
      </div>
    </div>
  );
}

// Composant séparé pour la liste des messages
function MessagesList({ messages, user, otherUser, otherUserName, messagesEndRef }) {
  return (
    <div className="flex-1 relative overflow-y-auto scrollbar-hidden">
      <div
        className="sticky top-0 left-0 right-0 w-full h-20 z-20 pointer-events-none"
        style={{
          background:
            "linear-gradient(to bottom, rgba(255,255,255,1) 0%, rgba(255,255,255,0.8) 50%, rgba(255,255,255,0) 100%)",
        }}
      ></div>

      {messages.length === 0 ? (
        <p className="text-center text-gray-400 text-sm py-4">
          Aucun message dans cette conversation.
        </p>
      ) : (
        messages
          .sort((a, b) => {
            const dateA = new Date(a.timestamp || a.createdAt);
            const dateB = new Date(b.timestamp || b.createdAt);
            return dateA.getTime() - dateB.getTime();
          })
          .map((msg, index) => {
            const isSentByUser = msg.senderId.toString() === user.id.toString();

            let messageDate = "Envoi en cours...";
            
            // Utiliser timestamp ou createdAt
            const msgTime = msg.timestamp || msg.createdAt;
            
            console.log("🕐 Message:", msg.content?.substring(0, 20), "msgTime:", msgTime, "status:", msg.messageStatus);
            
            if (msgTime && msg.messageStatus !== "SENDING") {
              // Essayer plusieurs formats de date
              let parsedDate;
              
              // Format avec espace (ex: "2024-01-20 14:30:00")
              if (typeof msgTime === 'string' && msgTime.includes(' ')) {
                parsedDate = new Date(msgTime.replace(" ", "T"));
              } else {
                parsedDate = new Date(msgTime);
              }
              
              if (!isNaN(parsedDate.getTime())) {
                messageDate = parsedDate.toLocaleString("fr-FR", {
                  day: "2-digit",
                  month: "2-digit",
                  year: "numeric",
                  hour: "2-digit",
                  minute: "2-digit",
                });
              } else {
                console.warn("⚠️ Date invalide pour le message:", msgTime, "type:", typeof msgTime);
                messageDate = "Date invalide";
              }
            }

            // Indicateur d'état
            const statusIndicator =
              msg.messageStatus === "SENDING" ? "⏳" : msg.messageStatus === "NOT_SENT" ? "❌" : "";

            return (
              <motion.div
                key={msg.id || msg.tempId || index}
                className={`flex ${isSentByUser ? "justify-end" : "justify-start"} mb-6 items-end`}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3, ease: "easeOut" }}
              >
                {/* Avatar gauche */}
                {!isSentByUser && otherUser?.avatar?.url && (
                  <img
                    src={otherUser.avatar.url}
                    alt={otherUserName}
                    className="w-10 h-10 rounded-full mr-2"
                  />
                )}

                {/* Bulle + date */}
                <div className="flex flex-col w-[45%] relative">
                  <div
                    className={`py-2 px-2 w-[100%] min-h-[50px] rounded-lg break-words ${
                      isSentByUser ? "bg-blue text-white self-end" : "bg-gray-200 text-gray-800 self-start"
                    } ${msg.messageStatus === "NOT_SENT" ? "opacity-50" : ""}`}
                  >
                    <div className="text-sm">
                      {msg.attachments && msg.attachments.length > 0 ? (
                        msg.attachments.map((attachment, idx) => (
                          <div key={idx} className="mb-1">
                            <a
                              href={attachment.fileUrl}
                              className="underline"
                              target="_blank"
                              rel="noopener noreferrer"
                            >
                              Pièce jointe: {attachment.fileUrl.split("/").pop() || "Fichier"}
                            </a>
                          </div>
                        ))
                      ) : (
                        msg.content
                      )}
                    </div>

                    {/* Triangle de bulle */}
                    <div
                      className={`absolute bottom-8 w-3 h-3 ${
                        isSentByUser
                          ? "bg-blue -right-0 translate-x-1/2 rotate-45"
                          : "bg-gray-200 -left-0 -translate-x-1/2 rotate-45"
                      }`}
                    ></div>
                  </div>

                  {/* Date + statut */}
                  <p
                    className={`text-xs text-gray-500 mt-1 ${
                      isSentByUser ? "self-start" : "self-end"
                    }`}
                  >
                    {statusIndicator} {messageDate}
                  </p>
                </div>

                {/* Avatar droit */}
                {isSentByUser && user?.avatar?.url && (
                  <img src={user.avatar.url} alt="Vous" className="w-10 h-10 rounded-full ml-2" />
                )}
              </motion.div>
            );
          })
      )}
      <div ref={messagesEndRef} />
    </div>
  );
}