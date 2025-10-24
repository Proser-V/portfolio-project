import Image from "next/image";
import logo from "../assets/logo/atelier-local-logo_white.png";
import Link from "next/link";
import ArtisanBurgerMenu from "@/components/ArtisanBurgerMenu";

export default function ArtisanHeader({ artisan }) {
  return (
    <header className="bg-blue text-gold shadow-md relative pr-4 py-2 md:pr-0 md:py-0 md:items-center">
      <div className="flex items-center justify-between">
        {/* Logo */}
        <Link href="/" className="flex-shrink-0 pl-4">
          <Image
            src={logo}
            alt="Atelier Local Logo"
            className="h-12 w-auto"
          />
        </Link>

        {/* Burger Menu + Navigation */}
        <ArtisanBurgerMenu artisan={artisan} />
      </div>
    </header>
  );
}
