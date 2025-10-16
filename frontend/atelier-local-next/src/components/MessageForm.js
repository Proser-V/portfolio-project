"use client";
import { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default function MessageForm({ userId, otherUserId, jwtToken }) {
  const [message, setMessage] = useState("");
  const [attachment, setAttachment] = useState(null);
  const [stompClient, setStompClient] = useState(null);
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    if (!jwtToken) {
      console.error("❌ JWT non fourni au composant");
      return;
    }

    const wsUrl = `${process.env.NEXT_PUBLIC_API_URL}/ws`;
    console.log("🔌 Tentative de connexion WebSocket à:", wsUrl);
    console.log("🔑 JWT reçu:", jwtToken ? "Oui" : "Non");

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

        // S'abonner aux messages entrants
        client.subscribe("/user/queue/messages", (msg) => {
          console.log("📩 Nouveau message reçu:", msg.body);
          try {
            const newMessage = JSON.parse(msg.body);
            console.log("Message parsé:", newMessage);
            
            // Recharger la page pour afficher le nouveau message
            setTimeout(() => {
              window.location.reload();
            }, 300);
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
        // Désactiver les logs trop verbeux
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
  }, [jwtToken]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!message.trim() && !attachment) {
      alert("⚠️ Veuillez entrer un message");
      return;
    }

    if (!stompClient || !isConnected) {
      alert("❌ Connexion WebSocket non établie. Veuillez rafraîchir la page.");
      console.error("État de connexion:", { 
        stompClient: !!stompClient, 
        isConnected,
        clientConnected: stompClient?.connected 
      });
      return;
    }

    const messageData = {
      senderId: userId,
      receiverId: otherUserId,
      content: message.trim(),
      timestamp: new Date().toISOString(),
    };

    try {
      console.log("📤 Envoi du message:", messageData);
      
      stompClient.publish({
        destination: "/app/chat",
        body: JSON.stringify(messageData),
      });

      console.log("✅ Message envoyé avec succès");
      
      // Réinitialiser le formulaire
      setMessage("");
      setAttachment(null);
      
      // Recharger la page pour afficher le message envoyé
      setTimeout(() => {
        window.location.reload();
      }, 500);
      
    } catch (error) {
      console.error("❌ Erreur lors de l'envoi:", error);
      alert("❌ Erreur lors de l'envoi du message: " + error.message);
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
          placeholder={isConnected ? "Entrez votre message ici..." : "Connexion en cours..."}
          className="flex-1 rounded-full border border-gray-300 px-4 py-2 text-sm outline-none focus:border-blue-500 disabled:bg-gray-100"
          disabled={!isConnected}
        />
        <label className={`cursor-pointer ${!isConnected ? 'opacity-50' : ''}`}>
          <input
            type="file"
            accept=".pdf"
            className="hidden"
            onChange={(e) => setAttachment(e.target.files?.[0] || null)}
            disabled={!isConnected}
          />
          <span className="text-blue-900 text-xl">📎</span>
        </label>
        <button
          type="submit"
          className="text-white bg-blue-900 rounded-full px-4 py-2 hover:bg-blue-800 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
          disabled={!isConnected}
        >
          Envoyer
        </button>
      </form>

      {attachment && (
        <div className="mt-2 text-sm text-gray-600">
          📎 Fichier sélectionné: {attachment.name}
        </div>
      )}
    </div>
  );
}