/**
 * Composant Footer
 * 
 * Affiche le pied de page du site avec :
 * - Liens importants : Contact, Liens utiles, Mentions légales
 * - Mention légale / copyright et crédits pour les icônes
 * 
 * Styling :
 * - Fond bleu et texte doré
 * - Responsive : colonne sur mobile, ligne sur desktop
 * - Espacement et alignement centrés
 */
export default function Footer() {
  return (
    <footer className="bg-blue text-gold px-2 py-2 flex flex-col items-center text-xs">
      
      {/* Liens principaux */}
      <div className="flex flex-col md:flex-row gap-2 md:gap-4 items-center text-center">
        <a href="/contact" className="hover:underline">Contact</a>
        <a href="/links" className="hover:underline">Liens utiles</a>
        <a href="/legal" className="hover:underline">Mentions légales</a>
      </div>

      {/* Texte copyright et crédits */}
      <div className="mt-1 text-center">
        L'Atelier Local © - Projet Portfolio - HolbertonSchool Dijon
        <span className="text-[10px]"> - Icons made by Freepik and Pixel perfect from www.flaticon.com</span>
      </div>
    </footer>
  );
}
