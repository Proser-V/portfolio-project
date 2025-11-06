import { Card, CardContent } from "@/components/ui/card";
import featureMessaging from "@/assets/feature-messaging.png";
import featureProfiles from "@/assets/feature-profile.png";
import featurePost from "@/assets/feature-post.png";

const features = [
  {
    title: "Messagerie directe",
    description: "Échangez directement avec les artisans pour discuter de vos projets, obtenir des devis et planifier vos rendez-vous. Une communication simple et humaine.",
    image: featureMessaging,
  },
  {
    title: "Profils détaillés",
    description: "Découvrez le portfolio de chaque artisan, leurs réalisations, leur expérience et leurs spécialités. Trouvez le professionnel qui correspond à vos besoins.",
    image: featureProfiles,
  },
  {
    title: "Postez vos besoins",
    description: "Faites connaître vos besoins ! Les artisans et commerçants locaux peuvent vous proposer leurs services en quelques clics.",
    image: featurePost,
  },
];

export const FeatureSection = () => {
  return (
    <section id="features" className="py-20 bg-muted/50">
      <div className="container px-4">
        <div className="text-center mb-16">
          <h2 className="text-3xl md:text-4xl font-bold mb-4">
            Fonctionnalités clés
          </h2>
          <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
            Tout ce dont vous avez besoin pour trouver et collaborer avec des artisans locaux
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-8">
          {features.map((feature, index) => (
            <Card key={index} className="overflow-hidden hover:shadow-lg transition-shadow">
              <div className="aspect-square overflow-hidden">
                <img 
                  src={feature.image} 
                  alt={feature.title}
                  className="w-full h-full object-cover hover:scale-105 transition-transform duration-300"
                />
              </div>
              <CardContent className="p-6">
                <h3 className="text-xl font-semibold mb-3">{feature.title}</h3>
                <p className="text-muted-foreground">{feature.description}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};
