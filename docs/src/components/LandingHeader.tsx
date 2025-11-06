import { Button } from "@/components/ui/button";
import logo from "@/assets/atelier-local-logo5.png"

export const LandingHeader = () => {
  const scrollToSection = (id: string) => {
    const element = document.getElementById(id);
    element?.scrollIntoView({ behavior: "smooth" });
  };

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container flex h-16 items-center justify-between">
        <div className="flex items-center gap-2">
          <img 
            src={logo} 
            alt="L'Atelier Local" 
            className="h-16 text-primary"
          />
        </div>
        
        <nav className="hidden md:flex items-center gap-6">
          <button 
            onClick={() => scrollToSection("features")}
            className="text-sm font-medium text-foreground hover:text-primary transition-colors"
          >
            Fonctionnalités
          </button>
          <button 
            onClick={() => scrollToSection("about")}
            className="text-sm font-medium text-foreground hover:text-primary transition-colors"
          >
            À propos
          </button>
          <Button asChild>
            <a href="#" target="_blank" rel="noopener noreferrer">
              Accéder à l'application
            </a>
          </Button>
        </nav>

        <Button asChild className="md:hidden">
          <a href="/app" target="_blank" rel="noopener noreferrer">
            Ouvrir
          </a>
        </Button>
      </div>
    </header>
  );
};
