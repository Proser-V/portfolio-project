import { Suspense } from "react";
import { getUser } from "@/lib/getUser";
import HomeContent from "@/components/HomeContent";
import getApiUrl from "@/lib/api";

export default async function Home() {
  const user = await getUser();
  let artisans = null;
  try {
    const res = await fetch(
      `${getApiUrl()}/api/artisans/random-top`,
      { cache: "no-store" }
    );
    
    if (res.ok) {
      artisans = await res.json();
    } else {
      console.error("Erreur lors du fetch des artisans");
    }
  } catch (err) {
    console.error("Erreur fetch artisans:", err);
  }
  
  return <HomeContent user={user} artisans={artisans} />;
}

// Métadonnées pour le SEO
export const metadata = {
  title: "Accueil - Atelier Local",
  description: "Le savoir faire à Dijon et ses alentours.",
};