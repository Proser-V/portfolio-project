import { Button } from "@/components/ui/button";
import heroBanner from "@/assets/hero-banner.jpg";
import logo from "@/assets/atelier-local-logo5_white.png"

export const HeroSection = () => {
  return (
    <section className="relative h-[600px] md:h-[700px] flex items-center justify-center overflow-hidden">
      <div 
        className="absolute inset-0 bg-cover bg-center"
        style={{ backgroundImage: `url(${heroBanner})` }}
      >
        <div className="absolute inset-0 bg-primary/80" />
      </div>
      
      <div className="relative z-10 container text-center text-white px-4">
        <img 
          src={logo} 
          alt="L'Atelier Local" 
          className="mx-auto mb-6 w-64 md:w-80 lg:w-96 object-contain"
        />
        <p className="text-xl md:text-2xl mb-8 max-w-3xl mx-auto">
          Connectez-vous facilement avec les artisans et commerçants locaux de votre région
        </p>
        <p className="text-lg md:text-xl mb-12 max-w-2xl mx-auto text-white/90">
          Une plateforme humaine, interactive et collaborative pour rendre le savoir-faire local accessible à tous
        </p>
        <Button 
          size="lg" 
          className="bg-secondary text-secondary-foreground hover:bg-secondary/90 text-lg px-8 py-6"
          asChild
        >
          <a href="#" target="_blank" rel="noopener noreferrer">
            Découvrir l'application
          </a>
        </Button>
      </div>
    </section>
  );
};
