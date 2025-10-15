import { getUser } from "@/lib/getUser";
import Image from "next/image";
import Link from "next/link";
import placeholder from "../../../../public/tronche.jpg";
import placeholderIcon from "../../../app/favicon.ico";
import ArtisanPortfolio from "@/components/ArtisanPortfolio";
import ProfileActionButton from "@/components/ProfileActionButton";

async function getArtisan(artisanId) {
  try {
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/artisans/${artisanId}`,
      { cache: "no-store" }
    );
    if (!res.ok) return null;
    return await res.json();
  } catch (err) {
    console.log("Erreur récupération artisan:", err);
    return null;
  }
}

export default async function ArtisanProfilePage({ params }) {
  const { artisanId } = await params;
  const artisan = await getArtisan(artisanId);
  const user = await getUser();

  if (!artisan) {
    return (
      <div className="mt-6 text-center">
        <h1 className="text-blue text-xl">Artisan non trouvé</h1>
        <Link href="/artisans" className="text-blue underline">
          Retour à la liste
        </Link>
      </div>
    );
  }

  // Détermine si l'utilisateur est propriétaire de cette page
  const isOwner = user?.id === artisan.id && user?.role === "artisan";

  // Calcul de l'expérience
  const experienceYears = artisan.activityStartDate
    ? new Date().getFullYear() - new Date(artisan.activityStartDate).getFullYear()
    : 0;
  const experienceText = experienceYears > 0 
    ? `${experienceYears} an${experienceYears > 1 ? 's' : ''}`
    : "Débutant";

  return (
    <div className="mt-6 flex flex-col items-center justify-center px-4 md:px-0">
      <h1 className="text-center text-blue text-xl font-normal font-cabin mb-4">
        {`Page personnelle - ${artisan.name}`}
      </h1>

      <div className="mt-6 flex flex-col items-center justify-center px-4 md:px-0 max-w-[1000px] mx-auto">
        <div className="w-full flex flex-col md:flex-row items-center gap-4">
          {/* Colonne gauche - Avatar et infos */}
          <div className="flex flex-col w-[250px] items-center gap-2">
            <Image
              src={artisan.avatar || placeholder}
              alt={`${artisan.name} avatar`}
              height={250}
              width={250}
              className="shadow-lg ml-2 border-solid border-black border-2"
            />
            <h2 className="text-gold text-xl mt-0 font-cabin">{artisan.name}</h2>
            <p className="block text-center text-sm text-silver mt-0">
              Recommandé {artisan.recommendations} fois par les habitants pour ses réalisations
            </p>
          </div>

          {/* Colonne droite - Carte principale */}
          <div className="relative flex flex-col bg-white border-gold border-2 border-solid w-full max-w-7xl mx-auto mb-6 overflow-hidden">
            {/* Filigrane */}
            <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
              <div className="relative w-[300px] h-[300px]">
                <Image
                  src={artisan?.logo || placeholderIcon}
                  alt={`${artisan?.categoryName || "Artisan category"} logo`}
                  fill
                  className="object-contain opacity-5"
                />
              </div>
            </div>

            {/* En-tête */}
            <div className="absolute flex right-4 md:right-6 pointer-events-none -mt-2">
              <p className="text-gold md:text-lg">A PROPOS DE MOI</p>
            </div>

            {/* Contenu */}
            <div className="relative z-10">
              <h3 className="text-blue ml-1 my-1">{artisan.categoryName}</h3>
              <p className="ml-1 mt-0 mb-4">
                <span className="underline">Expérience :</span> {experienceText}
              </p>
              <p className="ml-1 mt-0 mb-4">
                <span className="underline">Où me trouver :</span> Dans Ton Quartier
              </p>
              <p className="ml-1 mt-0 mb-24">
                <span className="underline">Bio :<br/></span>" {artisan.bio} "
              </p>
              <p className="ml-1 mt-0 mb-4">
                <span className="underline">SIRET :</span> {artisan.siret}
              </p>
              <p className="ml-1 mt-0 mb-4">
                <span className="underline">Email de contact :</span> {artisan.email}
              </p>
              <p className="ml-1 mt-0 mb-4">
                <span className="underline">Téléphone :</span> {artisan.phoneNumber}
              </p>
            </div>

            {/* Bouton conditionnel : Contact OU Modifier */}
            <ProfileActionButton artisan={artisan} isOwner={isOwner} />
          </div>
        </div>

        {/* Portfolio avec gestion si propriétaire */}
        <ArtisanPortfolio
          artisanId={artisan.id}
          initialPhotos={artisan.photoGallery}
          isOwner={isOwner}
        />
      </div>
    </div>
  );
}
