import ArtisansPageClient from "@/components/ArtisansPageClient";

// Fetch côté serveur
async function getArtisanCategories() {
  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/artisan-category/`,
      { cache: "no-store" } // ou { next: { revalidate: 3600 } } pour cache 1h
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
      `${process.env.NEXT_PUBLIC_API_URL}/api/artisans/`,
      { cache: "no-store" } // ou { next: { revalidate: 600 } } pour cache 10min
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