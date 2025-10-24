"use client";
// Directive Next.js : ce composant utilise des hooks ou du state côté client

import ClientHeader from "./ClientHeader";
import ArtisanHeader from "./ArtisanHeader";
import VisitorHeader from "./VisitorHeader";
import AdminHeader from "./AdminHeader";

/**
 * Composant qui sélectionne dynamiquement le header à afficher
 * selon le rôle de l'utilisateur connecté.
 *
 * Fonctionnalités :
 * - Affichage conditionnel : admin / client / artisan / visiteur
 *
 * Props :
 * @param {Object|null} user - Utilisateur connecté, null si visiteur
 */
export default function HeaderByRole({ user }) {
  if (user?.role === "admin") return <AdminHeader admin={user} />;       // Header admin
  if (user?.role === "client") return <ClientHeader client={user} />;    // Header client
  if (user?.role === "artisan") return <ArtisanHeader artisan={user} />; // Header artisan
  return <VisitorHeader />;                                               // Header visiteur par défaut
}
