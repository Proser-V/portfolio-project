import Image from "next/image";
import logo from "../assets/logo/atelier-local-logo_white.png"
import messengerLogo from "../assets/messenger/poignee-de-main-gold.png"
import Link from "next/link";

export default function ClientHeader({ client }) {
  return (
    <header className="bg-blue text-gold shadow-md flex flex-col md:flex-row justify-between items-center">
      <Link href="/">
        <Image
          src={logo}
          alt="Logo"
          className="h-12 w-auto mb-2 md:mb-0 ml-2"
        />
      </Link>
      <nav className="flex flex-col md:flex-row w-full md:w-auto text-sm text-center">
        <a href="/new-asking" className="flex-auto flex items-center justify-center px-2 py-1 hover:underline whitespace-nowrap">Demande de prestation</a>
        <a href="/artisans" className="flex-auto flex items-center justify-center px-2 py-1 hover:underline">Artisans</a>
        <a href="/messenger/" className="flex-auto flex items-center justify-center px-4 mt-2">
          <Image
            src={messengerLogo}
            alt="MessengerLogo"
            height={45}
          />
        </a>
        <a href="/client/client.id" className="bg-gold text-blue flex-auto flex items-center justify-center px-2 hover:underline pb-2 md:pb-0">
            <span className="pt-2 md:pt-0 whitespace-nowrap">
              Bienvenue dans l'Atelier,
              <br className="hidden md:block" />
              {client.firstName}
            </span>
          <Image
            src={client.avatar.url}
            alt={`${client.firstName} avatar`}
            height={64}
            width={64}
            className="hidden md:block ml-2"
          />
        </a>
        <a href="/logout" className="bg-gold text-blue flex-auto flex items-center justify-center pr-2 py-1 hover:underline">Déconnexion</a>
      </nav>
    </header>
  );
}
