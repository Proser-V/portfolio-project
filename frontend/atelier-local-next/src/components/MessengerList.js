"use client";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { getUnreadMessages, countUnreadByUser } from "@/lib/messageService";

export default function MessengerList({ initialConversations, conversationsPerPage, jwtToken, currentUserId }) {
  const [conversations, setConversations] = useState(initialConversations || []);
  const [unreadCounts, setUnreadCounts] = useState({});
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const router = useRouter();

  // Charger les messages non lus au montage
  useEffect(() => {
    loadUnreadCounts();
  }, [jwtToken]);

  // Rafraîchir les compteurs toutes les 30 secondes
  useEffect(() => {
    const interval = setInterval(() => {
      loadUnreadCounts();
    }, 30000);

    return () => clearInterval(interval);
  }, [jwtToken]);

  async function loadUnreadCounts() {
    const unreadMessages = await getUnreadMessages(jwtToken);
    const counts = countUnreadByUser(unreadMessages, currentUserId);
    setUnreadCounts(counts);
  }

  // Filtrer les conversations par recherche
  const filteredConversations = conversations.filter((conv) =>
    conv.otherUserName?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Pagination
  const totalPages = Math.ceil(filteredConversations.length / conversationsPerPage);
  const startIndex = (currentPage - 1) * conversationsPerPage;
  const paginatedConversations = filteredConversations.slice(
    startIndex,
    startIndex + conversationsPerPage
  );

  const handleConversationClick = (otherUserId) => {
    router.push(`/messenger/${otherUserId}`);
  };

  return (
    <div className="mt-8">
      {/* Barre de recherche */}
      <div className="mb-4">
        <input
          type="text"
          placeholder="Rechercher une conversation..."
          value={searchTerm}
          onChange={(e) => {
            setSearchTerm(e.target.value);
            setCurrentPage(1);
          }}
          className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {/* Liste des conversations */}
      {paginatedConversations.length === 0 ? (
        <p className="text-center text-gray-500 py-8">
          {searchTerm ? "Aucune conversation trouvée." : "Vous n'avez pas encore de conversations."}
        </p>
      ) : (
        <div className="space-y-3">
          {paginatedConversations.map((conv) => {
            const unreadCount = unreadCounts[conv.otherUserId] || 0;

            return (
              <div
                key={conv.otherUserId}
                onClick={() => handleConversationClick(conv.otherUserId)}
                className="flex items-center justify-between p-4 border rounded-lg cursor-pointer transition-all hover:shadow-md bg-white border-gray-200"
              >
                <div className="flex items-center space-x-3 flex-1">
                  {/* Avatar */}
                  {conv.otherUserAvatar ? (
                    <img
                      src={conv.otherUserAvatar}
                      alt={conv.otherUserName}
                      className="w-12 h-12 rounded-full object-cover"
                    />
                  ) : (
                    <div className="w-12 h-12 rounded-full bg-gray-300 flex items-center justify-center text-gray-600 font-semibold">
                      {conv.otherUserName?.charAt(0).toUpperCase() || "?"}
                    </div>
                  )}

                  {/* Nom et dernier message */}
                  <div className="flex-1 min-w-0">
                    <p className="font-semibold text-gray-800">
                      {conv.otherUserName}
                    </p>
                    <p className="text-sm text-gray-600 truncate">
                      {conv.lastMessage || "Aucun message"}
                    </p>
                    <p className="text-xs text-gray-400 mt-1">
                      {conv.lastMessageTime
                        ? new Date(conv.lastMessageTime).toLocaleString("fr-FR", {
                            day: "2-digit",
                            month: "2-digit",
                            year: "numeric",
                            hour: "2-digit",
                            minute: "2-digit",
                          })
                        : ""}
                    </p>
                  </div>
                </div>

                {/* Badge de messages non lus */}
              </div>
            );
          })}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center items-center space-x-2 mt-6">
          <button
            onClick={() => setCurrentPage((prev) => Math.max(1, prev - 1))}
            disabled={currentPage === 1}
            className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-gray-300 disabled:cursor-not-allowed"
          >
            Précédent
          </button>
          <span className="text-gray-600">
            Page {currentPage} / {totalPages}
          </span>
          <button
            onClick={() => setCurrentPage((prev) => Math.min(totalPages, prev + 1))}
            disabled={currentPage === totalPages}
            className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-gray-300 disabled:cursor-not-allowed"
          >
            Suivant
          </button>
        </div>
      )}
    </div>
  );
}