import VisitorHeader from "../components/VisitorHeader";
import ClientHeader from "../components/ClientHeader";
import ArtisanHeader from "../components/ArtisanHeader";
import AdminHeader from "../components/AdminHeader";
import Footer from "../components/Footer";
import background from "./favicon.ico";
import Image from "next/image";
import "./globals.css";
import { cookies } from "next/headers";
import UserProviderWrapper from "../components/UserProviderWrapper";
import React from "react";

export default async function RootLayout({ children }) {
  let header;
  const cookieStore = await cookies();
  const token = cookieStore.get("jwt")?.value;
  const cookieHeader = cookieStore
    .getAll()
    .map((c) => `${c.name}=${c.value}`)
    .join("; "); // conversion manuelle

  let user = null;

  if (token) {
    try {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/users/me`, {
        method: "GET",
        headers: {
          Cookie: cookieHeader, // envoie le vrai header Cookie
        },
        cache: "no-store",
      });

      if (res.ok) {
        const data = await res.json();
        user = {
          role: data.role?.toLowerCase(),
          ...data.user,
        };
      } else {
        console.warn("Échec de récupération du user :", await res.text());
      }
    } catch (err) {
      console.error("Erreur récupération user :", err);
    }
  }

  console.log("USER (SSR):", user);

  if (user?.role === "admin") header = <AdminHeader admin={user} />;
  else if (user?.role === "client") header = <ClientHeader client={user} />;
  else if (user?.role === "artisan") header = <ArtisanHeader artisan={user} />;
  else header = <VisitorHeader />;

  return (
    <html lang="fr">
      <body className="relative flex flex-col min-h-screen bg-white text-blue overflow-x-hidden">
        {header}
        <div className="fixed inset-0 flex items-center justify-center -z-10 overflow-hidden">
          <Image
            src={background}
            alt="filigrane"
            width={400}
            height={400}
            className="object-contain opacity-5 max-w-[80vw] max-h-[80vh]"
            priority
          />
        </div>
        <main className="flex-grow max-w-[1380px] mx-auto px-4 sm:px-6 md:px-8 mb-10">
          <UserProviderWrapper user={user}>
            {React.Children.map(children, (child) =>
              React.isValidElement(child)
                ? React.cloneElement(child, { user })
                : child
            )}
          </UserProviderWrapper>
        </main>
        <Footer />
      </body>
    </html>
  );
}
