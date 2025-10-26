// Import de la fonction utilitaire permettant de récupérer l’URL de base de l’API.
import getApiUrl from "./api";

/**
 * Marque un message spécifique comme lu côté serveur.
 *
 * @param {number|string} messageId - Identifiant unique du message à marquer comme lu.
 * @param {string} jwtToken - Jeton JWT pour l’authentification de la requête.
 * @returns {Promise<boolean>} - Retourne `true` si l’opération a réussi, `false` sinon.
 *
 * Cette fonction envoie une requête HTTP POST vers l’API afin d’indiquer
 * qu’un message a été lu par le destinataire. Elle est typiquement appelée
 * lorsqu’un utilisateur ouvre une conversation ou consulte ses messages.
 */
export async function markMessageAsRead(messageId, jwtToken) {
  try {
    const response = await fetch(`${getApiUrl()}/api/messages/${messageId}/read`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...(jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}),
      },
      credentials: "include",
    });

    // Retourne true si la requête s’est bien déroulée (code HTTP 200-299)
    return response.ok;
  } catch (error) {
    console.error("Erreur lors du marquage du message comme lu:", error);
    return false;
  }
}

/**
 * Récupère tous les messages non lus de l’utilisateur actuellement authentifié.
 *
 * @param {string} jwtToken - Jeton JWT pour authentifier la requête.
 * @returns {Promise<Array>} - Tableau de messages non lus (vide si aucun message ou en cas d’erreur).
 *
 * Cette fonction interroge l’API pour obtenir la liste des messages marqués comme non lus.
 * Les en-têtes et les options garantissent la sécurité et la cohérence des données.
 */
export async function getUnreadMessages(jwtToken) {
  try {
    const response = await fetch(`${getApiUrl()}/api/messages/unread`, {
      headers: {
        ...(jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}),
      },
      credentials: "include",
      cache: "no-store", // Évite d’utiliser une version en cache des données
    });

    if (response.ok) {
      const data = await response.json();
      return Array.isArray(data) ? data : [];
    }

    // Si aucun message non lu n’est trouvé (HTTP 404), on retourne un tableau vide.
    if (response.status === 404) {
      return [];
    }

    return [];
  } catch (error) {
    console.error("Erreur lors de la récupération des messages non lus:", error);
    return [];
  }
}

/**
 * Calcule le nombre total de messages non lus pour un utilisateur donné.
 *
 * @param {Array} unreadMessages - Liste complète des messages non lus.
 * @param {number|string} currentUserId - Identifiant de l’utilisateur connecté.
 * @returns {number} - Nombre total de messages non lus destinés à cet utilisateur.
 */
export function getTotalUnreadCount(unreadMessages, currentUserId) {
  return unreadMessages.filter((msg) => msg.receiverId === currentUserId).length;
}

/**
 * Calcule le nombre de messages non lus pour chaque expéditeur distinct.
 *
 * @param {Array} unreadMessages - Liste complète des messages non lus.
 * @param {number|string} currentUserId - Identifiant de l’utilisateur connecté.
 * @returns {Object} - Objet sous la forme { senderId: nombreDeMessagesNonLus }.
 *
 * Cette fonction permet d’afficher, par exemple, un badge de notification
 * pour chaque conversation distincte.
 */
export function countUnreadByUser(unreadMessages, currentUserId) {
  const counts = {};

  unreadMessages.forEach((msg) => {
    if (msg.receiverId === currentUserId && msg.isRead === false) {
      const senderId = msg.senderId;
      counts[senderId] = (counts[senderId] || 0) + 1;
    }
  });

  return counts;
}

/**
 * Marque comme lus tous les messages d’une conversation donnée pour un utilisateur.
 *
 * @param {Array} messages - Liste complète des messages de la conversation.
 * @param {number|string} currentUserId - Identifiant de l’utilisateur connecté.
 * @param {string} jwtToken - Jeton JWT pour l’authentification de l’utilisateur.
 *
 * Cette fonction identifie les messages non lus dans une conversation spécifique,
 * puis envoie une requête de marquage pour chacun d’eux en parallèle.
 */
export async function markConversationAsRead(messages, currentUserId, jwtToken) {
  // Filtrage des messages non lus pour l’utilisateur courant
  const unreadMessages = messages.filter(
    (msg) => msg.receiverId === currentUserId && !msg.isRead
  );

  // Exécution parallèle de toutes les requêtes de mise à jour
  const promises = unreadMessages.map((msg) => markMessageAsRead(msg.id, jwtToken));

  await Promise.all(promises);
}
