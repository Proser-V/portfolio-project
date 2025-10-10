"use client"

export default async function NewAskingPage() {
    return (
        <div className="w-full max-w-[1291px] mx-auto p-4 flex flex-col items-center gap-6">
            {/* Image principale */}
            <img
                src="https://placehold.co/545x510"
                className="w-full md:w-[545px] h-auto rounded shadow-lg"
                alt="Illustration"
            />

            {/* Texte explicatif */}
            <p className="text-center text-blue text-base font-alike max-w-[690px]">
                Choisissez une ou plusieurs catégories d'artisans. Une notification sera envoyée à chaque artisan concerné par votre demande.
            </p>

            {/* Cartes catégories */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 w-full">
                {/* Exemple de carte */}
                {[
                { title: "Traiteur", desc: "Buffet froid pour 80 personnes" },
                { title: "Photographe", desc: "Photos pro + photobooth" },
                { title: "DJ", desc: "Animations et musique pour la nuit" },
                { title: "Fleuriste", desc: "Fleurs pour la salle et gros bouquet" },
                ].map((cat, idx) => (
                <div
                    key={idx}
                    className="relative w-full h-24 bg-white rounded-[32px] shadow-md border border-silver p-4 flex flex-col justify-center"
                >
                    <span className="text-gold font-alike text-base text-center">{cat.title}</span>
                    <span className="text-blue font-alike text-xs text-center mt-1">{cat.desc}</span>
                    <div className="absolute top-2 right-2 w-3.5 h-3.5 bg-white border border-blue rounded-full"></div>
                </div>
                ))}
            </div>

            {/* Bouton principal */}
            <button className="mt-6 w-80 h-10 bg-blue text-gold rounded-[42px] border border-gold shadow hover:bg-indigo-800 transition">
                Créer un compte et poster ma demande
            </button>
        </div>
    )
}