"use client";

import { useState, useMemo } from "react";
import Link from "next/link";
import Image from "next/image";

export default function MessengerList({ initialConversations, conversationsPerPage = 10 }) {
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState("desc");

  // Filtrage et tri côté client
  const filteredConversations = useMemo(() => {
    let convs = initialConversations;
    if (search) {
      convs = convs.filter(c => c.otherUserName.toLowerCase().includes(search.toLowerCase()));
    }
    convs.sort((a, b) => {
      const da = new Date(a.lastTimestamp);
      const db = new Date(b.lastTimestamp);
      return sort === "asc" ? da - db : db - da;
    });
    return convs;
  }, [initialConversations, search, sort]);

  const totalPages = Math.ceil(filteredConversations.length / conversationsPerPage);

  return (
    <div className="bg-white py-8">
      <main className="max-w-xs md:max-w-4xl mx-auto px-4">
        {/* Barre de recherche et tri */}
        <p className="text-sm text-center mt-0">Filtrer vos messages par</p>
        <div className="flex flex-col md:flex-row items-center justify-center gap-4 mb-8">
          <div className="flex items-center gap-2">
            <label className="text-blue text-sm">Nom :</label>
            <input
              type="text"
              value={search}
              onChange={e => setSearch(e.target.value)}
              placeholder=""
              className="rounded-full border border-silver px-4 py-2 text-sm w-48 outline-none focus:border-blue focus:ring-1 focus:ring-blue"
            />
          </div>
          
          <div className="flex items-center gap-2">
            <label className="text-blue text-sm">Date :</label>
            <select
              value={sort}
              onChange={e => setSort(e.target.value)}
              className="rounded-full border border-silver px-4 py-2 text-sm appearance-none bg-white focus:border-blue focus:ring-1 focus:ring-blue"
            >
              <option value="desc">Plus récent → Plus ancien</option>
              <option value="asc">Plus ancien → Plus récent</option>
            </select>
          </div>
        </div>

        {/* Liste des conversations */}
        <div className="space-y-0">
          {filteredConversations.length === 0 ? (
            <p className="text-center text-silver text-sm py-8">
              Aucune conversation trouvée.
            </p>
          ) : (
            filteredConversations.map((conv, index) => (
              <Link
                key={conv.otherUserId}
                href={`/messenger/${conv.otherUserId}`}
                className="block bg-white border-2 border-solid border-silver w-full mb-4"
              >
                <div className="flex items-center h-28">
                  {/* Avatar */}
                  <div className="relative h-full aspect-square flex-shrink-0 overflow-hidden">
                    <Image
                      src={conv.otherUserAvatar || `/avatars/${conv.otherUserId}.jpg`}
                      alt={conv.otherUserName}
                      fill
                      sizes="100vw"
                      className="object-cover"
                    />
                  </div>

                  {/* Contenu */}
                  <div className="flex flex-col flex-1 min-w-0">
                    <div className="flex items-start">
                      <div className="flex-1 min-w-0 px-4">
                        {/* Nom */}
                        <h3 className="text-silver text-base md:text-lg font-cabin font-semibold mb-1 mt-0">
                          {conv.otherUserName}
                        </h3>

                        {/* Dernier message */}
                        <p className="text-silver text-xs md:text-sm font-cabin truncate">
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

                    {/* Date et heure */}
                    <div className="text-xs md:pt-1 pr-4 text-silver text-right border-0 border-t-2 border-solid border-silver">
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
              </Link>
            ))
          )}
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex items-center justify-center gap-3 mt-8">
            {/* Numéros de page */}
            <div className="flex items-center gap-2">
              {Array.from({ length: totalPages }).map((_, i) => (
                <button
                  key={i}
                  className={`w-8 h-8 flex items-center justify-center rounded text-sm font-semibold ${
                    i === 0 ? "bg-blue text-white" : "text-silver hover:bg-silver"
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
