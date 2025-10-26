// Import de la fonction utilitaire permettant de récupérer dynamiquement l’URL de base de l’API backend.
import getApiUrl from "@/lib/api";

/**
 * Déconnecte l’utilisateur actuellement authentifié.
 *
 * @returns {Promise<void>} - Ne retourne rien ; exécute la déconnexion côté serveur et supprime le cookie JWT côté client.
 *
 * Cette fonction :
 * - Envoie une requête POST au backend pour invalider la session côté serveur.
 * - Supprime le cookie JWT du navigateur afin d’empêcher toute reconnexion automatique.
 * - Affiche des messages de diagnostic dans la console pour faciliter le suivi.
 *
 * Notes techniques :
 * - L’option `credentials: "include"` permet d’envoyer les cookies avec la requête,
 *   indispensable pour que le serveur identifie correctement l’utilisateur connecté.
 * - La suppression du cookie est effectuée manuellement en redéfinissant sa durée de vie (`Max-Age=0`).
 * - Aucun rafraîchissement de page n’est déclenché ici : cette fonction doit être suivie d’une redirection manuelle côté front.
 */
export async function logout() {
  try {
    // Envoi d’une requête de déconnexion au backend
    const response = await fetch(`${getApiUrl()}/api/users/logout`, {
      method: "POST",
      credentials: "include", // Nécessaire pour transmettre les cookies existants
    });

    // Si la déconnexion serveur est réussie, on supprime le cookie JWT côté client
    if (response.ok) {
      document.cookie = "jwt=; Max-Age=0; path=/; SameSite=Strict";
      console.log("Logout successful, cookie removed");
    } else {
      console.error("Logout failed with status:", response.status);
    }
  } catch (err) {
    // Gestion des erreurs réseau ou d’exécution
    console.error("Erreur lors de la déconnexion : ", err);
  }
}
