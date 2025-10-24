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
import HeaderByRole from "@/components/HeaderByRole"
import getApiUrl from "@/lib/api";

export const dynamic = "force-dynamic";

async function fetchUnreadMessages(jwtToken) {
  if (!jwtToken) return [];
  try {
      const response = await fetch(
          `${getApiUrl()}/api/messages/unread`,
          {
              headers: {
                  ...(jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}),
              },
              credentials: "include",
              cache: "no-store",
          }
      );

      if (response.ok) {
          const data = await response.json();
          return Array.isArray(data) ? data : [];
      }

      if (response.status === 404) {
          return [];
      }

      return [];
  } catch (error) {
      console.error("Erreur lors de la récupération des messages non lus:", error);
      return [];
  }
}

export default async function RootLayout({ children }) {
  const user = await getUser();
  const unreadMessages = await fetchUnreadMessages(user?.jwtToken)
  const unreadCount = unreadMessages.length;

  return (
    <html lang="fr">
      <body className="relative flex flex-col min-h-screen bg-white text-blue overflow-x-hidden">
        <UnreadMessagesProvider
          jwtToken={user?.jwtToken}
          currentUserId={user?.id}
          initialUnreadMessages={unreadMessages}
        >
        <HeaderByRole user={user} />
        <div className="fixed inset-0 flex items-center justify-center -z-10 overflow-hidden">
          <Image
            src="/filigrane.png"
            alt="filigrane"
            width={400}
            height={400}
            className="object-contain opacity-5 max-w-[80vw] max-h-[80vh]"
          />
        </div>
        <main className="flex-grow w-full flex justify-center items-start mb-8">
          <UserProviderWrapper user={user}>
            {React.Children.map(children, (child) =>
              React.isValidElement(child)
                ? React.cloneElement(child, { user })
                : child
            )}
          </UserProviderWrapper>
        </main>
        </UnreadMessagesProvider>
        <Footer />
      </body>
    </html>
  );
}

export const metadata = {
  title: "Atelier Local",
  description: "Le savoir-faire à côté de chez vous.",
  icons: {
    icon: "/favicon.ico",
  },
};