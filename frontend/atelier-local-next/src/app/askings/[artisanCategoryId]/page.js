import AskingsPageClient from "@/components/AskingsPageClient";
import { getUser } from "@/lib/getUser";
import getApiUrl from "@/lib/api";
import { redirect } from "next/navigation";

/**
 * Récupère toutes les demandes ("askings") associées à une catégorie d'artisan.
 *
 * @param {string|number} artisanCategoryId - ID de la catégorie d'artisan.
 * @returns {Promise<Array>} Tableau des demandes, vide en cas d'erreur.
 */
async function getAllAskingsByCategory(artisanCategoryId) {
  try {
    const res = await fetch(
      `${getApiUrl()}/api/artisan-category/${artisanCategoryId}/askings`,
      { cache: "no-store" }
    );

    if (!res.ok) {
      const errorText = await res.text();
      throw new Error(errorText || "Erreur lors de la récupération des demandes.");
    }

    const data = await res.json();
    return data;
  } catch (err) {
    console.error("Fetch askings error:", err);
    return [];
  }
}

/**
 * Récupère les informations d'une catégorie d'artisan.
 *
 * @param {string|number} artisanCategoryId - ID de la catégorie d'artisan.
 * @returns {Promise<Object>} Objet contenant les infos de la catégorie, ou {} en cas d'erreur.
 */
async function getArtisanCategory(artisanCategoryId) {
  try {
    const res = await fetch(
      `${getApiUrl()}/api/artisan-category/${artisanCategoryId}`,
      { cache: "no-store" }
    );

    if (!res.ok) return {};
    return await res.json();
  } catch (err) {
    console.error("Fetch categories error:", err);
    return {};
  }
}

/**
 * Page des demandes pour une catégorie d'artisan.
 *
 * Cette page récupère les demandes des habitants liées à la catégorie,
 * vérifie que l'utilisateur connecté est un artisan,
 * et affiche les données via le composant AskingsPageClient.
 *
 * @param {Object} params - Paramètres de la route Next.js.
 * @param {string|number} params.artisanCategoryId - ID de la catégorie d'artisan.
 * @returns {JSX.Element} Composant React affichant les demandes.
 */
export default async function AskingsPage({ params }) {
  const { artisanCategoryId } = await params;
  const user = await getUser();

  // Redirection si l'utilisateur n'est pas un artisan
  if (!user || user.role !== "artisan") {
    redirect("/login");
  }

  // Récupération simultanée des demandes et de la catégorie
  const [data, artisanCategoryData] = await Promise.all([
    getAllAskingsByCategory(artisanCategoryId),
    getArtisanCategory(artisanCategoryId)
  ]);

  const artisanCategoryName = artisanCategoryData.name;

  // Rendu du composant client avec les données initiales
  return (
    <AskingsPageClient initialAskings={data} category={artisanCategoryName} />
  );
}

/**
 * Métadonnées SEO pour la page
 */
export const metadata = {
  title: "Demandes des habitants - Atelier Local",
  description: "Artisans, répondez aux demandes des habitants",
};
