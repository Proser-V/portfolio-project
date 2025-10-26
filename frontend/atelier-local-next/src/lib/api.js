/**
 * Retourne dynamiquement l’URL de base de l’API selon l’environnement d’exécution (serveur ou client).
 *
 * @returns {string} - L’URL de base de l’API à utiliser pour les requêtes HTTP.
 *
 * Cette fonction a pour objectif de garantir que les appels à l’API backend
 * utilisent toujours la bonne URL, que le code soit exécuté côté serveur (SSR)
 * ou côté client (navigateur).
 *
 * 🔹 Côté serveur :
 *   - `window` n’est pas défini, donc on lit la variable d’environnement `NEXT_PUBLIC_API_URL`
 *     (configurable dans un fichier `.env`).
 *   - Si cette variable n’existe pas, l’URL par défaut est `http://localhost:8080`.
 *
 * 🔹 Côté client :
 *   - On construit dynamiquement l’URL à partir du protocole et du nom d’hôte
 *     de la page courante, puis on ajoute le port `8080`.
 *   - Cela permet d’éviter les erreurs liées à des environnements différents
 *     (développement local, préproduction, production).
 */
const getApiUrl = () => {
  // Détection de l’environnement (SSR vs navigateur)
  if (typeof window === 'undefined') {
    // Exécution côté serveur : on utilise la variable d’environnement ou un fallback local
    return process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
  }

  // Exécution côté client : on reconstruit dynamiquement l’URL en fonction du domaine actuel
  return `${window.location.protocol}//${window.location.hostname}:8080`;
};

export default getApiUrl;