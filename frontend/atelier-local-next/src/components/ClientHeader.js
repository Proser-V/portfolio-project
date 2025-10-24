import Image from "next/image";
import logo from "../assets/logo/atelier-local-logo_white.png";
import Link from "next/link";
import ClientBurgerMenu from "@/components/ClientBurgerMenu";

/**
 * Composant d’en-tête affiché pour les clients connectés.
 *
 * @param {Object} props
 * @param {Object} props.client - Objet représentant le client connecté
 *
 * Ce composant :
 * - Affiche le logo de l’Atelier Local avec un lien vers la page d’accueil.
 * - Intègre le menu burger spécifique aux clients pour la navigation.
 * - Assure un style responsive et cohérent avec la charte graphique (fond bleu, texte doré, ombre).
 */
export default function ClientHeader({ client }) {
  return (
    <header className="bg-blue text-gold shadow-md relative pr-4 py-2 md:pr-0 md:py-0 md:items-center">
      <div className="flex items-center justify-between">
        {/* Logo */}
        <Link href="/" className="flex-shrink-0 pl-4">
          <Image
            src={logo}
            alt="Atelier Local Logo"
            className="h-12 w-auto"
          />
        </Link>

        {/* Burger Menu + Navigation */}
        <ClientBurgerMenu client={client} />
      </div>
    </header>
  );
}
