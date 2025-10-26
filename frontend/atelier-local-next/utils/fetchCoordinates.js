import getApiUrl from "@/lib/api";

/**
 * Convertit une adresse postale en coordonnées GPS (latitude et longitude)
 * en interrogeant le service de géocodage du backend.
 *
 * @param {string} address - Adresse textuelle à géocoder.
 * @returns {Promise<{ latitude: number|null, longitude: number|null }>} -
 *          Un objet contenant les coordonnées géographiques, ou des valeurs nulles en cas d’erreur.
 *
 * Cette fonction :
 * - Envoie une requête HTTP POST vers l’endpoint `/api/geocode` du backend.
 * - Le backend se charge d’interroger un service de géocodage (ex : Nominatim, Google Maps API...).
 * - Retourne les coordonnées extraites si la requête aboutit, ou des valeurs nulles en cas d’échec.
 *
 * Notes techniques :
 * - L’URL de l’API est définie via la variable d’environnement `_API_URL`.
 * - Le corps de la requête contient l’adresse en JSON.
 * - Un `try/catch` gère les erreurs réseau ou serveur pour éviter les crashs côté client.
 */
const fetchCoordinates = async (address) => {
  // Si aucune adresse n’est fournie, on retourne des valeurs nulles par défaut.
  if (!address) return { latitude: null, longitude: null };

  try {
    // Requête vers le service backend de géocodage
    const apiUrl = getApiUrl(); // Appelez la fonction pour obtenir l'URL
    console.log("API URL:", apiUrl); // Pour déboguer
    const response = await fetch(`${apiUrl}/api/geocode`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ address }),
    });

    // Vérification du statut de la réponse
    if (!response.ok) throw new Error("Erreur geocoding");

    // Lecture et retour des données renvoyées par le backend ({ latitude, longitude })
    const data = await response.json();
    return data;
  } catch (err) {
    // Gestion des erreurs réseau ou serveur
    console.error("Erreur geocoding :", err);
    return { latitude: null, longitude: null };
  }
};

export default fetchCoordinates;
