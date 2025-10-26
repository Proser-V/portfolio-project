import Image from "next/image";
import logo from "../assets/logo/atelier-local-logo_white.png";
import Link from "next/link";
import AdminBurgerMenu from "@/components/AdminBurgerMenu";

/**
 * Composant AdminHeader
 * ---------------------
 * En-tête de la page pour un administrateur.
 * Affiche le logo et le menu burger pour la navigation.
 *
 * @param {Object} props - Propriétés du composant
 * @param {Object} props.admin - Administrateur connecté
 * @param {number} props.unreadCount - Nombre de messages non lus
 *
 * @returns {JSX.Element} Header administrateur
 */
export default function AdminHeader({ admin, unreadCount }) {
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

        {/* Menu burger et navigation */}
        <AdminBurgerMenu admin={admin} unreadCount={unreadCount} />
      </div>
    </header>
  );
}
