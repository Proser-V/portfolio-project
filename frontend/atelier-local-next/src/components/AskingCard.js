"use client";

import { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import Image from "next/image";
import placeholder from "../../public/tronche.jpg";

async function getClient(clientId) {
  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/clients/${clientId}`,
      { cache: "no-store", credentials: "include" }
    );
    if (!res.ok) return null;
    return await res.json();
  } catch (err) {
    console.log("Erreur récupération client:", err);
    return null;
  }
}

async function getEventCategory(eventId) {
  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/event-categories/${eventId}`,
      { cache: "no-store", credentials: "include" }
    );
    if (!res.ok) return null;
    return await res.json();
  } catch (err) {
    console.log("Erreur récupération évènement:", err);
    return null;
  }
}

export default function AskingCard({ asking, className }) {
  const [client, setClient] = useState(null);
  const [event, setEvent] = useState(null);
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    if (asking?.clientId) getClient(asking.clientId).then(setClient);
    if (asking?.eventCategoryId) getEventCategory(asking.eventCategoryId).then(setEvent);
  }, [asking]);

  // --- Date de publication
  const publicationDate = asking?.createdAt ? new Date(asking.createdAt) : null;
  let daysAgoText = "Date inconnue";
  if (publicationDate) {
    const diffDays = Math.floor((new Date() - publicationDate) / (1000 * 60 * 60 * 24));
    daysAgoText =
      diffDays === 0
        ? "Publié aujourd'hui"
        : diffDays === 1
        ? "Publié il y a 1 jour"
        : `Publié il y a ${diffDays} jours`;
  }

  const formattedDate = asking?.eventDate
    ? new Date(asking.eventDate).toLocaleDateString("fr-FR")
    : "-";

  return (
    <>
      {/* --- Carte compacte --- */}
      <div
        onClick={() => setIsOpen(true)}
        className={`cursor-pointer flex flex-col sm:flex-row items-center sm:items-stretch justify-between bg-white border-2 border-gold shadow-md border-solid overflow-hidden w-full max-w-[1150px] transition-transform duration-300 hover:scale-[1.01] ${className}`}
      >
        {/* Image client */}
        <div className="relative w-full sm:w-48 h-48 flex-shrink-0">
          <Image
            src={client?.avatar || placeholder}
            alt={`${client?.firstName || ""} ${client?.lastName || "Client"} avatar`}
            fill
            className="object-cover"
          />
        </div>

        {/* Contenu résumé */}
        <div className="flex flex-col justify-start px-6 text-center sm:text-left flex-grow py-2">
          <p className="text-xl sm:text-2xl text-gold font-cabin my-0">
            {client
              ? `${client.firstName || ""} ${client.lastName || ""}`.trim()
              : "Client anonyme"}
          </p>
          <p className="text-xs sm:text-sm text-silver font-cabin italic my-0">
            {daysAgoText}
          </p>
          <p className="text-base sm:text-lg text-blue font-cabin my-0">
            {asking?.title}
          </p>
          <p className="text-sm text-silver font-cabin my-0">
            {event?.name
              ? `${event.name} • ${asking?.eventLocalisation || "Lieu inconnu"} • ${formattedDate}`
              : asking?.eventLocalisation || "Lieu inconnu"}
          </p>
          <p className="text-sm sm:text-base text-silver font-cabin mt-2 mb-2">
            "
            {asking?.content
              ? asking.content.length > 100
                ? `${asking.content.slice(0, 100)}...`
                : asking.content
              : "Aucune description fournie."}
            "
          </p>
        </div>
      </div>

      {/* --- Modal animé --- */}
      <AnimatePresence>
        {isOpen && (
          <>
            {/* Fond noir semi-transparent */}
            <motion.div
              className="fixed inset-0 bg-black bg-opacity-50 z-40"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setIsOpen(false)}
            />

            {/* Carte agrandie au centre */}
            <motion.div
              className="fixed z-50 top-1/2 left-1/2 bg-white border-2 border-gold shadow-2xl rounded-lg w-[90%] max-w-2xl transform -translate-x-1/2 -translate-y-1/2 overflow-hidden"
              initial={{ opacity: 0, scale: 0.9, y: 30 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 30 }}
              transition={{ duration: 0.3 }}
            >
              <div className="relative w-full h-56">
                <Image
                  src={client?.avatar || placeholder}
                  alt="Client avatar"
                  fill
                  className="object-cover"
                />
              </div>

              <div className="p-6">
                <h2 className="text-2xl font-cabin text-gold mb-2">
                  {asking?.title}
                </h2>
                <p className="text-silver italic text-sm mb-1">{daysAgoText}</p>
                <p className="text-blue mb-2">
                  {event?.name
                    ? `${event.name} • ${asking?.eventLocalisation} • ${formattedDate}`
                    : asking?.eventLocalisation || "Lieu inconnu"}
                </p>
                <p className="text-base text-silver font-cabin mb-6">
                  {asking?.content || "Aucune description fournie."}
                </p>

                <div className="flex justify-between items-center">
                  <button
                    onClick={() => setIsOpen(false)}
                    className="px-4 py-2 rounded-full bg-gray-200 text-gray-700 hover:bg-gray-300 transition"
                  >
                    Fermer
                  </button>
                  <button
                    onClick={() => alert(`Contact avec ${client?.firstName || "le client"}`)}
                    className="px-5 py-2 rounded-full bg-gold text-white hover:bg-yellow-600 transition"
                  >
                    Contacter le client
                  </button>
                </div>
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>
    </>
  );
}
