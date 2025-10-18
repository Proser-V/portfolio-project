"use client";
import { useState, useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import Image from "next/image";

export default function MessageForm({ user, otherUser, jwtToken, messages, setMessages }) {
  const [message, setMessage] = useState("");
  const [attachment, setAttachment] = useState(null);
  const [stompClient, setStompClient] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const messagesEndRef = useRef(null); // Référence pour le défilement
  const userId = user?.id;
  const otherUserId = otherUser?.id;

  // Faire défiler automatiquement vers le dernier message
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  useEffect(() => {
    if (!jwtToken) {
      console.error("JWT non fourni au composant");
      return;
    }

    const wsUrl = `${process.env.NEXT_PUBLIC_API_URL}/ws`;
    console.log("🔌 Tentative de connexion WebSocket à:", wsUrl);

    const socket = new SockJS(wsUrl);
    const client = new Client({
      webSocketFactory: () => socket,
      connectHeaders: {
        Authorization: `Bearer ${jwtToken}`,
      },
      onConnect: () => {
        console.log("✅ Connecté à WebSocket");
        setIsConnected(true);
        setStompClient(client);

        client.subscribe("/user/queue/messages", (msg) => {
          console.log("📩 Nouveau message reçu:", msg.body);
          try {
            const newMessage = JSON.parse(msg.body);
            setMessages((prev) => [...prev, newMessage]);
          } catch (e) {
            console.error("❌ Erreur parsing message:", e);
          }
        });
      },
      onStompError: (frame) => {
        console.error("❌ Erreur STOMP:", frame);
        setIsConnected(false);
      },
      onWebSocketClose: () => {
        console.log("🔌 WebSocket fermé");
        setIsConnected(false);
      },
      onWebSocketError: (error) => {
        console.error("❌ Erreur WebSocket:", error);
        setIsConnected(false);
      },
      debug: (str) => {
        if (str.includes(">>> PING") || str.includes("<<< PONG")) return;
        console.log("🔍 DEBUG:", str);
      },
    });

    console.log("🚀 Activation du client WebSocket...");
    client.activate();

    return () => {
      console.log("🔌 Déconnexion WebSocket");
      if (client) {
        client.deactivate();
      }
    };
  }, [jwtToken, setMessages]);

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!message.trim() && !attachment) {
      alert("⚠️ Veuillez entrer un message ou joindre un fichier");
      return;
    }

    setIsSending(true);

    try {
      if (attachment) {
        console.log("📎 Envoi via API REST (fichier présent)");
        const formData = new FormData();
        formData.append("receiverId", otherUserId);
        formData.append("content", message.trim() || "Fichier joint");
        formData.append("file", attachment);

        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/messages`, {
          method: "POST",
          headers: {
            Authorization: `Bearer ${jwtToken}`,
          },
          credentials: "include",
          body: formData,
        });

        if (!response.ok) {
          const error = await response.json();
          throw new Error(error.messageError || "Erreur lors de l'envoi");
        }

        const newMessage = await response.json();
        setMessages((prev) => [...prev, newMessage]);
        console.log("✅ Message avec fichier envoyé via REST");
      } else {
        if (!stompClient || !isConnected) {
          alert("❌ Connexion WebSocket non établie. Veuillez rafraîchir la page.");
          console.error("État de connexion:", {
            stompClient: !!stompClient,
            isConnected,
            clientConnected: stompClient?.connected,
          });
          setIsSending(false);
          return;
        }

        console.log("💬 Envoi via WebSocket (texte uniquement)");
        const messageData = {
          senderId: userId,
          receiverId: otherUserId,
          content: message.trim(),
          timestamp: new Date().toISOString(),
        };

        stompClient.publish({
          destination: "/app/chat",
          body: JSON.stringify(messageData),
        });

        setMessages((prev) => [...prev, messageData]);
        console.log("✅ Message texte envoyé via WebSocket");
      }

      setMessage("");
      setAttachment(null);
    } catch (error) {
      console.error("❌ Erreur lors de l'envoi:", error);
      alert(`❌ Impossible d'envoyer le message : ${error.message}. Veuillez réessayer.`);
    } finally {
      setIsSending(false);
    }
  };

  return (
    <div>
      {/* Indicateur de connexion
      <div className="text-sm flex items-center gap-2 bg-white">
        {isConnected ? (
          <>
            <span className="text-green-600 font-semibold">🟢 Connecté</span>
            <span className="text-gray-500 text-xs">Prêt à envoyer des messages</span>
          </>
        ) : (
          <>
            <span className="text-red-600 font-semibold">🔴 Déconnecté</span>
            <span className="text-gray-500 text-xs">
              {!jwtToken ? "JWT manquant" : "Connexion en cours..."}
            </span>
          </>
        )}
      </div> */}
      <div className="flex flex-col items-center justify-center">
        <form onSubmit={handleSubmit} className="relative flex w-[80%] md:w-[65%]">
          <textarea
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            rows={4}
            placeholder={
              isSending
                ? "Envoi en cours..."
                : attachment
                ? "Message optionnel avec le fichier..."
                : "Entrez votre message ici..."
            }
            className="flex-1 rounded-lg border-solid border-silver text-blue px-4 py-2 pr-16 text-sm outline-none focus:border-blue disabled:bg-silver disabled:text-gray-500 resize-none font-cabin"
            disabled={isSending}
          />
          
          <div className="absolute items-center justify-center -right-6 top-0 -translate-y-1/2 flex flex-col gap-1">
            <label className={`cursor-pointer ${isSending ? "opacity-50" : ""}`}>
              <input
                type="file"
                accept="image/png,image/jpeg,application/pdf"
                className="hidden"
                onChange={(e) => setAttachment(e.target.files?.[0] || null)}
                disabled={isSending}
              />
              <div className="flex items-center justify-center bg-blue border-gold rounded-full border-solid w-[36px] h-[36px]">
                <Image
                  src="/attachment-icon.png"
                  alt="Joindre un fichier"
                  width={24}
                  height={24}
                />
              </div>
            </label>
            
            <button
              type="submit"
              disabled={isSending || (!message.trim() && !attachment)}
              className={`flex items-center justify-center rounded-full border-solid w-[50px] h-[50px] transition-colors
                ${
                  isSending || (!message.trim() && !attachment)
                    ? "bg-white border-silver cursor-not-allowed"
                    : "bg-blue border-gold cursor-pointer"
                }`}
            >
              <Image
                src={
                  isSending || (!message.trim() && !attachment)
                    ? "/send-silver.png"
                    : "/send-gold.png"
                }
                alt="Envoyer"
                width={28}
                height={28}
              />
            </button>
          </div>
        </form>


      {attachment && (
        <div className="mt-2 text-sm text-gray-500 flex items-center gap-2">
          <span className="">Fichier sélectionné: {attachment.name}</span>
          <button
            type="button"
            onClick={() => setAttachment(null)}
            className="text-gold text-xs bg-blue h-5 w-5 rounded-full"
            disabled={isSending}
          >
            &times;
          </button>
        </div>
      )}
      </div>
      <div ref={messagesEndRef} />
    </div>
  );
}