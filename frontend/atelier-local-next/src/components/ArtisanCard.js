import Image from "next/image";
import Link from "next/link";

export default function ArtisanCard({ artisan, className }) {
  return (
    <div className={`h-[125px] border border-gold rounded p-4 ${className}`}>
      {/* Contenu de l'artisan */}
      <p>{artisan?.name}</p>
    </div>
  );
}