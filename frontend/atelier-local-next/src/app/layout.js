import VisitorHeader from "../components/VisitorHeader";
import ClientHeader from "../components/ClientHeader";
import ArtisanHeader from "../components/ArtisanHeader";
import AdminHeader from "../components/AdminHeader";
import Footer from "../components/Footer";
import background from "./favicon.ico"
import Image from "next/image";
import './globals.css';
import { imageConfigDefault } from "next/dist/shared/lib/image-config";

export default function RootLayout({ children }) {
  let header;
  const user = {
    role: "artisan",
    name: "Valentin",
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
          {children}
        </main>
        <Footer />
      </body>
    </html>
  );
}
