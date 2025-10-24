import Image from "next/image";
import Link from "next/link";

export default function ArtisanCard({ artisan, className }) {
  return (
    <Link
      href={`/artisans/${artisan?.id}`}
      className={`flex flex-col sm:flex-row items-center sm:items-stretch justify-between bg-white border-2 border-gold shadow-md border-solid overflow-hidden w-full max-w-[1150px] ${className}`}
    >
      {/* Image artisan */}
      <div className="relative w-full sm:w-48 h-48 flex-shrink-0">
        <Image
          src={artisan?.avatar?.url || "/placeholder.png"}
          alt={`${artisan?.name || "Artisan"} logo`}
          fill
          sizes="100vw"
          className="object-cover"
        />
      </div>

      {/* Contenu texte */}
      <div className="flex flex-col justify-start px-6 text-center sm:text-left flex-grow">
        <p className="text-xl sm:text-2xl text-gold font-cabin mt-1 mb-0">
          {artisan?.name || "Nom de l'artisan"}
        </p>
        <p className="text-base sm:text-lg text-blue font-cabin my-0">
          {artisan?.categoryName || "Métier / ville"}
        </p>
        <p className="text-sm sm:text-base text-silver font-cabin mt-2">
          {artisan?.recommendations
            ? `Recommandé ${artisan.recommendations} fois par les habitants`
            : "Aucune recommandation pour le moment"}
        </p>
      </div>

      {/* Icône métier */}
      <div className="hidden sm:block relative w-40 h-40 my-auto mr-4">
        <Image
          src="/filigrane.png"
          alt="icone"
          fill
          sizes="100vw"
          className="object-cover opacity-5"
        />
      </div>
    </Link>
  );
}
