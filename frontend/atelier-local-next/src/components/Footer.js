export default function Footer() {
  return (
    <footer className="bg-blue text-gold px-2 py-2 flex flex-col items-center text-xs">
      <div className="flex flex-col md:flex-row gap-2 md:gap-4 items-center text-center">
        <a href="/contact" className="hover:underline">Contact</a>
        <a href="/links" className="hover:underline">Liens utiles</a>
        <a href="/legal" className="hover:underline">Mentions légales</a>
      </div>
      <div className="mt-1 text-center">
        L'Atelier Local © - Projet Portfolio - HolbertonSchool Dijon<span className="text-[10px]"> - Icons made by Freepik and Pixel perfect from www.flaticon.com</span>
      </div>
    </footer>
  );
}
