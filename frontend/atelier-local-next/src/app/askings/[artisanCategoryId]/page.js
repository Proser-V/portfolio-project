import AskingsPageClient from "@/components/AskingsPageClient";
import { getUser } from "@/lib/getUser";

async function getAllAskingsByCategory(artisanCategoryId) {
  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/artisan-category/${artisanCategoryId}/askings`,
      { cache: "no-store" }
    );
    if (!res.ok){
        const errorText = await res.text();
        throw new Error(errorText || "Erreur lors de la récupération des demandes.");
    };

    const data = await res.json();

    return data;
  } catch (err) {
    console.error("Fetch askings error:", err);
    return [];
  }
}

async function getArtisanCategory(artisanCategoryId) {
  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/artisan-category/${artisanCategoryId}`,
      { cache: "no-store" }
    );
    if (!res.ok) return {};
    return await res.json();
  } catch (err) {
    console.error("Fetch categories error:", err);
    return {};
  }
}

export default async function AskingsPage({ params }) {
  const { artisanCategoryId } = await params;
  const user = await getUser();

    if (!user || user.role !== "artisan") {
    redirect("/login");
  }

  const [data, artisanCategoryData] = await Promise.all([
    getAllAskingsByCategory(artisanCategoryId),
    getArtisanCategory(artisanCategoryId)
  ]);

  const artisanCategoryName = artisanCategoryData.name;

  return (
    <AskingsPageClient initialAskings={data} category={artisanCategoryName}/>
  );
}

// Métadonnées pour le SEO
export const metadata = {
  title: "Demandes des habitants - Atelier Local",
  description: "Artisans, répondez aux demandes des habitants",
};