import { cookies } from "next/headers";
import { getUser } from "@/lib/getUser";
import ConversationClient from "@/components/ConversationClient";
import getApiUrl from "@/lib/api";

export const dynamic = "force-dynamic";

/**
 * @function ConversationPage
 * @async
 * @description
 * Page serveur de la conversation entre l'utilisateur connecté et un autre utilisateur (client ou artisan).  
 * Elle récupère :
 * - Les informations de session de l'utilisateur courant  
 * - L'historique des messages entre les deux utilisateurs  
 * - Les informations de profil de l'interlocuteur (artisan ou client)
 *
 * @param {Object} params - Paramètres dynamiques provenant de l'URL.
 * @param {string} params.userId - Identifiant de l'autre utilisateur dans la conversation.
 * @returns {Promise<JSX.Element>} Composant JSX affichant la conversation ou un message d’erreur/Vous n'êtes pas connectés.
 */
export default async function ConversationPage({ params }) {
  // Récupération du cookie JWT côté serveur
  const cookieStore = await cookies();
  const jwt = cookieStore.get("jwt")?.value;

  // Récupération des informations de l'utilisateur actuellement connecté
  const user = await getUser();

  // Si la session est invalide ou expirée, invite à se reconnecter
  if (!user || !user.id) {
    return (
      <div className="mt-20 text-center text-blue">
        Vous n'êtes pas connecté -{" "}
        <a href="/login" className="underline text-blue">
          Veuillez vous connecter
        </a>
        .
      </div>
    );
  }

  // Extraction du paramètre userId depuis l'URL
  const { userId } = await params;
  const otherUserId = userId;

  /**
   * Étape 1 : Récupération de l'historique des messages
   */
  const messagesRes = await fetch(
    `${getApiUrl()}/api/messages/history?user1Id=${user.id}&user2Id=${otherUserId}`,
    {
      headers: jwt ? { Cookie: `jwt=${jwt}` } : {},
      credentials: "include",
      cache: "no-store", // Toujours récupérer les messages à jour
    }
  );

  // Variables pour stocker les données de la conversation
  let messages = [];
  let otherUserName = "Utilisateur inconnu";
  let otherUser = null;

  /**
   * Étape 2 : Tentative de récupération des informations de l'autre utilisateur
   * (l'API peut renvoyer un artisan ou un client selon son rôle)
   */
  try {
    let role = null;

    // 🔹 Tentative 1 : Vérifie si l’autre utilisateur est un artisan
    const artisanRes = await fetch(
      `${getApiUrl()}/api/artisans/${otherUserId}`,
      {
        headers: jwt ? { Cookie: `jwt=${jwt}` } : {},
        credentials: "include",
        cache: "no-store",
      }
    );

    if (artisanRes.ok) {
      otherUser = await artisanRes.json();
      role = "artisan";
    } else {
      // 🔹 Tentative 2 : Sinon, essaie comme client
      const clientRes = await fetch(
        `${getApiUrl()}/api/clients/${otherUserId}`,
        {
          headers: jwt ? { Cookie: `jwt=${jwt}` } : {},
          credentials: "include",
          cache: "no-store",
        }
      );

      if (clientRes.ok) {
        otherUser = await clientRes.json();
        role = "client";
      }
    }

    // Si on a bien trouvé un utilisateur et son rôle, on détermine le nom à afficher
    if (otherUser && role) {
      if (role === "artisan") {
        otherUserName = otherUser.name || "Artisan inconnu";
      } else if (role === "client") {
        otherUserName = `${otherUser.firstName || ""} ${otherUser.lastName || ""}`.trim() || "Client inconnu";
      }
    } else {
      console.warn(`Impossible de récupérer les infos de l'utilisateur avec l'ID ${otherUserId}`);
    }
  } catch (e) {
    console.error("Erreur lors de la récupération de l'autre utilisateur :", e);
  }

  /**
   * Étape 3 : Traitement de la réponse des messages
   */
  if (messagesRes.ok) {
    const data = await messagesRes.json();
    messages = Array.isArray(data) ? data : [];
  } else {
    // En cas d'erreur de récupération, affichage d’un message d’erreur simple
    return (
      <div className="mt-20 text-center text-red-500">
        Erreur de chargement de la conversation.
      </div>
    );
  }

  /**
   * Étape 4 : Affichage du contenu principal de la page de conversation
   */
  return (
    <div className="">
      <main className="w-[90vw] md:w-[45vw] mx-auto p-4">
        <h1 className="text-center text-blue text-xl font-semibold mb-6">
          Votre fil de discussion avec {otherUserName}
        </h1>

        {/* Composant client gérant l’affichage et l’envoi des messages */}
        <ConversationClient
          initialMessages={messages}
          user={user}
          otherUser={otherUser}
          otherUserName={otherUserName}
          jwtToken={jwt}
        />
      </main>
    </div>
  );
}
