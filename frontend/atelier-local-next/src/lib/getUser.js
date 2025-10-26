// Importation des dépendances nécessaires
// - cookies : API Next.js pour accéder aux cookies côté serveur
// - getApiUrl : fonction utilitaire renvoyant l’URL de base de l’API backend
import { cookies } from "next/headers";
import getApiUrl from "./api";

/**
 * Récupère les informations de l’utilisateur actuellement connecté à partir du cookie JWT.
 *
 * @returns {Promise<object|null>} - Retourne un objet utilisateur enrichi (avec son rôle et le token JWT)
 *                                  ou `null` si aucun utilisateur n’est authentifié.
 *
 * Cette fonction :
 * - Extrait le cookie JWT stocké côté serveur (grâce à Next.js).
 * - Construit l’en-tête `Cookie` complet pour la requête.
 * - Fait un appel à l’API `/api/users/me` afin d’obtenir les informations de l’utilisateur.
 * - Retourne un objet utilisateur formaté pour une utilisation directe dans l’application.
 */
export async function getUser() {
  // Récupération de tous les cookies disponibles côté serveur
  const cookieStore = await cookies();

  // Extraction du token JWT stocké dans le cookie "jwt"
  const token = cookieStore.get("jwt")?.value;

  // Si aucun token n’est présent, on considère que l’utilisateur n’est pas connecté
  if (!token) return null;

  // Construction de la chaîne d’en-tête "Cookie"
  const cookieHeader = cookieStore
    .getAll()
    .map((c) => `${c.name}=${c.value}`)
    .join("; ");

  try {
    // Requête au backend pour obtenir les informations de l’utilisateur connecté
    const res = await fetch(`${getApiUrl()}/api/users/me`, {
      method: "GET",
      headers: {
        Cookie: cookieHeader, // Transmission des cookies pour authentification
      },
      cache: "no-store", // Empêche la mise en cache pour garantir des données à jour
    });

    // Si la réponse est valide (HTTP 200), on parse le JSON et on reformate les données
    if (res.ok) {
      const data = await res.json();

      return {
        ...data.user, // Données utilisateur principales
        role: data.role?.toLowerCase() || data.user.role?.toLowerCase(), // Normalisation du rôle
        jwtToken: token, // Ajout du token pour usage ultérieur
      };
    }
  } catch (err) {
    // Gestion des erreurs de communication avec le serveur
    console.error("Erreur lors de la récupération de l'utilisateur : ", err);
  }

  // En cas d’échec ou d’absence de données, on retourne null
  return null;
}
