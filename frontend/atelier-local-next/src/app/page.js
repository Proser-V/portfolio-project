import ClientHome from "@/components/ClientHome";
import VisitorHome from "@/components/VisitorHome";
import ArtisanHome from "@/components/ArtisanHome";
import AdminHome from "@/components/AdminHome";

export default function Home() {
  const user = {
    role: "artisan",
    name: "Valentin",
    avatar: "/tronche.jpg"
  };

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