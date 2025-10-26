"use client";
// Directive Next.js : ce composant utilise des hooks et du state, côté client

import ClientHome from "@/components/ClientHome";
import VisitorHome from "@/components/VisitorHome";
import ArtisanHome from "@/components/ArtisanHome";
import AdminHome from "@/components/AdminHome";
import { useSearchParams } from "next/navigation";
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

  // Effet pour afficher un toast si accès non autorisé
  useEffect(() => {
    if (error === "unauthorized") {
      // Création du toast
      const toast = document.createElement("div");
      toast.innerText = "Accès refusé : seuls les administrateurs peuvent accéder à cette page.";
      toast.style.cssText = `
        position: fixed; top: 20px; left: 50%; transform: translateX(-50%);
        background: #fee2e2; color: #dc2626; padding: 12px 24px; border-radius: 8px;
        border: 1px solid #fecaca; z-index: 9999; font-size: 14px; font-weight: 500;
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
      `;
      document.body.appendChild(toast);

      // Disparition automatique après 4s
      setTimeout(() => {
        toast.style.transition = "opacity 0.5s";
        toast.style.opacity = "0";
        setTimeout(() => document.body.removeChild(toast), 500);
      }, 4000);
    }
  }, [error]);

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
