"use client";
// Directive Next.js : ce composant est côté client car il utilise des hooks et state

import { useState, useMemo, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { countUnreadByUser, getUnreadMessages, markConversationAsReadLocally } from "@/lib/messageService";
import { useUnreadMessages } from "./UnreadMessageProvider";

/**
 * Liste des conversations pour le messenger.
 *
 * Permet :
 * - Filtrage par nom
 * - Tri par date (asc/desc)
 * - Pagination
 * - Gestion des messages non lus
 *
 * Props :
 * @param {Array} initialConversations - Liste initiale de toutes les conversations
 * @param {number} conversationsPerPage - Nombre de conversations par page (défaut : 10)
 * @param {string|number} currentUserId - ID de l'utilisateur courant
 * @param {Array} initialUnreadMessages - Liste initiale des messages non lus
 */
export default function MessengerList({
  initialConversations,
  conversationsPerPage = 10,
  currentUserId,
  initialUnreadMessages = []
}) {
  const router = useRouter();

  // États locaux pour recherche, tri et pagination
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState("desc");
  const [currentPage, setCurrentPage] = useState(1);

  // Hook global pour les messages non lus
  const { unreadMessages, refreshUnread, markConversationAsReadLocally } = useUnreadMessages();

  // Comptage des messages non lus par utilisateur
  const unreadCounts = countUnreadByUser(unreadMessages, currentUserId);

  /**
   * Filtrage et tri des conversations côté client
   * - Filtre par nom si search non vide
   * - Tri par dernière date (asc/desc)
   */
  const filteredConversations = useMemo(() => {
    let convs = initialConversations.filter(conv => conv.lastTimestamp);
    if (search) {
      convs = convs.filter(c =>
        c.otherUserName.toLowerCase().includes(search.toLowerCase())
      );
    }
    convs.sort((a, b) => {
      const da = new Date(a.lastTimestamp);
      const db = new Date(b.lastTimestamp);
      return sort === "asc" ? da - db : db - da;
    });
    return convs;
  }, [initialConversations, search, sort]);

  // Pagination
  const totalPages = Math.ceil(filteredConversations.length / conversationsPerPage);
  const startIndex = (currentPage - 1) * conversationsPerPage;
  const paginatedConversations = filteredConversations.slice(
    startIndex,
    startIndex + conversationsPerPage
  );

  // Gestion du changement de page
  const handlePageChange = (page) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  /**
   * Gestion du clic sur une conversation :
   * - Marque localement les messages comme lus
   * - Si token JWT présent, marque les messages non lus côté serveur
   * - Redirection vers la conversation
   */
  async function handleConversationClick(conv) {
    markConversationAsReadLocally(conv.otherUserId);

    const jwtToken = document.cookie
      .split('; ')
      .find(row => row.startsWith('jwt='))
      ?.split('=')[1];

    if (!jwtToken) {
      router.push(`/messenger/${conv.otherUserId}`);
      return;
    }

    const unreadInConv = unreadMessages.filter(
      msg => msg.senderId === conv.otherUserId && msg.receiverId === currentUserId
    );

    if (unreadInConv.length > 0 && jwtToken) {
      await Promise.all(unreadInConv.map(msg => markMessageAsRead(msg.id, jwtToken)));
      refreshUnread();
    }

    router.push(`/messenger/${conv.otherUserId}`);
  }

  return (
    <div className="py-8">
      <main className="max-w-xs md:max-w-4xl mx-auto px-4">
        {/* Barre de recherche et tri */}
        <p className="text-sm text-center mt-0">Filtrer vos messages par</p>
        <div className="flex flex-col md:flex-row items-center justify-center gap-4 mb-8">
          {/* Recherche par nom */}
          <div className="flex items-center gap-2">
            <label className="text-blue text-sm">Nom :</label>
            <input
              type="text"
              value={search}
              onChange={e => {
                setSearch(e.target.value);
                setCurrentPage(1);
              }}
              placeholder=""
              className="rounded-full border border-silver px-4 py-2 text-sm w-48 outline-none focus:border-blue focus:ring-1 focus:ring-blue"
            />
          </div>
          
          {/* Sélecteur de tri par date */}
          <div className="flex items-center gap-2">
            <label className="text-blue text-sm">Date :</label>
            <select
              value={sort}
              onChange={e => {
                setSort(e.target.value);
                setCurrentPage(1);
              }}
              className="rounded-full border border-silver px-4 py-2 text-sm appearance-none bg-white focus:border-blue focus:ring-1 focus:ring-blue"
            >
              <option value="desc">Plus récent → Plus ancien</option>
              <option value="asc">Plus ancien → Plus récent</option>
            </select>
          </div>
        </div>

        {/* Liste des conversations */}
        <div className="my-2">
          {filteredConversations.length === 0 ? (
            <p className="text-center text-silver text-sm py-8">
              {search ? "Aucune conversation trouvée." : "Vous n'avez pas encore de conversations."}
            </p>
          ) : (
            paginatedConversations.map((conv) => {
              const unreadCount = unreadCounts[conv.otherUserId] || 0;
              const hasUnread = unreadCount > 0;

              return (
                <div
                  key={conv.otherUserId}
                  href={`/messenger/${conv.otherUserId}`}
                  onClick={() => handleConversationClick(conv)}
                  className={`block border-2 border-solid w-full mb-4 transition-all hover:shadow-lg cursor-pointer ${
                    hasUnread 
                      ? "border-gold shadow-md bg-[#fffbe5]" 
                      : "border-silver bg-white"
                  }`}
                >
                  <div className="flex items-center h-28">
                    {/* Avatar de l'autre utilisateur */}
                    <div className="relative h-full aspect-square flex-shrink-0 overflow-hidden">
                      <Image
                        src={conv.otherUserAvatar || `/avatars/${conv.otherUserId}.jpg`}
                        alt={conv.otherUserName}
                        fill
                        sizes="100vw"
                        className="object-cover"
                      />
                    </div>

                    {/* Contenu conversation */}
                    <div className="flex flex-col flex-1 min-w-0">
                      <div className="flex items-start">
                        <div className="flex-1 min-w-0 px-4">
                          <h3 className={`text-base md:text-lg font-cabin font-semibold mb-1 mt-0 ${
                            hasUnread ? "text-gold" : "text-silver"
                          }`}>
                            {conv.otherUserName}
                          </h3>

                          <p className={`text-xs md:text-sm font-cabin truncate ${
                            hasUnread ? "text-blue font-semibold" : "text-silver"
                          }`}>
                            {conv.lastMessage ? (
                              [".pdf", ".jpg", ".jpeg", ".png"].some(ext =>
                                conv.lastMessage.toLowerCase().includes(ext)
                              ) ? (
                                <span className="italic">
                                  Vous avez envoyé 1 pièce-jointe : {conv.lastMessage}
                                </span>
                              ) : (
                                conv.lastMessage
                              )
                            ) : (
                              <span className="italic">(Aucun message)</span>
                            )}
                          </p>
                        </div>
                      </div>

                      {/* Date et heure du dernier message */}
                      <div className={`text-xs md:pt-1 pr-4 text-right border-0 border-t-2 border-solid ${
                        hasUnread 
                          ? "border-gold text-blue" 
                          : "border-silver text-silver"
                      }`}>
                        {conv.lastTimestamp ? (
                          <>
                            Reçu le{" "}
                            {new Date(conv.lastTimestamp).toLocaleDateString("fr-FR", {
                              day: "2-digit",
                              month: "2-digit",
                              year: "numeric",
                            })}{" "}
                            à{" "}
                            {new Date(conv.lastTimestamp).toLocaleTimeString("fr-FR", {
                              hour: "2-digit",
                              minute: "2-digit",
                            })}
                          </>
                        ) : null}
                      </div>
                    </div>
                  </div>
                </div>
              );
            })
          )}
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex items-center justify-center gap-3 mt-8">
            <div className="flex items-center gap-2">
              {Array.from({ length: totalPages }).map((_, i) => (
                <button
                  key={i}
                  onClick={() => handlePageChange(i + 1)}
                  className={`w-8 h-8 flex items-center justify-center rounded text-sm font-semibold ${
                    i + 1 === currentPage ? "bg-blue text-white" : "text-silver hover:bg-silver"
                  }`}
                >
                  {i + 1}
                </button>
              ))}
            </div>

            {/* Bouton page suivante */}
            <button className="bg-blue text-white px-6 py-2 rounded-full hover:bg-gold transition-colors duration-200 text-sm font-cabin flex items-center gap-2">
              Page suivante <span>▶</span>
            </button>
          </div>
        )}
      </main>
    </div>
  );
}
