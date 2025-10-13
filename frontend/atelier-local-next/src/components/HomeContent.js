"use client";

import ClientHome from "@/components/ClientHome";
import VisitorHome from "@/components/VisitorHome";
import ArtisanHome from "@/components/ArtisanHome";
import AdminHome from "@/components/AdminHome";

export default function HomeContent({ user, artisans }) {
  if (!user) return <VisitorHome artisans={artisans} />;

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