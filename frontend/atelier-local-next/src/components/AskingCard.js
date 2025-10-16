"use client";

import { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import Image from "next/image";
import placeholder from "../../public/tronche.jpg";
import Link from "next/link";

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
      <div
        onClick={() => setIsOpen(true)}
        className={`cursor-pointer sm:items-stretch bg-white border-2 border-gold shadow-md border-solid overflow-hidden w-full max-w-[1150px] transition-transform duration-300 hover:scale-[1.01] ${className}`}
      >
        <div className="flex flex-row">
          <div className="relative w-1/2 sm:w-48 h-48 flex-shrink-0">
            <Image
              src={client?.avatar?.url || placeholder}
              alt={`${client?.firstName || ""} ${client?.lastName || "Client"} avatar`}
              fill
              className="object-cover"
            />
          </div>

          <div className="flex flex-col px-6 text-center sm:text-left flex-grow py-2">
            <p className="text-xl sm:text-2xl text-gold font-cabin my-0">
              {client
                ? `${client.firstName || ""} ${client.lastName || ""}`.trim()
                : "Client anonyme"}
            </p>
            <p className="text-xs sm:text-sm text-silver font-cabin italic mt-1 mb-3">
              {daysAgoText}
            </p>
            <p className="text-base sm:text-lg text-blue font-cabin my-1">
              {asking?.title}
            </p>
            <p className="text-sm text-silver font-cabin my-0">
              {event?.name
                ? `${event.name} • ${asking?.eventLocalisation || ""} • ${formattedDate}`
                : asking?.eventLocalisation || ""}
            </p>
          <div className="hidden md:block mt-4">
            <p className="text-sm text-silver font-cabin mt-2 mb-2">
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
        </div>
        <div className="text-center m-4 md:hidden block">
          <p className="text-sm text-silver font-cabin mt-2 mb-2">
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
              className="fixed inset-0 z-40 bg-black bg-opacity-50 flex items-center justify-center"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setIsOpen(false)} // clic sur le fond ferme le modal
            >
              <motion.div
                className="bg-white shadow-2xl w-[90%] max-w-2xl overflow-hidden relative border-2 border-gold border-solid"
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.9 }}
                transition={{ duration: 0.3 }}
                onClick={(e) => e.stopPropagation()} // empêche fermeture quand on clique dedans
              >
                <div className="flex md:flex-row">
                  <div className="relative w-1/2 sm:w-48 h-48 flex-shrink-0">
                    <Image
                      src={client.avatar?.url || placeholder}
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
                  <p className="text-xs sm:text-sm text-silver font-cabin italic mt-1 mb-3">
                    {daysAgoText}
                  </p>
                  <p className="text-base md:text-xl text-blue font-cabin my-1">
                    {asking?.title}
                  </p>
                  <p className="text-xs md:text-sm text-silver font-cabin my-0">
                    {event?.name
                      ? `${event.name} • ${asking?.eventLocalisation || ""} • ${formattedDate}`
                      : asking?.eventLocalisation || ""}
                  </p>
                  <div className="flex items-center mt-2">
                  <Link
                    href={`/messenger/${client.id}`}
                    className="w-full h-10 md:w-3/4 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                  text-gold md:text-base text-sm font-cabin flex items-center justify-center text-center p-1"
                  >
                    Répondre à {client?.firstName}
                  </Link>
                </div>
                  </div>
                </div>
                <div className="text-center my-8 px-8">
                  <p className="text-sm sm:text-base text-blue font-cabin mt-2 mb-2">
                    "
                    {asking?.content}
                    "
                  </p>
                </div>
            </motion.div>
          </motion.div>
          </>
        )}
      </AnimatePresence>
    </>
  );
}
