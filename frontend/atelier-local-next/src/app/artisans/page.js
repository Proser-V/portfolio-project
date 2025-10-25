import ArtisansPageClient from "@/components/ArtisansPageClient";
import getApiUrl from "@/lib/api";

/**
 * Récupère toutes les catégories d'artisans depuis l'API.
 * Fetch côté serveur avec cache désactivé pour toujours avoir les données à jour.
 * @returns {Promise<Array>} Tableau des catégories d'artisans ou [] en cas d'erreur
 */
async function getArtisanCategories() {
  try {
    const res = await fetch(
      `${getApiUrl()}/api/artisan-category/`,
      { cache: "no-store" }
    );
    if (!res.ok) return [];
    return await res.json();
  } catch (err) {
    console.error("Fetch categories error:", err);
    return [];
  }
}

/**
 * Récupère tous les artisans depuis l'API.
 * Fetch côté serveur avec cache désactivé.
 * @returns {Promise<Array>} Tableau des artisans ou [] en cas d'erreur
 */
async function getAllArtisans() {
  try {
    const res = await fetch(
      `${getApiUrl()}/api/artisans/`,
      { cache: "no-store" }
    );
    if (!res.ok) return [];
    const text = await res.text();
    return text ? JSON.parse(text) : [];
  } catch (err) {
    console.error("Fetch artisans error:", err);
    return [];
  }
}

/**
 * Page principale des artisans.
 * Effectue les fetchs côté serveur en parallèle et transmet les données initiales au composant client.
 * @returns {JSX.Element} Composant client ArtisansPageClient avec données initiales
 */
export default async function ArtisansPage() {
  // Fetch en parallèle côté serveur
  const [categories, artisans] = await Promise.all([
    getArtisanCategories(),
    getAllArtisans()
  ]);

  // Passe les données au composant client pour les filtres et l'affichage
  return (
    <ArtisansPageClient 
      initialCategories={categories}
      initialArtisans={artisans}
    />
  );
}

// Métadonnées pour le SEO
export const metadata = {
  title: "Artisans locaux - Atelier Local",
  description: "Découvrez les artisans qualifiés de Dijon et ses alentours.",
};
