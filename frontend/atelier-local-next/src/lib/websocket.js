// Importation des dépendances nécessaires
// SockJS : permet d'établir une connexion WebSocket compatible avec les navigateurs ne supportant pas WebSocket nativement.
// STOMP (Simple Text Oriented Messaging Protocol) : protocole utilisé pour gérer la communication par messages sur la socket.
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import getApiUrl from "./api";

// Variable globale pour conserver une instance unique du client STOMP
let stompClient = null;

/**
 * Établit une connexion WebSocket via STOMP avec le serveur backend.
 *
 * @param {string} jwtToken - Jeton JWT utilisé pour authentifier l'utilisateur auprès du serveur.
 * @param {function} onMessageReceived - Fonction callback appelée à chaque réception d'un nouveau message.
 * @returns {Client} L'instance du client STOMP (utile pour réutiliser la connexion).
 *
 * Cette fonction :
 * - Vérifie si une connexion active existe déjà (afin d’éviter les doublons).
 * - Initialise la connexion WebSocket en utilisant SockJS.
 * - Configure le client STOMP avec la gestion des reconnexions, l’authentification et l’abonnement aux messages personnels.
 */
export function connectWebSocket(jwtToken, onMessageReceived) {
  // Si une connexion STOMP est déjà active, on la réutilise.
  if (stompClient && stompClient.connected) return stompClient;

  // Création d'une connexion SockJS pointant vers l'endpoint WebSocket du backend.
  const socket = new SockJS(`${getApiUrl()}/ws`);

  // Initialisation du client STOMP avec configuration personnalisée.
  stompClient = new Client({
    // Définit la factory WebSocket à utiliser (ici SockJS)
    webSocketFactory: () => socket,

    // Active les logs pour faciliter le débogage
    debug: (str) => console.log("STOMP:", str),

    // Définit un délai de reconnexion automatique (en millisecondes)
    reconnectDelay: 5000,

    // En-têtes envoyés lors de la connexion (ici pour l’authentification JWT)
    connectHeaders: {
      Authorization: `Bearer ${jwtToken}`,
    },

    // Callback exécutée une fois la connexion établie avec succès
    onConnect: () => {
      console.log("STOMP connecté");

      // Abonnement à la file personnelle de l'utilisateur (messages destinés uniquement à lui)
      stompClient.subscribe("/user/queue/messages", (msg) => {
        // Conversion du corps du message JSON en objet JavaScript
        const data = JSON.parse(msg.body);

        // Appel du callback fourni par le composant appelant
        onMessageReceived(data);
      });
    },
  });

  // Activation de la connexion STOMP
  stompClient.activate();

  // Retourne le client STOMP pour une éventuelle utilisation ultérieure
  return stompClient;
}

/**
 * Ferme proprement la connexion WebSocket en désactivant le client STOMP.
 *
 * À utiliser lors de la déconnexion de l’utilisateur ou à la
 * destruction d’un composant React pour libérer les ressources.
 */
export function disconnectWebSocket() {
  if (stompClient) stompClient.deactivate();
}
