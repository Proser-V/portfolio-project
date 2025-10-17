"use client";
import { useState, useEffect, useRef } from "react";
import { motion } from "framer-motion";

export default function MessagesList({ initialMessages, userId, otherUserId, otherUserName }) {
  const [messages, setMessages] = useState(initialMessages || []);
  const messagesEndRef = useRef(null);

  // Mettre à jour l'état des messages lorsque initialMessages change
  useEffect(() => {
    setMessages(initialMessages || []);
  }, [initialMessages]);

  // Faire défiler automatiquement vers le dernier message
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  return (
    <div className="bg-white border border-gray-200 rounded-xl shadow-lg p-4 mb-4 h-[60vh] overflow-y-auto">
      {messages.length === 0 ? (
        <p className="text-center text-gray-400 text-sm py-4">
          Aucun message dans cette conversation.
        </p>
      ) : (
        messages
          .sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime())
          .map((msg, index) => {
            const isSentByUser = msg.senderId.toString() === userId.toString();

            let messageDate = "Date inconnue";
            if (msg.timestamp) {
              const parsedDate = new Date(msg.timestamp.replace(" ", "T"));
              if (!isNaN(parsedDate.getTime())) {
                messageDate = parsedDate.toLocaleString("fr-FR", {
                  day: "2-digit",
                  month: "2-digit",
                  year: "numeric",
                  hour: "2-digit",
                  minute: "2-digit",
                });
              }
            }

            return (
              // Utiliser motion.div pour l'animation
              <motion.div
                key={msg.id || index} // Fallback sur index si id est absent
                className={`flex ${isSentByUser ? "justify-end" : "justify-start"} mb-4`}
                initial={{ opacity: 0, y: 20 }} // Animation d'entrée
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3, ease: "easeOut" }}
              >
                {!isSentByUser && (
                  <img
                    src={`/avatars/${otherUserId}.jpg`}
                    alt={otherUserName}
                    className="w-10 h-10 rounded-full mr-2"
                  />
                )}
                <div
                  className={`max-w-[70%] p-3 rounded-lg ${
                    isSentByUser ? "bg-blue text-white" : "bg-yellow-100 text-gray-800"
                  }`}
                >
                  <p className="text-sm">
                    {msg.attachments && msg.attachments.length > 0 ? (
                      msg.attachments.map((attachment, index) => (
                        <div key={index} className="mb-1">
                          <a
                            href={attachment.fileUrl}
                            className="text-blue-500 underline"
                            target="_blank"
                            rel="noopener noreferrer"
                          >
                            Pièce jointe: {attachment.fileUrl.split("/").pop() || "Fichier inconnu"}
                          </a>
                        </div>
                      ))
                    ) : (
                      msg.content
                    )}
                  </p>
                  <p
                    className={`text-xs mt-1 ${isSentByUser ? "text-gray-300" : "text-gray-500"}`}
                  >
                    {messageDate}
                  </p>
                </div>
                {isSentByUser && (
                  <img
                    src={`/avatars/${userId}.jpg`}
                    alt="Vous"
                    className="w-10 h-10 rounded-full ml-2"
                  />
                )}
              </motion.div>
            );
          })
      )}
      <div ref={messagesEndRef} />
    </div>
  );
}