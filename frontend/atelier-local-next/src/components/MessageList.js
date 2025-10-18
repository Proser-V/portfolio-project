"use client";
import { useState, useEffect, useRef } from "react";
import { motion } from "framer-motion";

export default function MessagesList({ initialMessages, user, otherUser, otherUserName }) {
  const [messages, setMessages] = useState(initialMessages || []);
  const messagesEndRef = useRef(null);
  const userId = user?.id;
  const otherUserId = otherUser?.id;

  console.log("user", user);
  console.log("otherUser", otherUser);

  // Mettre à jour l'état des messages lorsque initialMessages change
  useEffect(() => {
    setMessages(initialMessages || []);
  }, [initialMessages]);

  // Faire défiler automatiquement vers le dernier message
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  return (
    <div className="relative h-[60vh] overflow-y-auto scrollbar-hidden">
      <div className="sticky top-0 left-0 right-0 w-full h-20 z-20 pointer-events-none" 
         style={{
           background: 'linear-gradient(to bottom, rgba(255,255,255,1) 0%, rgba(255,255,255,0.8) 50%, rgba(255,255,255,0) 100%)'
         }}>
      </div>

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
                key={msg.id || index}
                className={`flex ${isSentByUser ? "justify-end" : "justify-start"} mb-6 items-end`}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3, ease: "easeOut" }}
              >
                {/* Avatar gauche */}
                {!isSentByUser && (
                  <img
                    src={otherUser?.avatar?.url}
                    alt={otherUserName}
                    className="w-10 h-10 rounded-full mr-2"
                  />
                )}

                {/* Bulle + date */}
                <div className="flex flex-col w-[45%] relative">
                  <div className={`py-2 px-2 w-[100%] min-h-[50px] rounded-lg break-words
                    ${isSentByUser
                    ? "bg-blue text-gold self-end"
                    : "bg-gold text-blue self-start"
                    }`}>
                    <div className="text-sm">
                      {msg.attachments && msg.attachments.length > 0 ? (
                        msg.attachments.map((attachment, index) => (
                          <div key={index} className="mb-1">
                            <a
                              href={attachment.fileUrl}
                              className="underline"
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
                    </div>

                    {/* Triangle de bulle */}
                    <div
                      className={`absolute bottom-8 w-3 h-3
                        ${isSentByUser 
                          ? "bg-blue -right-0 translate-x-1/2 rotate-45"
                          : "bg-gold -left-0 -translate-x-1/2 rotate-45"
                        }`}
                    ></div>
                  </div>

                  {/* Date alignée à la fin de la bulle */}
                  <p className={`text-xs text-silver mt-1
                      ${isSentByUser
                        ? "self-start"
                        : "self-end"
                    }`}
                  >
                    {messageDate}
                  </p>
                </div>

                {/* Avatar droit */}
                {isSentByUser && (
                  <img
                    src={user?.avatar?.url}
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