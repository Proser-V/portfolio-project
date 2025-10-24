"use client"; 
// Directive Next.js : indique que ce module s’exécute côté client

import { createContext, useContext } from "react";

/**
 * Contexte global de l’utilisateur.
 *
 * Permet de partager les informations de l’utilisateur connecté (profil, rôle, jeton JWT, etc.)
 * à travers toute l’application React sans avoir à passer les props manuellement.
 */
export const UserContext = createContext(null);

/**
 * Fournisseur du contexte utilisateur.
 *
 * @param {object} props
 * @param {object|null} props.user - Objet représentant l’utilisateur connecté, ou `null` si aucun utilisateur n’est authentifié.
 * @param {React.ReactNode} props.children - Composants enfants pouvant accéder au contexte utilisateur.
 *
 * @returns {JSX.Element} - Composant Provider encapsulant les enfants avec la valeur du contexte utilisateur.
 *
 * Exemple d’utilisation :
 * ```jsx
 * <UserProvider user={currentUser}>
 *   <Dashboard />
 * </UserProvider>
 * ```
 *
 * Tous les composants enfants peuvent ensuite récupérer les infos utilisateur via le hook `useUser()`.
 */
export const UserProvider = ({ user, children }) => (
  <UserContext.Provider value={user}>{children}</UserContext.Provider>
);

/**
 * Hook personnalisé pour accéder facilement au contexte utilisateur.
 *
 * @returns {object|null} - Retourne les informations de l’utilisateur connecté ou `null` si aucun utilisateur n’est défini.
 *
 * Exemple d’utilisation :
 * ```jsx
 * const user = useUser();
 * console.log(user?.role);
 * ```
 */
export const useUser = () => useContext(UserContext);
