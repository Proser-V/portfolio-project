"use client";
// Directive Next.js : ce composant utilise des hooks et du state, côté client

import ClientHome from "@/components/ClientHome";
import VisitorHome from "@/components/VisitorHome";
import ArtisanHome from "@/components/ArtisanHome";
import AdminHome from "@/components/AdminHome";
import { useSearchParams } from "next/navigation";
import { useToast } from "@/context/ToastContext";
import { useEffect } from "react";

/**
 * Composant principal pour afficher le contenu de la page d'accueil
 * selon le type d'utilisateur connecté.
 *
 * Fonctionnalités :
 * - Affichage conditionnel selon le rôle : admin / client / artisan / visiteur
 * - Gestion d'un message d'erreur pour accès non autorisé via query param
 *
 * Props :
 * @param {Object|null} user - Utilisateur connecté, null si visiteur
 * @param {Array} artisans - Liste des artisans à afficher (pour tous les types)
 */
export default function HomeContent({ user, artisans }) {
  const searchParams = useSearchParams(); // Hook pour récupérer les query params
  const error = searchParams.get("error"); // Lecture du paramètre d'erreur
  const { addToast } = useToast();

  // Effet pour afficher un toast si accès non autorisé
  useEffect(() => {
    if (error === "unauthorized") {
      addToast(
        "Accès refusé : Vous n'êtes pas autorisé à accéder à cette page",
        "error"
      );
    }
  }, [error, addToast]);

  // Si aucun utilisateur connecté => affichage visiteur
  if (!user) return <VisitorHome artisans={artisans} />;

  // Affichage selon le rôle de l'utilisateur
  switch (user.role) {
    case "admin":
      return <AdminHome admin={user} artisans={artisans} />;
    case "client":
      return <ClientHome client={user} artisans={artisans} />;
    case "artisan":
      return <ArtisanHome artisan={user} artisans={artisans} />;
    default:
      return <VisitorHome artisans={artisans} />;
  }
}
