import { Hammer } from "lucide-react";
import logo from "@/assets/atelier-local-logo5_white.png"

export const LandingFooter = () => {
  return (
    <footer className="bg-primary text-primary-foreground py-12">
      <div className="container px-4">
        <div className="flex flex-col md:flex-row justify-between items-center gap-6">
          <div className="flex items-center gap-2">
            <img 
              src={logo} 
              alt="L'Atelier Local" 
              className="h-16 text-primary"
            />
          </div>
          
          <div className="flex flex-wrap justify-center gap-6 text-sm">
            <a href="#" className="hover:text-secondary transition-colors">
              Contact
            </a>
            <a href="#" className="hover:text-secondary transition-colors">
              Mentions légales
            </a>
            <a href="#" className="hover:text-secondary transition-colors">
              Conditions d'utilisation
            </a>
          </div>
        </div>
        
        <div className="text-center mt-8 text-sm opacity-80">
          © 2025 L'Atelier Local - Projet Portfolio Holberton School
        </div>
      </div>
    </footer>
  );
};
