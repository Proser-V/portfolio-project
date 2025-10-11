import Image from "next/image";
import logo from "../assets/logo/atelier-local-logo_white.png"
import Link from "next/link";

export default function VisitorHeader() {
  return (
    <header className="bg-blue text-gold shadow-md flex flex-col md:flex-row justify-between items-center px-4 sm:px-6 md:px-8 py-3 md:py-4">
      <Link href="/">
        <Image
          src={logo}
          alt="Logo"
          className="h-12 w-auto mb-2 md:mb-0"
        />
      </Link>
      <nav className="flex flex-col md:flex-row md:gap-6 w-full md:w-auto text-sm text-center">
        <a href="/new-asking" className="flex-auto flex items-center justify-center py-2 hover:underline">Demande de prestation</a>
        <a href="/artisans" className="flex-auto flex items-center justify-center py-2 hover:underline">Artisans</a>
        <a href="/registration" className="flex-auto flex items-center justify-center py-2 hover:underline">Créer un compte</a>
        <a href="/login" className="flex-auto flex items-center justify-center py-2 hover:underline">Connexion</a>
      </nav>
    </header>
  );
}
