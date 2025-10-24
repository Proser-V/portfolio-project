import Image from "next/image";
import Link from "next/link";

/**
 * Page indiquant que la section est en construction.
 *
 * Ce composant :
 * - Affiche un lien de retour à l'accueil en haut de la page.
 * - Centré une image "Under Construction" prenant toute la hauteur disponible.
 * - S’adapte à la taille de la fenêtre pour rester responsive.
 *
 * Utilisation typique :
 * <UnderConstructionPage />
 */
export default function UnderConstructionPage() {
  return (
    <div className="flex flex-col w-full min-h-[calc(100vh-10rem)]">
      
      {/* Lien de navigation vers la page d’accueil */}
      <div className="p-4">
        <Link href="/" className="text-blue-600 hover:underline">
          ← Retour à l'accueil
        </Link>
      </div>

      {/* Image principale centrée dans la page */}
      <div className="relative flex-grow flex items-center justify-center">
        <Image
          src="/under-construction.png"
          alt="Under construction"
          fill // L’image remplit le conteneur parent
          className="object-contain max-h-full max-w-full" // Conserve les proportions sans déformation
          priority // Priorité de chargement pour améliorer le rendu
        />
      </div>
    </div>
  );
}
