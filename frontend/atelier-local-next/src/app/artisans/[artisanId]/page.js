import { getUser } from "@/lib/getUser";
import Image from "next/image";
import Link from "next/link";
import placeholder from "../../../../public/tronche.jpg"
import placeholderIcon from "../../../app/favicon.ico"
import ArtisanPortfolio from "@/components/ArtisanPortfolio";

async function getArtisan(artisanId) {
    try {
        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/artisans/${artisanId}`,
        { cache: "no-store" }
        );
        if (!res.ok) {
            return null;
        }
        return await res.json();
    } catch (err) {
        console.log("Erreur récupération artisan: ", err)
        return null;
    }
}

export default async function ArtisanProfilePage({ params }) {
  const { artisanId } = await params;
  const artisan = await getArtisan(artisanId);
  const user = await getUser();
  const experienceYears = Math.floor(
    (new Date() - new Date(artisan.activityStartDate)) / (1000 * 60 * 60 * 24 * 365)
  );
  let experienceText = "";

  if (experienceYears < 1) {
    experienceText = "Inférieure à 1 an";
  } else if (experienceYears === 1) {
    experienceText = "1 an";
  } else {
    experienceText = `${experienceYears} ans`;
  }

  if (!artisan) {
    return (
      <div className="mt-6 text-center">
        <h1 className="text-blue text-xl">Artisan non trouvé</h1>
        <p>Cet artisan n'existe pas ou a été supprimé.</p>
        <a href="/artisans" className="text-blue underline">
          Retour à la liste
        </a>
      </div>
    );
  }

  return (
    <div className="mt-6 flex flex-col items-center justify-center px-4 md:px-0">
      <h1 className="text-center text-blue text-xl font-normal font-cabin mb-4">
        {`Page personnelle - ${artisan.name}`}
      </h1>
      <div className="w-full flex flex-col md:flex-row items-center gap-4 mt-4">
        <div className="flex flex-col w-[250px] items-center gap-1">
          <Image
            src={artisan.avatar || placeholder}
            alt={`${artisan.name} avatar`}
            height={250}
            width={250}
            className="shadow-lg ml-2 border-solid border-black border-2"
          />
          <h2 className="text-gold text-xl mt-0 font-cabin">{artisan.name}</h2>
          <p className="block text-center text-sm text-silver mt-0">Recommandé {artisan.recommendations} fois par les habitants pour ses réalisations</p>
        </div>
        <div className="relative flex flex-col bg-white border-gold border-2 border-solid w-[325px] md:w-[700px] overflow-hidden">
          {/* Filigrane en arrière-plan */}
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
          <div className="absolute flex right-4 md:right-6 pointer-events-none -mt-2">
            <div className="relative">
                <p className="text-gold md:text-lg">A PROPOS DE MOI</p>
            </div>
          </div>
          <h3 className="text-blue ml-1 my-1">{artisan.categoryName}</h3>
          <p className="ml-1 mt-0 mb-4"><span className="underline">Expérience :</span> {experienceText}</p>
          <p className="ml-1 mt-0 mb-4"><span className="underline">Où me trouver :</span> Dans Ton Quartier</p>
          <p className="ml-1 mt-0 mb-24"><span className="underline">Bio :<br/></span>" {artisan.bio} "</p>
          <p className="ml-1 mt-0 mb-4"><span className="underline">SIRET :</span> {artisan.siret}</p>
          <p className="ml-1 mt-0 mb-4"><span className="underline">Email de contact :</span> {artisan.email}</p>
          <p className="ml-1 mt-0 mb-4"><span className="underline">Téléphone :</span> {artisan.phoneNumber}</p>
          <Link
          href="/#"
          className="btn-primary relative z-10 mx-auto mt-0 mb-2
                   md:absolute md:bottom-4 md:right-4 md:mb-0 md:mx-0"
          >
          Contactez-moi
          </Link>
        </div>
      </div>
      <div className="w-full flex flex-col md:flex-row items-center gap-4 mt-4">
        <ArtisanPortfolio photoGallery={artisan.photoGallery} />
      </div>
    </div>
  );
}