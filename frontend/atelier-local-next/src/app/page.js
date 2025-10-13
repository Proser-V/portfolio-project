import ClientHome from "@/components/ClientHome";
import VisitorHome from "@/components/VisitorHome";
import ArtisanHome from "@/components/ArtisanHome";
import AdminHome from "@/components/AdminHome";

export default function Home({ user }) {
  if (!user) return <VisitorHome />;

  switch (user.role) {
    case "admin":
      return <AdminHome admin={user} />;
    case "client":
      return <ClientHome client={user} />;
    case "artisan":
      return <ArtisanHome artisan={user} />;
    default:
      return <VisitorHome />;
  }
}