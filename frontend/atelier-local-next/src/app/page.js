import ClientHome from "@/components/ClientHome";
import VisitorHome from "@/components/VisitorHome";
import ArtisanHome from "@/components/ArtisanHome";
import AdminHome from "@/components/AdminHome";

export default async function Home() {
  const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/users/me`, { cache: "no-store" });
  const user = res.ok ? await res.json() : null;

  if (user?.role === 'admin') {
    return <AdminHome admin={user} />;
  } else if (user?.role === 'client') {
    return <ClientHome client={user} />;
  } else if (user?.role === 'artisan') {
    return <ArtisanHome artisan={user} />;
  } else {
    return <VisitorHome />;
  }
}