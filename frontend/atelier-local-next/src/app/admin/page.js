import Link from "next/link";
import { redirect } from "next/navigation";
import AdminClient from "@/components/AdminClient";
import { getUser } from "@/lib/getUser";

// Récupérer les artisans
async function fetchArtisans() {
  try {
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/artisans/`, {
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

// Récupérer les catégories d'artisan
async function fetchArtisanCategories() {
  try {
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/artisan-category/`, {
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

// Récupérer les catégories d'évènement
async function fetchEventCategories() {
  try {
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/event-categories/`, {
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

export default async function AdminPanel() {
  // Vérifier si l'utilisateur est admin avec getUser
  const user = await getUser();
  if (!user || user.role !== "admin") {
    redirect("/login");
  }

  // Récupérer les données côté serveur
  const { data: artisans, error: artisansError } = await fetchArtisans();
  const { data: artisanCategories, error: artisanCategoriesError } = await fetchArtisanCategories();
  const { data: eventCategories, error: eventCategoriesError } = await fetchEventCategories();

  const error = artisansError || artisanCategoriesError || eventCategoriesError;

  return (
    <div className="mt-4 items-center justify-center">
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
        />
    </div>
  );
}