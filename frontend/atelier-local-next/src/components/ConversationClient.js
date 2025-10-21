"use client";
import { useState, useEffect } from "react";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import MessagesList from "./MessageList";
import MessageForm from "./MessageForm";

export default function ConversationClient({ initialMessages, user, otherUser, otherUserName, jwtToken }) {
  const [messages, setMessages] = useState(initialMessages || []);
  const [stompClient, setStompClient] = useState(null);

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
              const tempIndex = prev.findIndex((m) => m.tempId === received.tempId);
              console.log("🔍 Index du message temporaire:", tempIndex);

              if (tempIndex !== -1) {
                // Remplacer le message temporaire par le vrai
                const updated = [...prev];
                const finalTimestamp = received.createdAt || received.timestamp || prev[tempIndex].timestamp || new Date().toISOString();
                updated[tempIndex] = {
                  ...received,
                  messageStatus: "DELIVERED",
                  timestamp: finalTimestamp,
                  createdAt: finalTimestamp,
                };
                console.log("✅ Message temporaire remplacé:", updated[tempIndex]);
                console.log("✅ Timestamp final:", finalTimestamp);
                return updated;
              }
            }

            // Cas 2 : Nouveau message reçu d'un autre utilisateur
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
        console.log("🔌 WebSocket déconnecté");
      }
    };
  }, [jwtToken]);

  return (
    <>
      <MessagesList
        initialMessages={messages}
        user={user}
        otherUser={otherUser}
        otherUserName={otherUserName}
      />
      <MessageForm
        user={user}
        otherUser={otherUser}
        jwtToken={jwtToken}
        messages={messages}
        setMessages={setMessages}
        stompClient={stompClient}
      />
    </>
  );
}