import Link from "next/link";
import { redirect } from "next/navigation";
import AdminClient from "@/components/AdminClient";
import getApiUrl from "@/lib/api";
import { getUser } from "@/lib/getUser";

/**
 * Récupère la liste des artisans depuis l'API.
 * @returns {Promise<{data: Array, error: string|null}>} Les artisans ou une erreur
 */
async function fetchArtisans() {
  try {
    const response = await fetch(`${getApiUrl()}/api/artisans/`, {
      method: "GET",
      credentials: "include",
    });
    if (response.ok) {
      return { data: await response.json(), error: null };
    }
    return { data: [], error: "Erreur lors de la récupération des artisans" };
  } catch (err) {
    return { data: [], error: "Erreur réseau, impossible de récupérer les artisans" };
  }
}

/**
 * Récupère les catégories d'artisans depuis l'API.
 * @returns {Promise<{data: Array, error: string|null}>} Les catégories ou une erreur
 */
async function fetchArtisanCategories() {
  try {
    const response = await fetch(`${getApiUrl()}/api/artisan-category/`, {
      method: "GET",
      credentials: "include",
    });
    if (response.ok) {
      return { data: await response.json(), error: null };
    }
    return { data: [], error: "Erreur lors de la récupération des catégories" };
  } catch (err) {
    return { data: [], error: "Erreur réseau, impossible de récupérer les catégories" };
  }
}

/**
 * Récupère les catégories d'événements depuis l'API.
 * @returns {Promise<{data: Array, error: string|null}>} Les catégories ou une erreur
 */
async function fetchEventCategories() {
  try {
    const response = await fetch(`${getApiUrl()}/api/event-categories/`, {
      method: "GET",
      credentials: "include",
    });
    if (response.ok) {
      return { data: await response.json(), error: null };
    }
    return { data: [], error: "Erreur lors de la récupération des catégories" };
  } catch (err) {
    return { data: [], error: "Erreur réseau, impossible de récupérer les catégories" };
  }
}

/**
 * Page principale du panneau d'administration.
 * Vérifie le rôle admin et fournit les données initiales au composant client.
 * @returns {JSX.Element} Composant React du panneau admin
 */
export default async function AdminPanel() {
  // Vérification que l'utilisateur est admin
  const user = await getUser();
  if (!user || user.role !== "admin") {
    redirect("/?error=unauthorized");
  }

  // Récupération des données côté serveur
  const { data: artisans, error: artisansError } = await fetchArtisans();
  const { data: artisanCategories, error: artisanCategoriesError } = await fetchArtisanCategories();
  const { data: eventCategories, error: eventCategoriesError } = await fetchEventCategories();

  const error = artisansError || artisanCategoriesError || eventCategoriesError;

  return (
    <div className="mt-4 items-center justify-center w-full">
      <div className="text-center text-blue text-xl mb-4">Panneau d'administration</div>

      {error && (
        <div className="h-5 flex justify-center items-center mb-6">
          <p className="text-red-500 text-sm text-center">{error}</p>
        </div>
      )}

      <AdminClient
        initialArtisans={artisans}
        initialArtisanCategories={artisanCategories}
        initialEventCategories={eventCategories}
        currentUser={user}
      />
    </div>
  );
}
