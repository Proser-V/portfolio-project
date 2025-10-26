"use client";

import { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import Image from "next/image";
import Link from "next/link";
import getApiUrl from "@/lib/api";

/**
 * Récupère les informations d’un client via son ID
 * @param {string|number} clientId - ID du client
 * @returns {Promise<Object|null>} - Objet client ou null en cas d’erreur
 */
async function getClient(clientId) {
  try {
    const res = await fetch(
      `${getApiUrl()}/api/clients/${clientId}`,
      { cache: "no-store", credentials: "include" }
    );
    if (!res.ok) return null;
    return await res.json();
  } catch (err) {
    console.log("Erreur récupération client:", err);
    return null;
  }
}

/**
 * Récupère les informations d’une catégorie d’évènement via son ID
 * @param {string|number} eventId - ID de l’évènement
 * @returns {Promise<Object|null>} - Objet catégorie d’évènement ou null en cas d’erreur
 */
async function getEventCategory(eventId) {
  try {
    const res = await fetch(
      `${getApiUrl()}/api/event-categories/${eventId}`,
      { cache: "no-store", credentials: "include" }
    );
    if (!res.ok) return null;
    return await res.json();
  } catch (err) {
    console.log("Erreur récupération évènement:", err);
    return null;
  }
}

/**
 * AskingCard
 *
 * Composant affichant une carte de demande ("asking") avec :
 * - Informations client
 * - Informations de l’évènement
 * - Titre et description
 * - Date de publication et date d’évènement
 * - Modal détaillé pour afficher toutes les informations et bouton pour répondre
 *
 * Fonctionnalités :
 * - Récupération asynchrone des informations client et évènement
 * - Affichage conditionnel pour données manquantes
 * - Troncature du contenu long pour la version carte
 * - Modal animé avec Framer Motion pour visualisation complète
 *
 * @component
 *
 * @param {Object} props - Les props du composant
 * @param {Object} props.asking - Objet représentant la demande
 * @param {string|number} props.asking.clientId - ID du client
 * @param {string|number} props.asking.eventCategoryId - ID de la catégorie d’évènement
 * @param {string} props.asking.title - Titre de la demande
 * @param {string} props.asking.content - Description de la demande
 * @param {string} props.asking.eventDate - Date de l’évènement (ISO)
 * @param {string} props.asking.eventLocalisation - Localisation de l’évènement
 * @param {string} props.asking.createdAt - Date de création de la demande (ISO)
 * @param {string} [props.className] - Classe CSS optionnelle pour personnaliser la carte
 *
 * @example
 * <AskingCard 
 *    asking={{
 *      clientId: 1,
 *      eventCategoryId: 2,
 *      title: "Réparer fuite d’eau",
 *      content: "Besoin d’un plombier urgent...",
 *      eventDate: "2025-11-01",
 *      eventLocalisation: "Dijon",
 *      createdAt: "2025-10-23"
 *    }} 
 *    className="w-full"
 * />
 */
export default function AskingCard({ asking, className }) {
  // États locaux
  const [client, setClient] = useState(null);     // Stocke les infos client
  const [event, setEvent] = useState(null);       // Stocke les infos catégorie d’évènement
  const [isOpen, setIsOpen] = useState(false);    // Contrôle l’ouverture du modal

  // Récupération asynchrone des informations client et évènement
  useEffect(() => {
    if (asking?.clientId) getClient(asking.clientId).then(setClient);
    if (asking?.eventCategoryId) getEventCategory(asking.eventCategoryId).then(setEvent);
  }, [asking]);

  // --- Gestion de la date de publication ---
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

  // Formatage de la date de l’évènement en français
  const formattedDate = asking?.eventDate
    ? new Date(asking.eventDate).toLocaleDateString("fr-FR")
    : "-";

  return (
    <>
      {/* --- Carte de demande --- */}
      <div
        onClick={() => setIsOpen(true)}
        className={`cursor-pointer sm:items-stretch bg-white border-2 border-gold shadow-md border-solid overflow-hidden w-full max-w-[1150px] transition-transform duration-300 hover:scale-[1.01] ${className}`}
      >
        {/* Ligne principale : image + contenu */}
        <div className="flex flex-row">
          {/* Image client */}
          <div className="relative w-1/2 sm:w-48 h-48 flex-shrink-0">
            <Image
              src={client?.avatar?.url || "/placeholder.png"}
              alt={`${client?.firstName || ""} ${client?.lastName || "Client"} avatar`}
              fill
              className="object-cover"
            />
          </div>

          {/* Contenu texte */}
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
            {/* Contenu résumé pour Desktop */}
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

        {/* Contenu résumé pour Mobile */}
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

      {/* --- Modal animé avec Framer Motion --- */}
      <AnimatePresence>
        {isOpen && (
          <>
            {/* Fond semi-transparent */}
            <motion.div
              className="fixed inset-0 z-40 bg-black bg-opacity-50 flex items-center justify-center"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setIsOpen(false)} // Fermeture au clic sur fond
            >
              <motion.div
                className="bg-white shadow-2xl w-[90%] max-w-2xl overflow-hidden relative border-2 border-gold border-solid"
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.9 }}
                transition={{ duration: 0.3 }}
                onClick={(e) => e.stopPropagation()} // Empêche la fermeture au clic interne
              >
                {/* Contenu modal : image + détails */}
                <div className="flex md:flex-row">
                  <div className="relative w-1/2 sm:w-48 h-48 flex-shrink-0">
                    <Image
                      src={client.avatar?.url || "/placeholder.png"}
                      alt={`${client?.firstName || ""} ${client?.lastName || "Client"} avatar`}
                      fill
                      sizes="100vw"
                      className="object-cover"
                    />
                  </div>

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

                    {/* Bouton répondre */}
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

                {/* Contenu complet de la demande */}
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
