import ArtisansPageClient from "@/components/ArtisansPageClient";
import getApiUrl from "@/lib/api";

// Fetch côté serveur
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

export default async function ArtisansPage() {
  // Fetch en parallèle côté serveur
  const [categories, artisans] = await Promise.all([
    getArtisanCategories(),
    getAllArtisans()
  ]);

  // Passe les données au composant client pour les filtres
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