import { headers } from "next/headers";
import ClientHome from "@/components/ClientHome";
import VisitorHome from "@/components/VisitorHome";
import ArtisanHome from "@/components/ArtisanHome";
import AdminHome from "@/components/AdminHome";

export default async function Home() {
  const cookieHeader = (await headers()).get("cookie") || "";

  const res = await fetch("http://localhost:3000/api/me", {
    cache: "no-store",
    headers: { cookie: cookieHeader },
  });

  const user = res.ok ? await res.json() : null;
  const role = user?.role?.toLowerCase();

  if (role === "admin") return <AdminHome admin={user.user} />;
  if (role === "client") return <ClientHome client={user.user} />;
  if (role === "artisan") return <ArtisanHome artisan={user.user} />;
  return <VisitorHome />;
}
