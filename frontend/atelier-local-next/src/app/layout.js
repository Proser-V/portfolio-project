import VisitorHeader from "../components/VisitorHeader";
import ClientHeader from "../components/ClientHeader";
import ArtisanHeader from "../components/ArtisanHeader";
import AdminHeader from "../components/AdminHeader";
import Footer from "../components/Footer";
import Image from "next/image";
import "./globals.css";
import UserProviderWrapper from "../components/UserProviderWrapper";
import React from "react";
import { getUser } from "@/lib/getUser";
import { UnreadMessagesProvider } from "@/components/UnreadMessageProvider";
import HeaderByRole from "@/components/HeaderByRole";
import getApiUrl from "@/lib/api";

// Force le rendu dynamique de la page pour éviter la mise en cache côté serveur
export const dynamic = "force-dynamic";

/**
 * @async
 * @function fetchUnreadMessages
 * @description
 * Récupère la liste des messages non lus de l'utilisateur authentifié via un appel à l'API backend.  
 * Retourne un tableau vide si aucun message n’est trouvé, ou en cas d’erreur réseau ou d’absence de token.
 *
 * @param {string} jwtToken - Le token JWT de l'utilisateur (optionnel).
 * @returns {Promise<Array>} Une promesse résolue contenant un tableau de messages non lus.
 */
async function fetchUnreadMessages(jwtToken) {
  // Si aucun token n’est fourni, on retourne directement un tableau vide
  if (!jwtToken) return [];

  try {
    // Requête vers l’API pour récupérer les messages non lus
    const response = await fetch(
      `${getApiUrl()}/api/messages/unread`,
      {
        headers: {
          ...(jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}), // Ajoute l’en-tête Authorization si un token est présent
        },
        credentials: "include", // Inclut les cookies dans la requête (utile pour l’authentification)
        cache: "no-store", // Désactive le cache pour obtenir des données fraîches à chaque chargement
      }
    );

    // Si la requête réussit, on parse le JSON et on vérifie qu'il s'agit bien d'un tableau
    if (response.ok) {
      const data = await response.json();
      return Array.isArray(data) ? data : [];
    }

    // Si aucun message non lu n'est trouvé, on renvoie simplement un tableau vide
    if (response.status === 404) {
      return [];
    }

    // Retour par défaut en cas de code HTTP inattendu
    return [];
  } catch (error) {
    // Logge une erreur claire en cas de problème réseau ou d’exception non gérée
    console.error("Erreur lors de la récupération des messages non lus:", error);
    return [];
  }
}

/**
 * @async
 * @function RootLayout
 * @description
 * Composant racine (layout principal) de l’application.  
 * Il gère la structure globale de la page, l’affichage conditionnel des en-têtes selon le rôle utilisateur,  
 * le contexte utilisateur, le contexte des messages non lus, ainsi que le pied de page.
 *
 * @param {Object} props - Propriétés passées au layout.
 * @param {React.ReactNode} props.children - Contenu principal rendu à l’intérieur du layout.
 * @returns {Promise<JSX.Element>} La structure complète de la page HTML.
 */
export default async function RootLayout({ children }) {
  // Récupération de l'utilisateur authentifié (ou null si non connecté)
  const user = await getUser();

  // Récupération des messages non lus pour cet utilisateur
  const unreadMessages = await fetchUnreadMessages(user?.jwtToken);
  const unreadCount = unreadMessages.length; // Nombre total de messages non lus (peut être utilisé plus tard)

  return (
    <html lang="fr">
      <body className="relative flex flex-col min-h-screen bg-white text-blue overflow-x-hidden">
        {/* Fournit le contexte global des messages non lus à toute l’application */}
        <UnreadMessagesProvider
          jwtToken={user?.jwtToken}
          currentUserId={user?.id}
          initialUnreadMessages={unreadMessages}
        >
          {/* Affiche le header approprié en fonction du rôle de l'utilisateur */}
          <HeaderByRole user={user} />

          {/* Image de fond filigrane fixée derrière le contenu */}
          <div className="fixed inset-0 flex items-center justify-center -z-10 overflow-hidden">
            <Image
              src="/filigrane.png"
              alt="filigrane"
              width={400}
              height={400}
              className="object-contain opacity-5 max-w-[80vw] max-h-[80vh]"
            />
          </div>

          {/* Conteneur principal du contenu de la page */}
          <main className="flex-grow w-full flex justify-center items-start mb-8">
            {/* Fournit le contexte utilisateur aux composants enfants */}
            <UserProviderWrapper user={user}>
              {React.Children.map(children, (child) =>
                React.isValidElement(child)
                  ? React.cloneElement(child, { user }) // Passe l’objet utilisateur à chaque enfant valide
                  : child
              )}
            </UserProviderWrapper>
          </main>
        </UnreadMessagesProvider>

        {/* Pied de page visible sur toutes les pages */}
        <Footer />
      </body>
    </html>
  );
}

/**
 * Métadonnées globales de l'application.
 * Utilisées par Next.js pour le référencement et les icônes du site.
 */
export const metadata = {
  title: "Atelier Local", // Titre affiché dans l’onglet du navigateur
  description: "Le savoir-faire à côté de chez vous.", // Description pour le SEO
  icons: {
    icon: "/favicon.ico", // Icône principale du site
  },
};
