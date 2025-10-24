"use client";
import { useState, useEffect, useRef } from "react";
import Image from "next/image";

export default function MessageForm({ user, otherUser, jwtToken, messages, setMessages, stompClient }) {
  const [message, setMessage] = useState("");
  const [attachment, setAttachment] = useState(null);
  const [isSending, setIsSending] = useState(false);
  const messagesEndRef = useRef(null);
  const fileInputRef = useRef(null);
  const userId = user?.id;
  const otherUserId = otherUser?.id;

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

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
        if (!stompClient || !stompClient.connected) {
          alert("❌ Connexion WebSocket non établie. Veuillez rafraîchir la page.");
          console.error("État de connexion:", {
            stompClient: !!stompClient,
            isConnected: stompClient?.connected,
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
          tempId: `temp-${Date.now()}`,
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
      if (fileInputRef.current) {
        fileInputRef.current.value = ""; // Réinitialise l'input file
      }
    } catch (error) {
      console.error("❌ Erreur lors de l'envoi:", error);
      alert(`❌ Impossible d'envoyer le message : ${error.message}. Veuillez réessayer.`);
    } finally {
      setIsSending(false);
    }
  };

  const handleCancelAttachment = () => {
    setAttachment(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = ""; // Réinitialise l'input
    }
  };

  return (
    <div>
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
            disabled={isSending || attachment}
          />
          <div className="absolute items-center justify-center -right-6 top-0 -translate-y-1/2 flex flex-col gap-1">
            <label
              className={`flex items-center justify-center rounded-full border-solid w-[36px] h-[36px] transition-colors
                ${
                  isSending || message.trim()
                    ? "bg-white border-silver cursor-not-allowed"
                    : "bg-blue border-gold cursor-pointer"
                }`}
            >
              <input
                ref={fileInputRef}
                type="file"
                accept="image/png,image/jpeg,application/pdf"
                className="hidden"
                onChange={(e) => setAttachment(e.target.files?.[0] || null)}
                disabled={isSending || message.trim()}
              />
              <Image
                src={isSending || message.trim() ? "/attachment-silver.png" : "/attachment-gold.png"}
                alt="Joindre un fichier"
                width={24}
                height={24}
              />
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
            <span>Fichier sélectionné: {attachment.name}</span>
            <button
              type="button"
              onClick={handleCancelAttachment}
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