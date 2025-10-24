import Image from "next/image";
import Link from "next/link";

/**
 * Composant ArtisanHome
 * ---------------------
 * Page d'accueil personnalisée pour un artisan.
 * Affiche un message de bienvenue, une invitation à parcourir les besoins des habitants,
 * et une sélection d'artisans recommandés dans la région.
 *
 * @param {Object} props - Propriétés du composant
 * @param {Object} props.artisan - Artisan actuellement connecté ou affiché
 * @param {string|number} props.artisan.id - Identifiant de l'artisan
 * @param {string} props.artisan.name - Nom de l'artisan
 * @param {string|number} props.artisan.categoryId - Identifiant de la catégorie de l'artisan
 * @param {Array<Object>} props.artisans - Liste d'artisans à mettre en avant (recommandés)
 *
 * @returns {JSX.Element} Composant affichant la page d'accueil personnalisée
 */
export default function ArtisanHome({ artisan, artisans }) {
    return (
        <section className="relative mx-auto px-4 sm:px-6 lg:px-8">
            {/* Titre principal */}
            <h1 className="text-blue text-xl lg:text-2xl text-center mt-4 font-cabin">
                Le savoir faire à Dijon et ses alentours
            </h1>

            {/* Description introductive */}
            <p className="text-silver text-center mt-4 text-xs">
                L'Atelier Local vous connecte facilement avec les artisans et commerçants autour de Dijon.
                <br className="block" />
                Explorez leurs réalisations, contactez-les directement et trouvez l'expertise qu'il vous faut, en toute proximité.
                <br className="block" />
                Déposez vos besoins ici, les professionnels vous recontactent directement.
            </p>

            {/* Message de bienvenue personnalisé */}
            <div className="text-center">
                <p className="text-gold -mb-1 text-lg">
                    Bienvenue dans l'Atelier, {artisan.name} !
                </p>
            </div>

            {/* Bouton pour accéder aux besoins des habitants */}
            <Link
                href={`/askings/${artisan.categoryId}`}
                className="max-w-xs h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                        text-gold text-sm font-normal font-cabin
                        flex items-center justify-center mx-auto mt-5 px-4"
            >
                Parcourez et répondez aux besoins des habitants
            </Link>

            {/* Section des artisans recommandés */}
            <div className="relative flex flex-col items-center w-full">
                <p className="text-blue text-center font-cabin text-sm mb-4 lg:absolute lg:top-10 lg:mb-0">
                    Vos voisins les recommandent :
                </p>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 justify-items-center w-full max-w-4xl mx-auto">
                    {artisans.map((artisan, index) => (
                        <Link
                            key={artisan.id || index}
                            href={`/artisans/${artisan.id}`}
                            className={`relative w-[250px] h-[250px] border-2 border-solid border-gold shadow-lg overflow-hidden ${
                                index === 1 ? "lg:mt-24" : "lg:mt-12"
                            }`}
                        >
                            {/* Image de l'artisan ou placeholder si non disponible */}
                            <Image
                                src={artisan.avatar?.url || "/placeholder.png"}
                                alt={artisan.name}
                                width={250}
                                height={250}
                                className="object-center object-cover"
                            />

                            {/* Dégradé pour lisibilité du texte */}
                            <div
                                className="absolute inset-0 z-10"
                                style={{
                                    backgroundImage:
                                    "linear-gradient(to bottom right, transparent, rgba(255, 255, 255, 1))",
                                }}
                            ></div>

                            {/* Informations sur l'artisan */}
                            <div className="absolute -bottom-2 right-2 text-right text-blue font-cabin z-20">
                                <p>
                                    <span className="text-lg font-bold">{artisan.name}</span><br />
                                    <span className="text-base font-semibold">{artisan.categoryName}</span><br />
                                </p>
                            </div>
                        </Link>
                    ))}
                </div>
            </div>
        </section>
    );
}
