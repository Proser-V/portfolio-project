export default function Footer() {
  return (
    <footer className="bg-blue text-gold px-4 py-4 flex flex-col items-center gap-2 text-xs">
      <div className="flex flex-col md:flex-row gap-2 md:gap-4 items-center text-center">
        <a href="/contact" className="hover:underline">Contact</a>
        <a href="/links" className="hover:underline">Liens utiles</a>
        <a href="/legal" className="hover:underline">Mentions légales</a>
      </div>
      <div className="text-[10px] mt-1 text-center">
        Icons made by Freepik and Pixel perfect from www.flaticon.com
      </div>
    </footer>
  );
}
