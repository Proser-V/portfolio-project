"use client";
import { createContext, useContext, useState, useEffect } from "react";
import { getUnreadMessages, getTotalUnreadCount, markMessageAsRead } from "@/lib/messageService";
import { connectWebSocket, disconnectWebSocket } from "@/lib/websocket";

const UnreadContext = createContext();

export function UnreadMessagesProvider({ children, jwtToken, currentUserId, initialUnreadMessages = [] }) {
  const [unreadMessages, setUnreadMessages] = useState(initialUnreadMessages);
  const [unreadCount, setUnreadCount] = useState(0);
  const [activeConversationId, setActiveConversationId] = useState(null);

  async function refreshUnread() {
    if (!jwtToken || !currentUserId) return;
    const messages = await getUnreadMessages(jwtToken);
    const count = getTotalUnreadCount(messages, currentUserId);
    setUnreadMessages(messages);
    setUnreadCount(count);
  }

  function markConversationAsReadLocally(otherUserId) {
    setUnreadMessages(prev => {
      const updated = prev.filter(msg => msg.senderId !== otherUserId);
      setUnreadCount(updated.length);
      return updated;
    });
  }

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
    return () => disconnectWebSocket();
  }, [jwtToken, currentUserId, activeConversationId]);

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

export function useUnreadMessages() {
  const context = useContext(UnreadContext);
  if (!context) throw new Error("useUnreadMessages must be used within UnreadMessagesProvider");
  return context;
}
