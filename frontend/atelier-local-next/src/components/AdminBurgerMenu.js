"use client";
import Image from "next/image";
import { useState } from "react";
import messengerLogo from "../assets/messenger/poignee-de-main-gold.png";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import { useUnreadMessages } from "./UnreadMessageProvider";

/**
 * Menu de navigation responsive pour les administrateurs.
 * Affiche une navigation horizontale sur desktop et un menu burger sur mobile.
 * 
 * @component
 * @param {Object} props - Les propriétés du composant
 * @param {Object} props.admin - Objet contenant les informations de l'administrateur connecté
 * @param {string} props.admin.id - Identifiant unique de l'administrateur
 * @param {string} props.admin.firstName - Prénom de l'administrateur
 * @param {string} props.admin.name - Nom complet de l'administrateur
 * @param {Object} props.admin.avatar - Avatar de l'administrateur
 * @param {string} props.admin.avatar.url - URL de l'image d'avatar
 * @returns {JSX.Element} Menu de navigation administrateur
 */
export default function AdminBurgerMenu({ admin }) {
  // ============================================================================
  // ÉTATS LOCAUX
  // ============================================================================
  
  /** @type {[boolean, Function]} État d'ouverture du menu mobile */
  const [isOpen, setIsOpen] = useState(false);
  
  /** @type {Object} Hook personnalisé pour récupérer le nombre de messages non lus */
  const { unreadCount } = useUnreadMessages();

  // ============================================================================
  // RENDU DU COMPOSANT
  // ============================================================================
  
  return (
    <>
      {/* ====================================================================== */}
      {/* NAVIGATION DESKTOP - Visible uniquement sur écrans larges (lg:) */}
      {/* ====================================================================== */}
      <nav className="hidden lg:flex flex-row items-stretch text-sm h-full">
        {/* Lien: Panel d'administration */}
        <Link
          href="/admin"
          className="flex items-center justify-center px-4 py-2 text-gold hover:underline whitespace-nowrap"
        >
          Admin panel
        </Link>

        {/* Lien: Créer une demande de prestation */}
        <Link
          href="/new-asking"
          className="flex items-center justify-center px-4 py-2 text-gold hover:underline whitespace-nowrap"
        >
          Demande de prestation
        </Link>

        {/* Lien: Liste des artisans */}
        <Link
          href="/artisans"
          className="flex items-center justify-center px-4 py-2 text-gold hover:underline whitespace-nowrap"
        >
          Artisans
        </Link>

        {/* Lien: Messagerie avec badge de notification */}
        <Link
          href="/messenger"
          className="flex flex-col items-center px-4"
        >
          {/* Badge de notification (nombre de messages non lus) */}
          {unreadCount > 0 ?(
          <div className={`absolute flex items-center justify-center border-gold border-solid bg-white rounded-full ${unreadCount < 10 ? "h-3 w-3 mt-1" : "h-2 w-2 mt-3 ml-10"}`}>
            {/* Affiche le nombre si < 10, sinon affiche juste un point */}
            {unreadCount < 10 ?(
              <span className="relative flex text-[12px] items-center justify-center text-center">{unreadCount}</span>
            ) : (
              ""
            )}
          </div>
          ) : (
            ""
          )}
          <Image
          src={messengerLogo}
          alt="Messenger"
          height={30}
          className={`${unreadCount > 0 ? "lg:mt-5" : "lg:mt-4"} w-auto`}
          />
        </Link>

        {/* Section dorée avec profil et déconnexion */}
        <div className="flex flex-row items-center bg-gold text-blue h-full">
          {/* Lien: Profil de l'administrateur */}
          <Link
            href={`/clients/${admin.id}`}
            className="flex items-center justify-center pl-4 pr-2 hover:underline gap-2 h-full"
          >
            <span className="whitespace-nowrap text-center pr-2">
              Bienvenue dans l'Atelier,<br/>{admin.firstName}
            </span>
            <div className="w-14 h-14 overflow-hidden">
              <Image
                src={admin.avatar?.url}
                alt={`${admin.name} avatar`}
                width={56}
                height={56}
                className="object-cover w-full h-full"
              />
            </div>
          </Link>

          {/* Lien: Déconnexion */}
          <Link
            href="/logout"
            className="flex items-center justify-center px-4 py-2 hover:underline h-full"
          >
            Déconnexion
          </Link>
        </div>
      </nav>

      {/* ====================================================================== */}
      {/* BOUTON BURGER - Visible uniquement sur mobile (< lg) */}
      {/* ====================================================================== */}
      <button
      className="lg:hidden text-gold border border-gold rounded-lg p-2 bg-blue z-50 mr-2"
        onClick={() => setIsOpen(!isOpen)}
        aria-label="Toggle menu"
      >
        {/* Icône burger (3 barres horizontales) */}
        <span className="block w-6 h-0.5 bg-gold mb-1"></span>
        <span className="block w-6 h-0.5 bg-gold mb-1"></span>
        <span className="block w-6 h-0.5 bg-gold"></span>
      </button>

      {/* ====================================================================== */}
      {/* NAVIGATION MOBILE - Menu déroulant avec animation */}
      {/* ====================================================================== */}
      <AnimatePresence>
        {isOpen && (
          <motion.nav
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.3, ease: "easeInOut" }}
            className="absolute left-0 right-0 top-full w-full bg-blue text-gold flex flex-col text-center shadow-lg lg:hidden border-t border-gold z-40 overflow-hidden"
          >
            {/* Lien: Panel d'administration */}
            <Link
              href="/admin"
              onClick={() => setIsOpen(false)}
              className="py-4 transition-colors duration-200"
            >
              Admin panel
            </Link>

            {/* Lien: Créer une demande de prestation */}
            <Link
              href="/new-asking"
              onClick={() => setIsOpen(false)}
              className="py-4 transition-colors duration-200"
            >
              Demande de prestation
            </Link>

            {/* Lien: Liste des artisans */}
            <Link
              href="/artisans"
              onClick={() => setIsOpen(false)}
              className="py-4 transition-colors duration-200"
            >
              Artisans
            </Link>

            {/* Lien: Messagerie avec indicateur de messages non lus */}
            <Link
              href="/messenger"
              onClick={() => setIsOpen(false)}
              className="py-2 transition-colors duration-200 flex items-center justify-center"
            >
              {/* Indicateur visuel (point rouge) si messages non lus */}
              {unreadCount > 0 ?(
              <div className={`absolute flex items-center justify-center border-gold border-solid bg-white rounded-full h-2 w-2 mb-8 ml-10`}></div>
              ) : (
                ""
              )}
              <Image
                src={messengerLogo}
                alt="Messenger"
                height={30}
                className="w-auto"
              />
            </Link>

            {/* Lien: Profil de l'administrateur */}
            <Link
              href={`/clients/${admin.id}`}
              onClick={() => setIsOpen(false)}
              className="py-4 bg-gold text-blue transition-colors duration-200 flex items-center justify-center gap-2"
            >
              <span>Bienvenue dans l'atelier, {admin.firstName}</span>
            </Link>

            {/* Lien: Déconnexion */}
            <Link
              href="/logout"
              onClick={() => setIsOpen(false)}
              className="py-4 px-4 bg-gold text-blue transition-colors duration-200"
            >
              Déconnexion
            </Link>
          </motion.nav>
        )}
      </AnimatePresence>
    </>
  );
}
