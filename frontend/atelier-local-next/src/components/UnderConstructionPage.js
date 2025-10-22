import Image from "next/image";
import Link from "next/link";

export default function UnderConstructionPage() {
  return (
    <div className="flex flex-col w-full min-h-[calc(100vh-10rem)]">
      
      {/* Lien en haut */}
      <div className="p-4">
        <Link href="/" className="text-blue-600 hover:underline">
          ← Retour à l'accueil
        </Link>
      </div>

      {/* Image centrée */}
      <div className="relative flex-grow flex items-center justify-center">
        <Image
          src="/under-construction.png"
          alt="Under construction"
          fill
          className="object-contain max-h-full max-w-full"
          priority
        />
      </div>
    </div>
  );
}