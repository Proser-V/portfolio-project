"use client";
import { useEffect, useRef } from "react";
import { motion } from "framer-motion";

export default function MessagesList({ initialMessages, user, otherUser, otherUserName }) {
  const messages = initialMessages || [];
  const messagesEndRef = useRef(null);
  const userId = user?.id;

  // Auto-scroll
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // Filtrer et trier les messages
  const validMessages = messages
    .filter(msg => (msg.timestamp || msg.createdAt) && !isNaN(new Date(msg.timestamp || msg.createdAt).getTime()))
    .sort((a, b) => {
      const dateA = new Date(a.timestamp || a.createdAt);
      const dateB = new Date(b.timestamp || b.createdAt);
      return dateA.getTime() - dateB.getTime();
    });

  return (
    <div className="relative h-[60vh] overflow-y-auto scrollbar-hidden">
      <div
        className="sticky top-0 left-0 right-0 w-full h-20 z-20 pointer-events-none"
        style={{
          background: "linear-gradient(to bottom, rgba(255,255,255,1) 0%, rgba(255,255,255,0.8) 50%, rgba(255,255,255,0) 100%)",
        }}
      ></div>

      {validMessages.length === 0 ? (
        <p className="text-center text-gray-400 text-sm py-4">
          Aucun message dans cette conversation.
        </p>
      ) : (
        validMessages.map((msg, index) => {
          const isSentByUser = msg.senderId.toString() === userId.toString();
          const msgTime = msg.timestamp || msg.createdAt;
          let messageDate = "Envoi en cours...";

          if (msgTime && msg.messageStatus !== "SENDING") {
            let parsedDate;
          
            if (typeof msgTime === "string") {
              // Ajouter 'Z' si le timestamp ne contient pas d'information de fuseau horaire
              parsedDate = new Date(msgTime.includes("Z") || msgTime.includes("+") ? msgTime : `${msgTime}Z`);
            } else {
              parsedDate = new Date(msgTime); // Si msgTime est un objet Date
            }
          
            if (!isNaN(parsedDate.getTime())) {
              messageDate = parsedDate.toLocaleString("fr-FR", {
                timeZone: "Europe/Paris",
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
              });
            }
          }

          const statusIndicator =
            msg.messageStatus === "SENDING" ? "⏳" : msg.messageStatus === "NOT_SENT" ? "❌" : "";

          return (
            <motion.div
              key={msg.id || msg.tempId || `${msg.senderId}-${msg.content}-${index}`}
              className={`flex ${isSentByUser ? "justify-end" : "justify-start"} mb-2 items-end`}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3, ease: "easeOut" }}
            >
              {/* Avatar gauche */}
              {!isSentByUser && otherUser?.avatar?.url && (
                <img
                  src={otherUser?.avatar?.url}
                  alt={otherUserName}
                  className="w-10 h-10 rounded-full mr-2 object-cover"
                />
              )}

              {/* Bulle + date */}
              <div className="flex flex-col w-[45%] relative">
                <div
                  className={`py-2 px-2 w-[100%] min-h-[50px] rounded-lg break-words ${
                    isSentByUser ? "bg-blue text-gold self-end" : "bg-gold text-blue self-start"
                  } ${msg.messageStatus === "NOT_SENT" ? "opacity-50" : ""}`}
                >
                  <div className="text-sm">
                    {msg.attachments && msg.attachments.length > 0 ? (
                      msg.attachments.map((attachment, idx) => (
                        <div key={idx} className="mb-1">
                          <a
                            href={attachment.fileUrl}
                            className="underline"
                            target="_blank"
                            rel="noopener noreferrer"
                          >
                            Pièce jointe: {attachment.fileUrl.split("/").pop() || "Fichier"}
                          </a>
                        </div>
                      ))
                    ) : (
                      msg.content
                    )}
                  </div>

                  {/* Triangle de bulle */}
                  <div
                    className={`absolute bottom-8 w-3 h-3 ${
                      isSentByUser
                        ? "bg-blue -right-0 translate-x-1/2 rotate-45"
                        : "bg-gold -left-0 -translate-x-1/2 rotate-45"
                    }`}
                  ></div>
                </div>

                {/* Date */}
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
                    className="w-10 h-10 rounded-full ml-2 object-cover"
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