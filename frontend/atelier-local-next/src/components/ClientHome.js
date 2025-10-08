import Image from "next/image";
import Link from "next/link";
import avatar from "../../public/tronche.jpg"

export default function ClientHome({ client }) {
    return (
        <section className="relative mx-auto px-4 sm:px-6 md:px-8">
            <h1 className="text-blue text-xl md:text-2xl text-center mt-4 font-cabin">
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
            <div className="flex flex-wrap justify-center gap-1 md:flex-row-1">
                <Link
                href="/askings"
                className="max-w-xs h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                            text-gold text-sm font-normal font-cabin
                            flex items-center justify-center mx-2 
                            hover:bg-blue transition mt-5 px-4"
                >
                Postez une demande de prestation
                </Link>
                <Link
                href="/artisans"
                className="max-w-xs h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                            text-gold text-sm font-normal font-cabin
                            flex items-center justify-center mx-2
                            hover:bg-blue transition md:mt-5 px-4"
                >
                Parcourez les artisans autour de chez vous
                </Link>
            </div>

            <div className="relative flex flex-col items-center w-full">
                <p className="text-blue text-center font-cabin text-sm mb-4 md:absolute md:top-10 md:mb-0">
                Vos voisins les recommandent :
                </p>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 justify-items-center w-full max-w-4xl mx-auto">
                    <Link
                        href="/artisans"
                        className="relative w-[250px] h-[250px] border-2 border-solid border-gold shadow-lg overflow-hidden md:mt-12">
                        <Image
                            src={avatar}
                            alt="avatar"
                            fill
                            className="object-cover"
                        />
                        <div className="absolute inset-0 z-10"
                            style={{
                                backgroundImage: 'linear-gradient(to bottom right, transparent, rgba(255, 255, 255, 1))'
                            }}>
                        </div>
                        <div className="absolute -bottom-2 right-2 text-right text-blue font-cabin z-20">
                            <p>
                                Nom de l'artisan<br/>
                                Métier<br/>
                                Tel: 03 80 XX XX XX<br/>
                                email@artisan.com
                            </p>
                        </div>
                    </Link>

                    <Link
                        href="/artisans"
                        className="relative w-[250px] h-[250px] border-2 border-solid border-gold shadow-lg overflow-hidden md:mt-24">
                        <Image
                            src={avatar}
                            alt="avatar"
                            fill
                            className="object-cover"
                        />
                        <div className="absolute inset-0 z-10"
                            style={{
                                backgroundImage: 'linear-gradient(to bottom right, transparent, rgba(255, 255, 255, 1))'
                            }}>
                        </div>
                        <div className="absolute -bottom-2 right-2 text-right text-blue font-cabin z-20">
                            <p>
                                Nom de l'artisan<br/>
                                Métier<br/>
                                Tel: 03 80 XX XX XX<br/>
                                email@artisan.com
                            </p>
                        </div>
                    </Link>

                    <Link
                        href="/artisans"
                        className="relative w-[250px] h-[250px] border-2 border-solid border-gold shadow-lg overflow-hidden md:mt-12">
                        <Image
                            src={avatar}
                            alt="avatar"
                            fill
                            className="object-cover"
                        />
                        <div className="absolute inset-0 z-10"
                            style={{
                                backgroundImage: 'linear-gradient(to bottom right, transparent, rgba(255, 255, 255, 1))'
                            }}>
                        </div>
                        <div className="absolute -bottom-2 right-2 text-right text-blue font-cabin z-20">
                            <p>
                                Nom de l'artisan<br/>
                                Métier<br/>
                                Tel: 03 80 XX XX XX<br/>
                                email@artisan.com
                            </p>
                        </div>
                    </Link>
                </div>
            </div>
        </section>
    );
}
