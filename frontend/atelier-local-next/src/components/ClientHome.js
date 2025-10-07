import Image from "next/image";

export default function ClientHome({ client }) {
    return (
        <section className="relative w-full mx-auto px-4 sm:px-6 md:px-8">
            <h1 className="text-blue text-2xl md:text-4xl text-center mt-4 font-cabin">
                Le savoir faire à Dijon et ses alentours
            </h1>

            <p className="text-silver text-center mt-4">
                L'Atelier Local vous connecte facilement avec les artisans et commerçants autour de Dijon.
                <br className="block" />
                Explorez leurs réalisations, contactez-les directement et trouvez l'expertise qu'il vous faut, en toute proximité.
                <br className="block" />
                Déposez vos besoins ici, les professionnels vous recontactent directement.
            </p>

            <div className="relative flex flex-col items-center w-full">
                <p className="text-blue text-center font-cabin text-sm mb-4 md:absolute md:top-10 md:mb-0">
                Vos voisins les recommandent :
                </p>

                <div className="grid flex-wrap justify-center md:grid-cols-3 md:gap-10 w-full max-w-4xl">
                    <div className="relative w-[250px] h-[250px] border-2 border-solid border-gold shadow-lg overflow-hidden md:mt-12">
                        <Image
                            src={client.avatar}
                            alt={`${client.firstName} avatar`}
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
                    </div>

                    <div className="relative w-[250px] h-[250px] border-2 border-solid border-gold shadow-lg overflow-hidden md:mt-24 my-5">
                        <Image
                            src={client.avatar}
                            alt={`${client.firstName} avatar`}
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
                    </div>

                    <div className="relative w-[250px] h-[250px] border-2 border-solid border-gold shadow-lg overflow-hidden md:mt-12">
                        <Image
                            src={client.avatar}
                            alt={`${client.firstName} avatar`}
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
                    </div>
                </div>
            </div>
        </section>
    );
}