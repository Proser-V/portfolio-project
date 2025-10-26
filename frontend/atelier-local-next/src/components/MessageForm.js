"use client";
// Directive Next.js : ce composant utilise state, refs et hooks, il doit être côté client

import { useState, useEffect, useRef } from "react";
import Image from "next/image";
import getApiUrl from "@/lib/api";

/**
 * Formulaire d'envoi de messages dans une conversation.
 *
 * Fonctionnalités :
 * - Envoi de messages texte via WebSocket (STOMP)
 * - Envoi de messages avec pièce jointe via API REST
 * - Gestion de l'état d'envoi (isSending)
 * - Affichage du message et du fichier attaché
 * - Boutons dynamiques désactivés selon le contexte
 * - Auto-scroll vers le bas lors de l'ajout de messages
 *
 * Props :
 * @param {Object} user - Utilisateur courant
 * @param {Object} otherUser - Utilisateur destinataire
 * @param {string} jwtToken - Token JWT pour authentification REST
 * @param {Array} messages - Liste des messages actuels
 * @param {Function} setMessages - Setter pour mettre à jour la liste des messages
 * @param {Object} stompClient - Client WebSocket STOMP
 */
export default function MessageForm({ user, otherUser, jwtToken, messages, setMessages, stompClient }) {
  const [message, setMessage] = useState(""); // Contenu texte du message
  const [attachment, setAttachment] = useState(null); // Fichier attaché
  const [isSending, setIsSending] = useState(false); // Indique si l'envoi est en cours
  const messagesEndRef = useRef(null); // Ref pour auto-scroll
  const fileInputRef = useRef(null); // Ref pour réinitialiser l'input file
  const userId = user?.id;
  const otherUserId = otherUser?.id;

  // Auto-scroll vers le bas lorsque la liste des messages change
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  /**
   * Envoi du message
   * - Texte seul via WebSocket
   * - Avec pièce jointe via REST API
   */
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Vérification que le message ou le fichier existe
    if (!message.trim() && !attachment) {
      alert("Veuillez entrer un message ou joindre un fichier");
      return;
    }

    setIsSending(true);

    try {
      // Envoi avec pièce jointe via REST
      if (attachment) {
        console.log("📎 Envoi via API REST (fichier présent)");
        const formData = new FormData();
        formData.append("receiverId", otherUserId);
        formData.append("content", message.trim() || "Fichier joint");
        formData.append("file", attachment);

        const response = await fetch(`${getApiUrl()}/api/messages`, {
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
        console.log("Message avec fichier envoyé via REST");
      } else {
        // Envoi texte via WebSocket
        if (!stompClient || !stompClient.connected) {
          alert("Connexion WebSocket non établie. Veuillez rafraîchir la page.");
          console.error("État de connexion:", {
            stompClient: !!stompClient,
            isConnected: stompClient?.connected,
          });
          setIsSending(false);
          return;
        }

        console.log("Envoi via WebSocket (texte uniquement)");
        const messageData = {
          senderId: userId,
          receiverId: otherUserId,
          content: message.trim(),
          timestamp: new Date().toISOString(),
          tempId: `temp-${Date.now()}`, // ID temporaire pour affichage immédiat
        };

        stompClient.publish({
          destination: "/app/chat",
          body: JSON.stringify(messageData),
        });

        setMessages((prev) => [...prev, messageData]);
        console.log("Message texte envoyé via WebSocket");
      }

      // Réinitialisation du formulaire
      setMessage("");
      setAttachment(null);
      if (fileInputRef.current) {
        fileInputRef.current.value = ""; // Reset de l'input file
      }
    } catch (error) {
      console.error("Erreur lors de l'envoi:", error);
      alert(`Impossible d'envoyer le message : ${error.message}. Veuillez réessayer.`);
    } finally {
      setIsSending(false);
    }
  };

  // Annule l'ajout de la pièce jointe
  const handleCancelAttachment = () => {
    setAttachment(null);
    if (fileInputRef.current) {
      fileInputRef.current.value = ""; // Reset de l'input file
    }
  };

  return (
    <div>
      <div className="flex flex-col items-center justify-center">
        {/* Formulaire d'envoi */}
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
            disabled={isSending || attachment} // désactive si envoi ou fichier
          />
          <div className="absolute items-center justify-center -right-6 top-0 -translate-y-1/2 flex flex-col gap-1">
            {/* Bouton ajouter pièce jointe */}
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

            {/* Bouton envoyer */}
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

        {/* Affichage du fichier attaché */}
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

      {/* Ref pour auto-scroll */}
      <div ref={messagesEndRef} />
    </div>
  );
}
