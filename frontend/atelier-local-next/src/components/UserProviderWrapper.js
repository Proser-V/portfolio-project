"use client"; 
// Directive Next.js indiquant que ce composant s’exécute côté client
// (nécessaire car il utilise un contexte React et interagit avec des hooks)

import { UserProvider } from "@/context/UserContext";

/**
 * Composant enveloppe (wrapper) pour le contexte utilisateur.
 *
 * Ce composant :
 * - Fournit le contexte utilisateur (`UserContext`) à toute la hiérarchie enfant.
 * - Permet d’accéder facilement aux informations de l’utilisateur courant
 *   depuis n’importe quel composant de l’application via le hook `useUser()`.
 *
 * @param {Object} props
 * @param {Object|null} props.user - Données de l’utilisateur connecté (ou null si non connecté)
 * @param {React.ReactNode} props.children - Contenu enfant à englober par le provider
 *
 * Exemple d’utilisation :
 * <UserProviderWrapper user={user}>
 *   <App />
 * </UserProviderWrapper>
 */
export default function UserProviderWrapper({ user, children }) {
  // Fournit le contexte utilisateur à tous les composants enfants
  return <UserProvider user={user}>{children}</UserProvider>;
}
