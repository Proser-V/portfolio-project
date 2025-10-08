"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { fetchCoordinates } from "../utils/location";

export default function RegistrationPage() {
  const [role, setRole] = useState(null);
  const [error, setError] = useState("");
  const router = useRouter();
  const [ClientFormData, setClientFormData] = useState({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    address: "",
    phoneNumber: "",
    avatar: "",
    userRole: "CLIENT"
  });
  const [ArtisanFormData, setArtisanFormData] = useState({
    email: "",
    password: "",
    name: "",
    bio: "",
    categoryName: "",
    siret: "",
    address: "",
    phoneNumber: "",
    activityStartDate: "",
    avatar: "",
    userRole: "ARTISAN"
  });

  const handleSubmitClient = async (e) => {
    e.preventDefault();
    try {
      const coords = await fetchCoordinates(ClientFormData.address);
      const { address, ...rest } = ClientFormData;
      const payload = { ...rest, latitude: coords.latitude, longitude: coords.longitude };

      const response = await fetch("http://localhost:8080/api/clients/register", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
          credentials: "include",
      });

      if (!response.ok) {
          setError("Veuillez remplir tous les champs obligatoires.");
          return;
      }

        setError("");
        console.log("Compte créé avec succès.");
        router.push("/");
    } catch (err) {
        console.error("Erreur réseau :", err);
        setError("Serveur inaccessible. Vérifiez votre connexion.");
    }
  };

  const handleSubmitArtisan = async (e) => {
    e.preventDefault();
    try {
      const coords = await fetchCoordinates(ArtisanFormData.address);
      const { address, ...rest } = ArtisanFormData;
      const payload = { ...rest, latitude: coords.latitude, longitude: coords.longitude };

      const response = await fetch("http://localhost:8080/api/artisans/register", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
          credentials: "include",
      });

      if (!response.ok) {
          setError("Veuillez remplir tous les champs obligatoires.");
          return;
      }

      setError("");
      console.log("Compte créé avec succès.");
      router.push("/");
    } catch (err) {
        console.error("Erreur réseau :", err);
        setError("Serveur inaccessible. Vérifiez votre connexion.");
    }
  };

    const handleChange = (event) => {
        const { name, value } = event.target;
        setClientFormData((prev) => ({
            ...prev,
            [name]: value
        }));
    };

  return (
    <div className="mt-20 flex flex-col items-center justify-center px-4 md:px-0">
      <h1 className="text-center text-blue text-xl font-normal font-cabin mb-8">
        Création de compte
      </h1>

      {/* Choix du rôle */}
      {!role && (
        <div className="flex gap-4 mb-8">
          <button
            onClick={() => setRole("client")}
            className="w-48 h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold
                       text-gold text-base font-normal font-cabin flex items-center justify-center
                       hover:bg-blue transition"
          >
            Vous êtes un particulier
          </button>
          <button
            onClick={() => setRole("artisan")}
            className="w-48 h-10 rounded-[42.5px] bg-white border-2 border-solid border-silver
                       text-blue text-base font-normal font-cabin flex items-center justify-center
                       hover:bg-gray-100 transition"
          >
            Vous êtes un professionnel
          </button>
        </div>
      )}

      {/* Formulaire Client */}
      {role === "client" && (
        <form onSubmit={handleSubmitClient} className="flex flex-col items-center w-full max-w-md gap-6">
          <input
            name="firstName"
            value={ClientFormData.firstName}
            onChange={handleChange}
            placeholder="Votre prénom"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />
          <input
            name="lastName"
            value={ClientFormData.lastName}
            onChange={handleChange}
            placeholder="Votre nom"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />
          <input
            name="email"
            value={ClientFormData.email}
            onChange={handleChange}
            placeholder="Adresse email"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />
          <input
            name="password"
            value={ClientFormData.password}
            onChange={handleChange}
            type="password"
            placeholder="Mot de passe"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />
          <input
            name="phone"
            value={ClientFormData.phoneNumber}
            onChange={handleChange}
            placeholder="Numéro de téléphone (optionnel)"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />
          <input
            name="adresse"
            value={ClientFormData.adresse}
            onChange={handleChange}
            placeholder="Adresse (optionnel)"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />

          <button
            type="submit"
            className="w-1/2 h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold
                       text-gold text-base font-normal font-cabin flex items-center justify-center mx-auto
                       hover:bg-blue transition mt-4"
          >
            Créer mon compte
          </button>

          <button
            type="button"
            onClick={() => setRole(null)}
            className="text-blue mt-4 underline"
          >
            Retour
          </button>
        </form>
      )}

      {/* Formulaire Artisan */}
      {role === "artisan" && (
        <form onSubmit={handleSubmitArtisan} className="flex flex-col items-center w-full max-w-md gap-6">
          <input
            name="entreprise"
            value={ArtisanformData.name}
            onChange={handleChange}
            placeholder="Nom de votre entreprise"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />
          <input
            name="email"
            value={ArtisanformData.email}
            onChange={handleChange}
            placeholder="Adresse email"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />
          <input
            name="password"
            value={ArtisanformData.password}
            onChange={handleChange}
            type="password"
            placeholder="Mot de passe"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />
          <input
            name="phone"
            value={ArtisanformData.phoneNumber}
            onChange={handleChange}
            placeholder="Numéro de téléphone (optionnel)"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />
          <input
            name="adresse"
            value={ArtisanformData.adress}
            onChange={handleChange}
            placeholder="Adresse (optionnel)"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />
          <input
            name="siret"
            value={ArtisanformData.siret}
            onChange={handleChange}
            placeholder="SIRET"
            className="w-full h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)]
                       border-2 border-solid border-silver px-4 text-xs text-silver outline-none"
          />

          <button
            type="submit"
            className="w-1/2 h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold
                       text-gold text-base font-normal font-cabin flex items-center justify-center mx-auto
                       hover:bg-blue transition mt-4"
          >
            Créer mon compte
          </button>

          <button
            type="button"
            onClick={() => setRole(null)}
            className="text-blue mt-4 underline"
          >
            Retour
          </button>
        </form>
      )}
    </div>
  );
}
