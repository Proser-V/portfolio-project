"use client";
import { useState, useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default function MessageForm({ userId, otherUserId, jwtToken, messages, setMessages }) {
  const [message, setMessage] = useState("");
  const [attachment, setAttachment] = useState(null);
  const [stompClient, setStompClient] = useState(null);
  const [isConnected, setIsConnected] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const messagesEndRef = useRef(null); // Référence pour le défilement

  // Faire défiler automatiquement vers le dernier message
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  useEffect(() => {
    if (!jwtToken) {
      console.error("❌ JWT non fourni au composant");
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
      {/* Indicateur de connexion */}
      <div className="mb-2 text-sm flex items-center gap-2">
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
      </div>

      <form onSubmit={handleSubmit} className="flex items-center gap-2">
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          placeholder={
            isSending
              ? "Envoi en cours..."
              : attachment
              ? "Message optionnel avec le fichier..."
              : "Entrez votre message ici..."
          }
          className="flex-1 rounded-full border border-gray-300 px-4 py-2 text-sm outline-none focus:border-blue-500 disabled:bg-gray-100"
          disabled={isSending}
        />
        <label className={`cursor-pointer ${isSending ? "opacity-50" : ""}`}>
          <input
            type="file"
            accept="image/png,image/jpeg,application/pdf"
            className="hidden"
            onChange={(e) => setAttachment(e.target.files?.[0] || null)}
            disabled={isSending}
          />
          <span className="text-blue-900 text-xl">📎</span>
        </label>
        <button
          type="submit"
          className="text-white bg-blue-900 rounded-full px-4 py-2 hover:bg-blue-800 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
          disabled={isSending || (!message.trim() && !attachment)}
        >
          {isSending ? "Envoi..." : "Envoyer"}
        </button>
      </form>

      {attachment && (
        <div className="mt-2 text-sm text-gray-600 flex items-center gap-2">
          <span>📎 Fichier sélectionné: {attachment.name}</span>
          <button
            type="button"
            onClick={() => setAttachment(null)}
            className="text-red-500 hover:text-red-700 text-xs"
            disabled={isSending}
          >
            ✕ Supprimer
          </button>
        </div>
      )}
      <div ref={messagesEndRef} />
    </div>
  );
}