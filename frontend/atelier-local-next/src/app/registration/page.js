"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import fetchCoordinates from "../../../utils/fetchCoordinates";
import getApiUrl from "@/lib/api";

export default function RegistrationPage({ user }) {
  const router = useRouter();
  const [role, setRole] = useState("client");
  const [error, setError] = useState("");
  const [categories, setCategories] = useState([]);

  // Données formulaires
  const [clientData, setClientData] = useState({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    address: "",
    phoneNumber: "",
    avatar: "",
    avatarFile: null,
    avatarPreview: null,
    userRole: "CLIENT",
  });

  const [artisanData, setArtisanData] = useState({
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
    avatarFile: null,
    avatarPreview: null,
    userRole: "ARTISAN",
  });

  useEffect(() => {
    fetch(`${getApiUrl()}/api/artisan-category/`)
      .then((res) => res.json())
      .then(setCategories)
      .catch((err) => console.error("Erreur lors du chargement des catégories :", err));
  }, []);

  // Handlers Client
  const handleClientChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "avatar" && files[0]) {
      const file = files[0];
      const previewUrl = URL.createObjectURL(file);
      setClientData((prev) => ({
        ...prev,
        avatarFile: file,
        avatarPreview: previewUrl,
      }));
    } else {
      setClientData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleRemoveAvatar = () => {
    setClientData((prev) => ({
      ...prev,
      avatarFile: null,
      avatarPreview: null,
    }));
    setArtisanData((prev) => ({
      ...prev,
      avatarFile: null,
      avatarPreview: null,
    }));
    const fileInput = document.querySelector('input[name="avatar"]');
    if (fileInput) fileInput.value = "";
  };

  const handleClientSubmit = async (e) => {
    e.preventDefault();
    try {
      const coords = await fetchCoordinates(clientData.address);
      const { address, avatarFile, avatarPreview, ...rest } = clientData;
      const payload = { ...rest, latitude: coords.latitude, longitude: coords.longitude };
      
      const formData = new FormData();
      formData.append("client", new Blob([JSON.stringify(payload)], { type: "application/json" }));
      if (avatarFile) {
        formData.append("avatar", avatarFile);
        console.log("Avatar ajouté au FormData:", avatarFile.name, avatarFile.type);
      }

      console.log("FormData envoyé:", Array.from(formData.entries()).map(([k, v]) => [k, v instanceof Blob ? `Blob(${v.size} bytes)` : v]));

      const response = await fetch(`${getApiUrl()}/api/clients/register`, {
        method: "POST",
        body: formData,
        credentials: "include",
      });

      if (!response.ok) {
        let errorMessage = "Veuillez remplir tous les champs obligatoires.";
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorMessage;
        } catch {
          errorMessage = `Erreur ${response.status}: ${response.statusText}`;
        }
        setError(errorMessage);
        return;
      }

      const result = await response.json();
      console.log("Client créé avec succès:", result);
      
      // Vérifier si l'avatar a été enregistré
      if (avatarFile && result.avatar) {
        console.log("Avatar enregistré:", result.avatar);
      } else if (avatarFile && !result.avatar) {
        console.warn("⚠️ Avatar non enregistré côté backend");
      }

      if (clientData.avatarPreview) {
        URL.revokeObjectURL(clientData.avatarPreview);
      }

      router.push("/");
    } catch (err) {
      console.error("Erreur réseau :", err);
      setError("Serveur inaccessible. Vérifiez votre connexion.");
    }
  };

  // Handlers Artisan
  const handleArtisanChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "avatar" && files[0]) {
      const file = files[0];
      const previewUrl = URL.createObjectURL(file);
      setArtisanData((prev) => ({
        ...prev,
        avatarFile: file,
        avatarPreview: previewUrl,
      }));
    } else {
      setArtisanData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleArtisanSubmit = async (e) => {
    e.preventDefault();
    try {
      // Obtenir les coordonnées
      const coords = await fetchCoordinates(artisanData.address);
      const { address, avatarFile, avatarPreview, ...rest } = artisanData;
      const payload = { ...rest, latitude: coords.latitude, longitude: coords.longitude };

      // Créer la requête multipart/form-data
      const formData = new FormData();
      formData.append("artisan", new Blob([JSON.stringify(payload)], { type: "application/json" }));
      if (artisanData.avatarFile) {
        formData.append("avatar", artisanData.avatarFile);
      }

      const response = await fetch(`${getApiUrl()}/api/artisans/register`, {
        method: "POST",
        body: formData,
        credentials: "include",
      });

      if (!response.ok) {
        const errorData = await response.json();
        setError(errorData.message || "Veuillez remplir tous les champs obligatoires.");
        return;
      }

      if (artisanData.avatarPreview) {
        URL.revokeObjectURL(artisanData.avatarPreview);
      }

      setArtisanData((prev) => ({ ...prev, avatarFile: null, avatarPreview: null }));
      console.log("Compte artisan créé avec succès");
      router.push("/");
    } catch (err) {
      console.error("Erreur réseau :", err);
      setError("Serveur inaccessible. Vérifiez votre connexion.");
    }
  };

  // Animation Framer Motion
  const slideVariants = {
    initial: { x: "100%", opacity: 0 },
    animate: { x: 0, opacity: 1 },
    exit: { x: "-100%", opacity: 0 },
  };

  const activeBtnStyle = "bg-blue border-gold text-gold hover:bg-blue";
  const inactiveBtnStyle = "bg-white border-silver text-blue hover:bg-gray-100";

  return (
    <div className="mt-6 flex flex-col items-center justify-center px-4 lg:px-0">
      <h1 className="text-center text-blue text-xl font-normal font-cabin mb-4">
        Création de compte
      </h1>

      {/* Boutons de rôle */}
      <p className="text-silver text-sm">Vous souhaitez vous inscrire en tant que :</p>
      <div className="flex gap-4 mb-8">
        <button
          onClick={() => setRole("client")}
          className={`lg:w-[200px] w-1/2 h-10 rounded-[42.5px] border-2 border-solid 
                      text-base font-cabin flex items-center justify-center transition
                      ${role === "client" ? activeBtnStyle : inactiveBtnStyle}`}
        >
          Habitant
        </button>
        <button
          onClick={() => setRole("artisan")}
          className={`lg:w-[200px] w-1/2 px-8 h-10 rounded-[42.5px] border-2 border-solid 
                      text-base font-cabin flex items-center justify-center transition
                      ${role === "artisan" ? activeBtnStyle : inactiveBtnStyle}`}
        >
          Professionnel
        </button>
      </div>

      {error && <p className="text-red-500 text-sm mb-2">{error}</p>}

      {/* Zone d’animation */}
      <div className="relative flex w-full lg:w-screen items-center justify-center overflow-x-hidden overflow-y-auto py-2">
        <AnimatePresence mode="wait">
          {role === "client" ? (
            <motion.form
              key="client"
              variants={slideVariants}
              initial="initial"
              animate="animate"
              exit="exit"
              transition={{ duration: 0.3, ease: "easeInOut" }}
              onSubmit={handleClientSubmit}
              className="flex flex-col items-center justify-center w-full px-6"
            >
              <div className="flex flex-col lg:flex-row items-center justify-center w-full max-w-6xl">
                {/* Colonne gauche */}
                <div className="relative lg:w-48 w-36 aspect-square border-2 border-dashed border-silver flex items-center justify-center text-center mb-6">
                  <input
                    type="file"
                    name="avatar"
                    accept=".jpg,.jpeg,.png"
                    onChange={handleClientChange}
                    className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                  />
                  {clientData.avatarPreview ? (
                    <>
                      <img
                        src={clientData.avatarPreview}
                        alt="Prévisualisation de l'avatar"
                        className="w-full h-full object-cover rounded"
                      />
                      <button
                        type="button"
                        onClick={handleRemoveAvatar}
                        className="absolute top-2 right-2 bg-blue text-gold rounded-full w-6 h-6 flex items-center justify-center"
                      >
                        &times;
                      </button>
                    </>
                  ) : (
                    <span className="text-silver">Ajoutez une photo de profil (optionnel)</span>
                  )}
                </div>
                {/* Colonne droite */}
                <div className="flex flex-col items-center justify-center w-[100%] lg:w-1/2 gap-4">
                  <input name="firstName" value={clientData.firstName} onChange={handleClientChange} placeholder="Votre prénom" className="input" maxLength={50}/>
                  <input name="lastName" value={clientData.lastName} onChange={handleClientChange} placeholder="Votre nom" className="input" maxLength={50}/>
                  <input name="email" value={clientData.email} onChange={handleClientChange} placeholder="Adresse email" className="input" maxLength={100}/>
                  <input type="password" name="password" value={clientData.password} onChange={handleClientChange} placeholder="Mot de passe" className="input" />
                  <input name="address" value={clientData.address} onChange={handleClientChange} placeholder="Adresse" className="input" />
                  <input name="phoneNumber" value={clientData.phoneNumber} onChange={handleClientChange} placeholder="Téléphone (optionnel)" className="input" maxLength={12}/>
                </div>
              </div>

              <div className="flex justify-center w-full mt-8 mb-5">
                <button
                  type="submit"
                  className="w-3/4 lg:w-1/4 h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                            text-gold text-base font-normal font-cabin
                            flex items-center justify-center hover:cursor-pointer"
                >
                  Créer mon compte
                </button>
              </div>
            </motion.form>
          ) : (
            <motion.form
              key="artisan"
              variants={slideVariants}
              initial="initial"
              animate="animate"
              exit="exit"
              transition={{ duration: 0.3, ease: "easeInOut" }}
              onSubmit={handleArtisanSubmit}
              className="flex items-center justify-center w-full px-6"
            >
              <div className="flex flex-col lg:flex-row items-center justify-center w-full max-w-6xl">
                {/* Colonne gauche */}
                <div className="flex flex-col items-center justify-center w-full lg:w-1/3 gap-4">
                  {/* Avatar */}
                  <div className="relative w-36 lg:w-48 aspect-square border-2 border-dashed border-silver mb-6 overflow-hidden">
                    <input
                      type="file"
                      name="avatar"
                      accept=".jpg,.jpeg,.png"
                      onChange={handleArtisanChange}
                      className="absolute inset-0 w-full h-full opacity-0 cursor-pointer z-10"
                    />
                    {artisanData.avatarPreview ? (
                      <>
                        <img
                          src={artisanData.avatarPreview}
                          alt="Prévisualisation de l'avatar"
                          className="absolute inset-0 w-full h-full object-cover object-center"
                        />
                        <button
                          type="button"
                          onClick={handleRemoveAvatar}
                          className="absolute top-2 right-2 bg-blue text-gold rounded-full w-6 h-6 flex items-center justify-center z-20"
                        >
                          &times;
                        </button>
                      </>
                    ) : (
                      <div className="flex items-center justify-center w-full h-full text-silver text-center">
                        Ajoutez une photo de profil (optionnel)
                      </div>
                    )}
                  </div>

                  <div className="relative w-full flex justify-center">
                    <textarea
                      name="bio"
                      value={artisanData.bio}
                      onChange={handleArtisanChange}
                      placeholder="A propos de votre activité..."
                      className="textarea w-full hidden lg:block"
                      maxLength={500}
                    />
                    <p className="absolute -bottom-8 right-8 text-xs text-silver hidden lg:block">
                      {artisanData.bio.length}/500
                    </p>
                  </div>
                </div>

                {/* Colonne droite */}
                <div className="flex flex-col items-center justify-center w-full lg:w-3/4 gap-4 mt-4">
                  {/* Formulaire principal */}
                  <div className="flex flex-row items-center justify-center gap-2 w-[80%]">
                    {/* Catégorie */}
                    <div className="flex flex-col w-1/2 items-center justify-center">
                      <span className="text-xs text-silver mb-1 italic">Quel est votre métier ?</span>
                      <select
                        name="categoryName"
                        value={artisanData.categoryName}
                        onChange={handleArtisanChange}
                        className="input appearance-none text-center w-full"
                      >
                        <option value="">Votre catégorie pro</option>
                        {categories.map((cat) => (
                          <option key={cat.id} value={cat.name}>{cat.name}</option>
                        ))}
                      </select>
                    </div>

                    {/* Date */}
                    <div className="flex flex-col w-1/2 items-center justify-center">
                      <span className="text-xs text-silver mb-1 italic">Depuis quand?</span>
                      <input
                        type="date"
                        name="activityStartDate"
                        value={artisanData.activityStartDate}
                        onChange={handleArtisanChange}
                        className="input text-center w-full"
                      />
                    </div>
                  </div>

                  <input name="name" value={artisanData.name} onChange={handleArtisanChange} placeholder="Nom de votre entreprise" className="input" maxLength={50}/>
                  <input name="email" value={artisanData.email} onChange={handleArtisanChange} placeholder="Adresse email" className="input" maxLength={100}/>
                  <input type="password" name="password" value={artisanData.password} onChange={handleArtisanChange} placeholder="Mot de passe" className="input" />
                  <input name="address" value={artisanData.address} onChange={handleArtisanChange} placeholder="Votre adresse" className="input" />
                  <input name="siret" value={artisanData.siret} onChange={handleArtisanChange} placeholder="SIRET" className="input" maxLength={14}/>
                  <input name="phoneNumber" value={artisanData.phoneNumber} onChange={handleArtisanChange} placeholder="Votre numéro de téléphone (optionnel)" className="input" maxLength={12}/>

                  {/* Bio mobile */}
                  <div className="relative w-full flex justify-center">
                    <textarea
                      name="bio"
                      value={artisanData.bio}
                      onChange={handleArtisanChange}
                      placeholder="A propos de votre activité..."
                      className="textarea w-full lg:hidden block"
                      maxLength={500}
                    />
                    <p className="absolute -bottom-8 right-8 text-xs text-silver lg:hidden block">
                      {artisanData.bio.length}/500
                    </p>
                  </div>

                  <button
                    type="submit"
                    className="w-full lg:w-1/2 h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                                text-gold text-base font-normal font-cabin
                                flex items-center justify-center mx-auto hover:cursor-pointer 
                                mb-5 mt-8"
                  >
                    Créer mon compte
                  </button>
                </div>
              </div>
              </motion.form>
          )}
        </AnimatePresence>
      </div>
    </div>
  );
}
