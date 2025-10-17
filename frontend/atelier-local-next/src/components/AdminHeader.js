import Image from "next/image";
import logo from "../assets/logo/atelier-local-logo_white.png";
import messengerLogo from "../assets/messenger/poignee-de-main-gold.png";
import Link from "next/link";
import placeholder from "../app/favicon.ico"; // placeholder si avatar manquant

export default function AdminHeader({ admin }) {
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
        <Link
          href="/admin"
          className="flex-auto flex items-center justify-center px-2 py-1 hover:underline"
        >
          Admin panel
        </Link>

        <Link
          href="/new-asking"
          className="flex-auto flex items-center justify-center px-2 py-1 hover:underline whitespace-nowrap"
        >
          Demande de prestation
        </Link>

        <Link
          href="/artisans"
          className="flex-auto flex items-center justify-center px-2 py-1 hover:underline"
        >
          Artisans
        </Link>

        <Link
          href="/messenger"
          className="flex-auto flex items-center justify-center px-4 mt-2"
        >
          <Image
            src={messengerLogo}
            alt="MessengerLogo"
            height={45}
          />
        </Link>

        <Link
          href={`/clients/${admin.id}`}
          className="bg-gold text-blue flex-auto flex items-center justify-center px-2 hover:underline"
        >
          <span className="pt-2 md:pt-0 whitespace-nowrap md:pr-2">
            Bienvenue dans l'Atelier,
            <br className="hidden md:block" />
            {admin.firstName}
          </span>
          <div className="hidden md:block w-16 h-16 overflow-hidden">
            <Image
              src={admin.avatar?.url || placeholder}
              alt={`${admin.firstName} avatar`}
              height={64}
              width={64}
              className="object-cover w-full h-full"
            />
          </div>
          </Link>

        <Link
          href="/logout"
          className="bg-gold text-blue flex-auto flex items-center justify-center pr-2 py-2 hover:underline"
        >
          Déconnexion
        </Link>
      </nav>
    </header>
  );
}
