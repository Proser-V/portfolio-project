import Image from "next/image";
import Link from "next/link";

export default function VisitorHome({ artisans }) {
    return (
        <section className="relative mx-auto px-4 sm:px-6 lg:px-8">
            <h1 className="text-blue text-xl lg:text-2xl text-center mt-4 font-cabin">
                Le savoir faire à Dijon et ses alentours
            </h1>

            <p className="text-silver text-center mt-4 text-xs">
                L'Atelier Local vous connecte facilement avec les artisans et commerçants autour de Dijon.
                <br className="block" />
                Explorez leurs réalisations, contactez-les directement et trouvez l'expertise qu'il vous faut, en toute proximité.
                <br className="block" />
                Déposez vos besoins ici, les professionnels vous recontactent directement.
            </p>

            <div className="flex flex-col lg:flex-row items-center justify-center">
                <div className="text-center lg:text-right w-full lg:w-auto mb-4 lg:mb-0">
                    <p className="text-gold mb-1">Vous avez besoin d'artisans dijonnais ?</p>
                    <a href="/registration" className="text-blue underline text-sm">
                        Entrez dans L'Atelier Local et faites appel aux artisans dijonnais.
                    </a>
                </div>

                <div className="hidden lg:block w-px bg-silver mx-4 h-12"></div>

                <div className="text-center lg:text-left w-full lg:w-auto">
                    <p className="text-gold mb-1">Vous êtes artisans à proximité de Dijon ?</p>
                    <a href="/registration" className="text-blue underline text-sm">
                        Entrez dans L'Atelier Local et répondez aux besoins des habitants.
                    </a>
                </div>
            </div>

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
                        <Image
                        src={artisan?.avatar?.url || "/placeholder.png"}
                        alt={artisan.name}
                        height={250}
                        width={250}
                        sizes="(max-width: 768px) 200px, 250px"
                        className="object-cover"
                        />
                        <div
                        className="absolute inset-0 z-10"
                        style={{
                            backgroundImage:
                            "linear-gradient(to bottom right, transparent, rgba(255, 255, 255, 1))",
                        }}
                        ></div>
                        <div className="absolute -bottom-2 right-2 text-right text-blue font-cabin z-20">
                        <p>
                            {artisan.name} <br />
                            {artisan.artisanCategory} <br />
                            {artisan.phoneNumber ? `Tel: ${artisan.phoneNumer}` : ""} <br />
                            {artisan.email}
                        </p>
                        </div>
                    </Link>
                    ))}
                </div>
                <Link
                href="/artisans"
                className="w-full max-w-xs h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                            text-gold text-base font-normal font-cabin
                            flex items-center justify-center mx-auto 
                            hover:bg-blue transition mt-5"
                >
                Découvrez les artisans dijonnais
                </Link>
            </div>
        </section>
    );
}
