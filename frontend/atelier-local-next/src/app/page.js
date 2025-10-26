import { Suspense } from "react";
import { getUser } from "@/lib/getUser";
import HomeContent from "@/components/HomeContent";
import getApiUrl from "@/lib/api";

/**
 * @function Home
 * @async
 * @description
 * Composant serveur principal de la page d'accueil.  
 * Il récupère les informations de l'utilisateur connecté ainsi qu'une sélection
 * aléatoire d'artisans "top" via l'API, puis passe ces données au composant `HomeContent`.
 * 
 * @returns {Promise<JSX.Element>} Le contenu JSX de la page d'accueil.
 */
export default async function Home() {
  // Récupération de l'utilisateur connecté (ou null si non authentifié)
  const user = await getUser();

  // Déclaration de la variable qui contiendra la liste des artisans
  let artisans = null;

  try {
    // Appel à l'API backend pour récupérer une liste aléatoire d'artisans "top"
    const res = await fetch(
      `${getApiUrl()}/api/artisans/random-top`,
      { cache: "no-store" } // Désactive le cache pour toujours obtenir des données fraîches
    );

    // Vérifie si la réponse est valide
    if (res.ok) {
      // Parse la réponse JSON pour obtenir la liste des artisans
      artisans = await res.json();
    } else {
      // En cas de code HTTP non OK, logge une erreur côté serveur
      console.error("Erreur lors du fetch des artisans");
    }
  } catch (err) {
    // Capture et logge les erreurs réseau ou exceptions inattendues
    console.error("Erreur fetch artisans:", err);
  }

  // Retourne le composant principal de la page avec les données récupérées
  return <HomeContent user={user} artisans={artisans} />;
}

/**
 * Métadonnées de la page pour le SEO et les réseaux sociaux.
 * Ces informations sont utilisées par Next.js pour générer les balises <head>.
 */
export const metadata = {
  title: "Accueil - Atelier Local", // Titre de la page
  description: "Le savoir faire à Dijon et ses alentours.", // Description utilisée pour le référencement
};
