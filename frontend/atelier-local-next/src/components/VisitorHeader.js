import Image from "next/image";
import logo from "../assets/logo/atelier-local-logo_white.png";
import Link from "next/link";
import VisitorBurgerMenu from "@/components/VisitorBurgerMenu";

/**
 * Composant d’en-tête (header) affiché aux visiteurs non connectés.
 *
 * Ce composant :
 * - Affiche le logo de la plateforme, cliquable pour retourner à la page d’accueil.
 * - Intègre le menu burger spécifique aux visiteurs pour la navigation.
 * - Est stylisé avec les couleurs de la charte : fond bleu et texte doré.
 *
 * Utilisation typique :
 * <VisitorHeader />
 */
export default function VisitorHeader() {
  return (
    <header className="bg-blue text-gold shadow-md relative pr-4 py-2 md:pr-0 md:py-0 md:items-center">
      <div className="flex items-center justify-between">
        {/* Logo cliquable ramenant à la page d’accueil */}
        <Link href="/" className="flex-shrink-0 pl-4">
          <Image
            src={logo}
            alt="Atelier Local Logo"
            className="h-12 w-auto"
          />
        </Link>

        {/* Menu burger pour la navigation (composant externe) */}
        <VisitorBurgerMenu/>
      </div>
    </header>
  );
}
