"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { logout } from "utils/logout";

/**
 * @component LogoutPage
 * @description
 * Page de déconnexion du site **Atelier Local**.  
 * Lorsqu’un utilisateur accède à cette page :
 * 1. Le titre du document est mis à jour.
 * 2. La fonction `logout()` est appelée pour supprimer le jeton de session.
 * 3. L’utilisateur est ensuite redirigé automatiquement vers la page d’accueil.
 *
 * Cette page est exécutée côté client (`"use client"`) car elle manipule
 * le `window` et le `document`, ainsi que la navigation via `next/navigation`.
 *
 * @returns {JSX.Element} Message d’au revoir affiché pendant la déconnexion.
 */
export default function LogoutPage() {
  const router = useRouter();

  /**
   * Met à jour le titre de la page à l’affichage
   */
  useEffect(() => {
    document.title = "À bientôt dans l'Atelier Local";
  }, []);

  /**
   * Exécute la déconnexion dès le chargement de la page :
   * - Supprime les cookies/session côté client
   * - Redirige vers la page d’accueil après traitement
   */
  useEffect(() => {
    const doLogout = async () => {
      await logout(); // Appel à la fonction utilitaire de déconnexion
      window.location.href = "/"; // Redirection forcée vers l’accueil
    };

    doLogout();
  }, [router]);

  /**
   * Affichage temporaire pendant la déconnexion
   */
  return (
    <div className="flex items-center justify-center h-screen text-center text-xl p-4">
      Merci de votre visite,<br />à bientôt dans l'Atelier Local !
    </div>
  );
}
