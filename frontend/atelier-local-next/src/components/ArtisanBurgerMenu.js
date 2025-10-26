"use client";
import Image from "next/image";
import { useState } from "react";
import messengerLogo from "../assets/messenger/poignee-de-main-gold.png";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import { useUnreadMessages } from "./UnreadMessageProvider";

/**
 * Composant ArtisanBurgerMenu
 * ---------------------------
 * Menu de navigation spécifique à un artisan.
 * Affiche :
 *  - Navigation desktop : liste des demandes, messenger avec compteur de messages non lus, info artisan et logout
 *  - Navigation mobile : menu burger dropdown avec les mêmes options
 * Utilise Framer Motion pour les animations du menu mobile.
 *
 * @param {Object} props - Propriétés du composant
 * @param {Object} props.artisan - Artisan connecté
 * @param {string|number} props.artisan.id - Identifiant de l'artisan
 * @param {string} props.artisan.name - Nom de l'artisan
 * @param {string} props.artisan.categoryId - Catégorie de l'artisan
 * @param {Object} props.artisan.avatar - Objet contenant l'URL de l'avatar de l'artisan
 *
 * @returns {JSX.Element} Menu de navigation adapté desktop et mobile
 */
export default function ArtisanBurgerMenu({ artisan }) {
  const [isOpen, setIsOpen] = useState(false); // Etat du menu mobile
  const { unreadCount } = useUnreadMessages(); // Nombre de messages non lus

  return (
    <>
      {/* Navigation Desktop */}
      <nav className="hidden md:flex flex-row items-stretch text-sm h-full">
        {/* Lien vers les demandes */}
        <Link
          href={`/askings/${artisan.categoryId}`}
          className="flex items-center justify-center px-4 py-2 text-gold hover:underline whitespace-nowrap"
        >
          Liste des demandes
        </Link>

        {/* Lien Messenger avec compteur de messages non lus */}
        <Link
          href="/messenger"
          className="flex flex-col items-center px-4"
        >
          {unreadCount > 0 && (
            <div className={`absolute flex items-center justify-center border-gold border-solid bg-white rounded-full ${unreadCount < 10 ? "h-3 w-3 mt-1" : "h-2 w-2 mt-3 ml-10"}`}>
              {unreadCount < 10 && (
                <span className="relative flex text-[12px] items-center justify-center text-center">{unreadCount}</span>
              )}
            </div>
          )}
          <Image
            src={messengerLogo}
            alt="Messenger"
            height={30}
            className={`${unreadCount > 0 ? "lg:mt-5 md:mt-5" : "lg:mt-4 md:mt-4"} w-auto`}
          />
        </Link>

        {/* Bloc info artisan et déconnexion */}
        <div className="flex flex-row items-center bg-gold text-blue h-full">
          <Link
            href={`/artisans/${artisan.id}`}
            className="flex items-center justify-center pl-4 pr-2 hover:underline gap-2 h-full"
          >
            <span className="whitespace-nowrap text-center pr-2">
              Bienvenue dans l'Atelier,<br />{artisan.name}
            </span>
            <div className="w-14 h-14 overflow-hidden">
              <Image
                src={artisan.avatar?.url}
                alt={`${artisan.name} avatar`}
                width={56}
                height={56}
                className="object-cover w-full h-full"
              />
            </div>
          </Link>

          {/* Lien déconnexion */}
          <Link
            href="/logout"
            className="flex items-center justify-center px-4 py-2 hover:underline h-full"
          >
            Déconnexion
          </Link>
        </div>
      </nav>

      {/* Bouton Burger (mobile uniquement) */}
      <button
        className="md:hidden text-gold border border-gold rounded-lg p-2 bg-blue z-50"
        onClick={() => setIsOpen(!isOpen)}
        aria-label="Toggle menu"
      >
        <span className="block w-6 h-0.5 bg-gold mb-1"></span>
        <span className="block w-6 h-0.5 bg-gold mb-1"></span>
        <span className="block w-6 h-0.5 bg-gold"></span>
      </button>

      {/* Navigation Mobile (dropdown animé) */}
      <AnimatePresence>
        {isOpen && (
          <motion.nav
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.3, ease: "easeInOut" }}
            className="absolute left-0 right-0 top-full w-full bg-blue text-gold flex flex-col text-center shadow-lg md:hidden border-t border-gold z-40 overflow-hidden"
          >
            {/* Lien vers les demandes */}
            <Link
              href={`/askings/${artisan.categoryId}`}
              onClick={() => setIsOpen(false)}
              className="py-4"
            >
              Liste des demandes
            </Link>

            {/* Lien Messenger */}
            <Link
              href="/messenger"
              onClick={() => setIsOpen(false)}
              className="py-2 transition-colors duration-200 flex items-center justify-center"
            >
              {unreadCount > 0 && (
                <div className="absolute flex items-center justify-center border-gold border-solid bg-white rounded-full h-2 w-2 mb-8 ml-10"></div>
              )}
              <Image
                src={messengerLogo}
                alt="Messenger"
                height={30}
                className="w-auto"
              />
            </Link>

            {/* Lien profil artisan */}
            <Link
              href={`/artisans/${artisan.id}`}
              onClick={() => setIsOpen(false)}
              className="py-4 bg-gold text-blue transition-colors duration-200 flex items-center justify-center gap-2"
            >
              <span>Bienvenue dans l'atelier, {artisan.name}</span>
            </Link>

            {/* Lien déconnexion */}
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
