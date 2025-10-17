"use client";
import Image from "next/image";
import { useState } from "react";
import messengerLogo from "../assets/messenger/poignee-de-main-gold.png";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";

export default function VisitorBurgerMenu() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
    {/* Navigation Desktop */}
    <nav className="hidden md:flex flex-row items-stretch text-sm h-full">
      <Link
        href="/new-asking"
        className="flex items-center justify-center px-4 py-2 text-gold hover:underline whitespace-nowrap"
      >
        Demande de prestation
      </Link>

      <Link
        href="/artisans"
        className="flex items-center justify-center px-4 py-2 text-gold hover:underline whitespace-nowrap"
      >
        Artisans
      </Link>


      <Link
        href={`/registration`}
        className="flex items-center justify-center px-4 py-2 hover:underline h-full"
      >
          Créer un compte
      </Link>

      <Link
        href="/login"
        className="flex items-center justify-center pl-2 pr-4 py-2 hover:underline h-full"
      >
        Connexion
      </Link>
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

      {/* Navigation Mobile (dropdown) */}
      <AnimatePresence>
        {isOpen && (
          <motion.nav
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.3, ease: "easeInOut" }}
            className="absolute left-0 right-0 top-full w-full bg-blue text-gold flex flex-col text-center shadow-lg md:hidden border-t border-gold z-40 overflow-hidden"
          >

            <Link
              href="/new-asking"
              onClick={() => setIsOpen(false)}
              className="py-4 transition-colors duration-200"
            >
              Demande de prestation
            </Link>

            <Link
              href="/artisans"
              onClick={() => setIsOpen(false)}
              className="py-4 transition-colors duration-200"
            >
              Artisans
            </Link>

            <Link
              href="/registration"
              onClick={() => setIsOpen(false)}
              className="py-4 transition-colors duration-200 flex items-center justify-center gap-2"
            >
              Créer un compte
            </Link>

            <Link
              href="/login"
              onClick={() => setIsOpen(false)}
              className="py-4 px-4 transition-colors duration-200"
            >
              Connexion
            </Link>
          </motion.nav>
        )}
      </AnimatePresence>
    </>
  );
}