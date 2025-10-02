import logo from "../assets/atelier-local-logo_white.png"

export default function VisitorHeader() {
  return (
    <header className="bg-blue text-gold shadow-md flex flex-col md:flex-row justify-between items-center w-full px-4 sm:px-6 md:px-8 py-3 md:py-4">
      <img
        src={logo}
        alt="Logo"
        className="h-12 w-auto"
      />
      <nav className="flex text-center w-full text-sm font-cabin">
        <a href="#" className="flex-1 flex items-center justify-center py-2 hover:underline">Demande de prestation</a>
        <a href="#" className="flex-1 flex items-center justify-center py-2 hover:underline">Artisans</a>
        <a href="#" className="flex-1 flex items-center justify-center py-2 hover:underline">Créer un compte</a>
        <a href="#" className="flex-1 flex items-center justify-center py-2 hover:underline">Connexion</a>
      </nav>
    </header>
  );
}
