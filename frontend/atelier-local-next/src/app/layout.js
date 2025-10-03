import VisitorHeader from "../components/VisitorHeader";
import ClientHeader from "../components/ClientHeader";
import ArtisanHeader from "../components/ArtisanHeader";
import AdminHeader from "../components/AdminHeader";
import Footer from "../components/Footer";
import './globals.css';

export default function RootLayout({ children }) {
  let header;
  const user = {
    role: "client",
    firstName: "Valentin",
    avatar: "/tronche.jpg"
  };

  if (user?.role === 'admin') {
    header = <AdminHeader admin={user} />;
  } else if (user?.role === 'client') {
    header = <ClientHeader client={user} />;
  } else if (user?.role === 'artisan') {
    header = <ArtisanHeader artisan={user} />;
  } else {
    header = <VisitorHeader />;
  }

  return (
    <html lang="fr">
      <body className="flex flex-col">
        {header}
        <main className="max-w-[1380px] mx-auto px-4 sm:px-6 md:px-8">
          {children}
        </main>
        <Footer />
      </body>
    </html>
  );
}
