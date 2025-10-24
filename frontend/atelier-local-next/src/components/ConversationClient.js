/**
 * Composant ConversationClient
 * 
 * Gestion complète d'une conversation client-artisan avec :
 * - Affichage des messages existants
 * - Envoi de messages texte et fichiers
 * - Connexion WebSocket pour réception en temps réel
 * - Marquage automatique des messages comme lus
 * 
 * Props :
 * - initialMessages : liste initiale des messages de la conversation
 * - user : utilisateur courant
 * - otherUser : destinataire de la conversation
 * - otherUserName : nom à afficher pour l'autre utilisateur
 * - jwtToken : token d'authentification pour API et WebSocket
 */

"use client";
import { useState, useEffect } from "react";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import MessagesList from "./MessageList";
import MessageForm from "./MessageForm";
import { markConversationAsRead } from "@/lib/messageService";
import getApiUrl from "@/lib/api";

export default function ConversationClient({ initialMessages, user, otherUser, otherUserName, jwtToken }) {
  const [messages, setMessages] = useState(initialMessages || []);
  const [stompClient, setStompClient] = useState(null);

  // Marque tous les messages comme lus au montage du composant
  useEffect(() => {
    if (messages.length > 0 && user?.id) {
      markConversationAsRead(messages, user.id, jwtToken);
    }
  }, []); // Seulement au montage

  // Connexion et gestion WebSocket
  useEffect(() => {
    const socket = new SockJS(`${getApiUrl()}/ws`);
    const client = Stomp.over(() => socket);

    client.connect(
      { Authorization: `Bearer ${jwtToken}` },
      () => {

        // Abonnement aux messages entrants
        client.subscribe("/user/queue/messages", (message) => {
          const received = JSON.parse(message.body);

          setMessages((prev) => {
            // Cas 1 : Message temporaire (envoyé par nous mais pas encore confirmé)
            if (received.tempId) {
              const tempIndex = prev.findIndex((m) => m.tempId === received.tempId);
              if (tempIndex !== -1) {
                const updated = [...prev];
                const finalTimestamp = received.createdAt || received.timestamp || prev[tempIndex].timestamp || new Date().toISOString();
                updated[tempIndex] = {
                  ...received,
                  messageStatus: "DELIVERED",
                  timestamp: finalTimestamp,
                  createdAt: finalTimestamp,
                };
                return updated;
              }
            }

            // Cas 2 : Nouveau message d'un autre utilisateur
            const exists = prev.some((m) => m.id === received.id);
            if (exists) return prev; // Ignore les doublons

            // Marquer le message comme lu si c'est pour l'utilisateur courant
            if (received.receiverId === user.id && received.id) {
              fetch(`${getApiUrl()}/api/messages/${received.id}/read`, {
                method: "POST",
                headers: {
                  "Content-Type": "application/json",
                  ...(jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}),
                },
                credentials: "include",
              }).catch(err => console.error("Erreur marquage lu:", err));
            }

            return [...prev, received];
          });
        });

        setStompClient(client);
      },
      (error) => {
        console.error("Erreur WebSocket:", error);
      }
    );

    // Déconnexion lors du démontage du composant
    return () => {
      if (client && client.connected) {
        client.disconnect();
      }
    };
  }, [jwtToken, user.id]);

  return (
    <>
      {/* Liste des messages */}
      <MessagesList
        initialMessages={messages}
        user={user}
        otherUser={otherUser}
        otherUserName={otherUserName}
      />

      {/* Formulaire d'envoi de messages */}
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
