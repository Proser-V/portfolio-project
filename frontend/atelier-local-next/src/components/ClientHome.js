import Image from "next/image";
import Link from "next/link";

export default function ClientHome({ client, artisans }) {
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
            <div className="text-center">
                <p className="text-gold -mb-1 text-lg">Bienvenue dans l'Atelier, {client.firstName} !</p>
            </div>
            <div className="flex flex-wrap justify-center gap-1 lg:flex-row-1">
                <Link
                href="/new-asking"
                className="max-w-xs h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                            text-gold text-sm font-normal font-cabin
                            flex items-center justify-center mx-2 
                            mt-5 px-4"
                >
                Postez une demande de prestation
                </Link>
                <Link
                href="/artisans"
                className="max-w-xs h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                            text-gold text-sm font-normal font-cabin
                            flex items-center justify-center mx-2
                            mt-5 px-4"
                >
                Parcourez les artisans autour de chez vous
                </Link>
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
                        src={artisan.avatar?.url || "/placeholder.png"}
                        alt={artisan.name}
                        width={250}
                        height={250}
                        className="object-center object-cover"
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
