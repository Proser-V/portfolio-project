"use client";
import ClientHeader from "./ClientHeader";
import ArtisanHeader from "./ArtisanHeader";
import VisitorHeader from "./VisitorHeader";
import AdminHeader from "./AdminHeader";

export default function HeaderByRole({ user }) {
  if (user?.role === "admin") return <AdminHeader admin={user} />;
  if (user?.role === "client") return <ClientHeader client={user} />;
  if (user?.role === "artisan") return <ArtisanHeader artisan={user} />;
  return <VisitorHeader />;
}
