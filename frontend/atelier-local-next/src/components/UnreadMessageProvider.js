"use client";
// Directive Next.js : indique que ce composant s’exécute côté client
// (nécessaire car il utilise des hooks React et interagit avec des WebSockets)

import { createContext, useContext, useState, useEffect } from "react";
import { getUnreadMessages, getTotalUnreadCount, markMessageAsRead } from "@/lib/messageService";
import { connectWebSocket, disconnectWebSocket } from "@/lib/websocket";

/**
 * Contexte global pour la gestion des messages non lus.
 *
 * Fournit :
 * - La liste des messages non lus
 * - Le nombre total de messages non lus
 * - Des fonctions pour rafraîchir les messages et marquer une conversation comme lue
 */
const UnreadContext = createContext();

/**
 * Provider du contexte des messages non lus.
 *
 * @param {Object} props
 * @param {React.ReactNode} props.children - Composants enfants utilisant le contexte
 * @param {string} props.jwtToken - Token JWT de l’utilisateur pour l’authentification
 * @param {string|number} props.currentUserId - ID de l’utilisateur courant
 * @param {Array} props.initialUnreadMessages - Messages non lus initiaux (optionnel)
 *
 * Ce composant :
 * - Initialise les messages non lus et leur compteur
 * - Connecte un WebSocket pour recevoir les nouveaux messages en temps réel
 * - Permet de marquer les conversations comme lues localement
 * - Rafraîchit périodiquement les messages non lus depuis le serveur
 */
export function UnreadMessagesProvider({ children, jwtToken, currentUserId, initialUnreadMessages = [] }) {
  // État local des messages non lus
  const [unreadMessages, setUnreadMessages] = useState(initialUnreadMessages);
  // État local du nombre total de messages non lus
  const [unreadCount, setUnreadCount] = useState(0);
  // ID de la conversation actuellement ouverte
  const [activeConversationId, setActiveConversationId] = useState(null);

  /**
   * Rafraîchit les messages non lus depuis le serveur.
   * Met à jour la liste et le compteur localement.
   */
  async function refreshUnread() {
    if (!jwtToken || !currentUserId) return;
    const messages = await getUnreadMessages(jwtToken);
    const count = getTotalUnreadCount(messages, currentUserId);
    setUnreadMessages(messages);
    setUnreadCount(count);
  }

  /**
   * Marque localement une conversation comme lue.
   *
   * @param {string|number} otherUserId - ID de l’autre utilisateur dans la conversation
   *
   * Supprime les messages provenant de cet utilisateur et met à jour le compteur.
   */
  function markConversationAsReadLocally(otherUserId) {
    setUnreadMessages(prev => {
      const updated = prev.filter(msg => msg.senderId !== otherUserId);
      setUnreadCount(updated.length);
      return updated;
    });
  }

  /**
   * Effet React pour gérer la connexion WebSocket.
   * - Reçoit les nouveaux messages en temps réel
   * - Ignore les messages envoyés par l’utilisateur courant
   * - Incrémente le compteur uniquement si la conversation n’est pas active
   */
  useEffect(() => {
    if (!jwtToken || !currentUserId) return;

    const client = connectWebSocket(jwtToken, (messageData) => {
      if (messageData.senderId === currentUserId) return;

      setUnreadMessages(prev => [...prev, messageData]);
      setUnreadCount(prev => {
        if (messageData.senderId === activeConversationId) return prev;
        return prev + 1;
      });
    });

    // Nettoyage : déconnexion du WebSocket à la destruction du composant
    return () => disconnectWebSocket();
  }, [jwtToken, currentUserId, activeConversationId]);

  /**
   * Effet React pour rafraîchir périodiquement les messages non lus depuis le serveur.
   * Intervalle de 30 secondes.
   */
  useEffect(() => {
    refreshUnread();
    const interval = setInterval(refreshUnread, 30000);
    return () => clearInterval(interval);
  }, [jwtToken, currentUserId]);

  return (
    <UnreadContext.Provider
      value={{
        unreadCount,
        unreadMessages,
        refreshUnread,
        markConversationAsReadLocally,
        activeConversationId,
        setActiveConversationId
      }}
    >
      {children}
    </UnreadContext.Provider>
  );
}

/**
 * Hook personnalisé pour accéder facilement au contexte des messages non lus.
 *
 * @returns {Object} - Contient la liste des messages non lus, le compteur, et les fonctions associées
 * @throws {Error} si utilisé en dehors d’un `UnreadMessagesProvider`
 */
export function useUnreadMessages() {
  const context = useContext(UnreadContext);
  if (!context) throw new Error("useUnreadMessages must be used within UnreadMessagesProvider");
  return context;
}
