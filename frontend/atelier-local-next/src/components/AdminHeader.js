import Image from "next/image";
import logo from "../assets/logo/atelier-local-logo_white.png"
import messengerLogo from "../assets/messenger/poignee-de-main-gold.png"

export default function AdminHeader({ admin }) {
  return (
    <header className="bg-blue text-gold shadow-md flex flex-col md:flex-row justify-between items-center px-4 sm:px-6 md:px-8 py-3 md:py-4">
      <Image
        src={logo}
        alt="Logo"
        className="h-12 w-auto mb-2 md:mb-0"
      />
      <nav className="flex flex-col md:flex-row md:gap-6 w-full md:w-auto text-sm text-center">
        <a href="#" className="flex-auto flex items-center justify-center py-2 hover:underline">Panneau administrateur</a>
        <a href="#" className="flex-auto flex items-center justify-center py-2">
          <Image
            src={messengerLogo}
            alt="MessengerLogo"
            className="h-12 w-auto mb-2 md:mb-0"
          />
        </a>
        <a href="#" className="bg-gold text-blue flex-auto flex items-center justify-center py-2 hover:underline">Bienvenue dans l'Atelier {client.firstName}</a>
        <a href="#" className="flex-auto flex items-center justify-center py-2">
          <Image
            src={admin.avatar}
            alt={`${admin.firstName} avatar`}
            className="h-32 w-auto"
          />
        </a>
        <a href="#" className="bg-gold text-blue flex-auto flex items-center justify-center py-2 hover:underline">Déconnexion</a>
      </nav>
    </header>
  );
}
