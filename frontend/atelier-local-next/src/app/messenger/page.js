import { cookies } from "next/headers";
import { getUser } from "@/lib/getUser";
import MessengerList from "@/components/MessengerList";
import getApiUrl from "@/lib/api";

export const dynamic = "force-dynamic";

/**
 * @function fetchUnreadMessages
 * @async
 * @description
 * Récupère la liste des messages non lus pour l'utilisateur connecté.
 * La requête inclut le token JWT dans les en-têtes si disponible.
 *
 * @param {string} [jwtToken] - Jeton JWT de l'utilisateur pour authentifier la requête.
 * @returns {Promise<Array>} Tableau contenant les messages non lus (ou un tableau vide en cas d'erreur).
 */
async function fetchUnreadMessages(jwtToken) {
    try {
        // Appel à l’API backend pour récupérer les messages non lus
        const response = await fetch(
            `${getApiUrl()}/api/messages/unread`,
            {
                headers: {
                    ...(jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}),
                },
                credentials: "include",
                cache: "no-store", // Désactive le cache pour garantir des données à jour
            }
        );

        // Si la réponse est valide, on retourne les données JSON
        if (response.ok) {
            const data = await response.json();
            return Array.isArray(data) ? data : [];
        }

        // Si aucune ressource trouvée, retourne un tableau vide
        if (response.status === 404) {
            return [];
        }

        // Retourne un tableau vide pour toute autre réponse non OK
        return [];
    } catch (error) {
        // Gestion des erreurs réseau ou d’exécution
        console.error("Erreur lors de la récupération des messages non lus:", error);
        return [];
    }
}

/**
 * @function MessengerPage
 * @async
 * @description
 * Page principale de la messagerie utilisateur.  
 * Elle récupère les informations de l'utilisateur, les conversations associées et les messages non lus,  
 * puis affiche la liste via le composant `MessengerList`.
 *
 * @returns {Promise<JSX.Element>} Composant JSX affichant la messagerie ou un message d’erreur/session expirée.
 */
export default async function MessengerPage() {
  // Récupère les cookies côté serveur (Next.js 13+)
  const cookieStore = await cookies();
  const jwt = cookieStore.get("jwt")?.value;

  // Récupération de l'utilisateur actuellement connecté
  const user = await getUser();

  // Vérifie la validité de la session utilisateur
  if (!user || !user.id) {
      return (
          <div className="mt-20 text-center text-red-500">
              Session expirée - <a href="/login" className="underline text-blue-600">Veuillez vous reconnecter</a>.
          </div>
      );
  }

  // Tableau qui contiendra les conversations de l'utilisateur
  let conversations = [];
  try {
      // Appel à l'API pour récupérer toutes les conversations associées à l'utilisateur
      const convRes = await fetch(
          `${getApiUrl()}/api/messages/conversations/${user.id}`,
          {
              headers: {
                  Authorization: jwt ? `Bearer ${jwt}` : "",
              },
              credentials: "include",
              cache: "no-store", // Données toujours fraîches
          }
      );

      const data = await convRes.json();

      // Si l'API renvoie une erreur, on affiche un message explicite à l'utilisateur
      if (!convRes.ok) {
          return (
              <div className="mt-20 text-center text-red-500">
                  Erreur de chargement des conversations : {convRes.status === 403 ? "Accès non autorisé" : `Erreur ${convRes.status}`}
              </div>
          );
      }

      // Stocke la liste des conversations si le format est valide
      conversations = Array.isArray(data) ? data : [];
  } catch (err) {
      // En cas d’erreur de communication avec l’API, on affiche une alerte à l’utilisateur
      console.error("Erreur lors de la récupération des conversations:", err);
      return (
          <div className="mt-20 text-center text-red-500">
              Erreur de chargement des conversations : {err.message}
          </div>
      );
  }

  // Récupération des messages non lus pour le badge de notification
  const unreadMessages = await fetchUnreadMessages(jwt);

  // Affichage du contenu principal de la page messagerie
  return (
      <div>
          <main className="max-w-4xl mx-auto px-4">
              <h1 className="text-center text-blue text-2xl font-cabin font-normal mb-0">
                  Vos messages
              </h1>

              {/* Liste des conversations avec pagination et gestion des messages non lus */}
              <MessengerList 
                  initialConversations={conversations} 
                  conversationsPerPage={10}
                  currentUserId={user.id}
                  initialUnreadMessages={unreadMessages} // ✅ Transmet la liste récupérée
              />
          </main>
      </div>
  );
}
