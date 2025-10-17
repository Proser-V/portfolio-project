import Image from "next/image";
import logo from "../assets/logo/atelier-local-logo_white.png";
import messengerLogo from "../assets/messenger/poignee-de-main-gold.png";
import Link from "next/link";

export default function ArtisanHeader({ artisan }) {
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
          href={`/askings/${artisan.categoryId}`}
          className="flex-auto flex items-center justify-center px-2 py-1 hover:underline whitespace-nowrap"
        >
          Liste des demandes
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
          href={`/artisans/${artisan.id}`}
          className="bg-gold text-blue flex-auto flex items-center justify-center px-2 hover:underline gap-2"
        >
          <span className="pt-2 md:pt-0 whitespace-nowrap text-center">
            Bienvenue dans l'Atelier,
            <br className="hidden md:block" />
            {artisan.name}
          </span>

          <div className="hidden md:block w-16 h-16 overflow-hidden">
            <Image
              src={artisan.avatar?.url}
              alt={`${artisan.name} avatar`}
              width={64}
              height={64}
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
