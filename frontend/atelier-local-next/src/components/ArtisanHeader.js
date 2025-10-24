import Image from "next/image";
import logo from "../assets/logo/atelier-local-logo_white.png";
import Link from "next/link";
import ArtisanBurgerMenu from "@/components/ArtisanBurgerMenu";

/**
 * Composant ArtisanHeader
 * -----------------------
 * Header principal pour la section artisan.
 * Affiche le logo de l'application et le menu burger spécifique à l'artisan.
 *
 * @param {Object} props - Propriétés du composant
 * @param {Object} props.artisan - Artisan actuellement connecté ou affiché
 *
 * @returns {JSX.Element} Composant header de l'artisan
 */
export default function ArtisanHeader({ artisan }) {
  return (
    <header className="bg-blue text-gold shadow-md relative pr-4 py-2 md:pr-0 md:py-0 md:items-center">
      <div className="flex items-center justify-between">
        {/* Logo de l'application */}
        <Link href="/" className="flex-shrink-0 pl-4">
          <Image
            src={logo}
            alt="Atelier Local Logo"
            className="h-12 w-auto"
          />
        </Link>

        {/* Menu burger et navigation pour l'artisan */}
        <ArtisanBurgerMenu artisan={artisan} />
      </div>
    </header>
  );
}
