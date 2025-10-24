"use client";

import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import fetchCoordinates from "../../utils/fetchCoordinates";
import getApiUrl from "@/lib/api";

export default function RegistrationClientModal({ isOpen, onClose, onSuccess }) {
  const [clientData, setClientData] = useState({
    email: "",
    password: "",
    firstName: "",
    lastName: "",
    address: "",
    phoneNumber: "",
    avatarFile: null,
    avatarPreview: null,
    userRole: "CLIENT",
  });
  const [error, setError] = useState("");

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "avatar" && files[0]) {
      setClientData((prev) => ({
        ...prev,
        avatarFile: files[0],
        avatarPreview: URL.createObjectURL(files[0]),
      }));
    } else {
      setClientData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleRemoveAvatar = () => {
    setClientData((prev) => ({ ...prev, avatarFile: null, avatarPreview: null }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const coords = await fetchCoordinates(clientData.address);
      const { avatarFile, avatarPreview, ...rest } = clientData;
      const payload = { ...rest, latitude: coords.latitude, longitude: coords.longitude };

      const res = await fetch(`${getApiUrl()}/api/clients/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
        credentials: "include",
      });

      if (!res.ok) {
        setError("Erreur lors de l'inscription. Vérifiez les champs.");
        return;
      }

      const { uuid } = await res.json();

      if (avatarFile) {
        const formData = new FormData();
        formData.append("file", avatarFile);
        formData.append("userId", uuid);
        await fetch(`${getApiUrl()}/api/avatar/upload`, {
          method: "POST",
          body: formData,
          credentials: "include",
        });
      }

      if (avatarPreview) URL.revokeObjectURL(avatarPreview);

      onSuccess(); // callback pour signaler que l'inscription est faite
      onClose();
    } catch (err) {
      setError("Erreur réseau, veuillez réessayer.");
      console.error(err);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="bg-white rounded-2xl w-full max-w-2xl p-6 relative">
        <button
          className="absolute top-4 right-4 text-xl"
          onClick={onClose}
        >
          &times;
        </button>
        <h2 className="text-xl text-blue font-cabin mb-4 text-center">Créer un compte</h2>
        {error && <p className="text-red-500 text-sm mb-2">{error}</p>}
        <form onSubmit={handleSubmit} className="flex flex-col gap-3">
          <div className="flex flex-col md:flex-row items-center justify-center w-full max-w-6xl">
            {/* Colonne gauche */}
            <div className="relative w-48 aspect-square border-2 border-dashed border-silver flex items-center justify-center text-center mb-6">
              <input
                type="file"
                name="avatar"
                accept=".jpg,.jpeg,.png"
                onChange={handleChange}
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
            <div className="flex flex-col items-center justify-center md:w-3/4 gap-4">
              <input name="firstName" value={clientData.firstName} onChange={handleChange} placeholder="Votre prénom" className="input" maxLength={50}/>
              <input name="lastName" value={clientData.lastName} onChange={handleChange} placeholder="Votre nom" className="input" maxLength={50}/>
              <input name="email" value={clientData.email} onChange={handleChange} placeholder="Adresse email" className="input" maxLength={100}/>
              <input type="password" name="password" value={clientData.password} onChange={handleChange} placeholder="Mot de passe" className="input" />
              <input name="address" value={clientData.address} onChange={handleChange} placeholder="Adresse" className="input" />
              <input name="phoneNumber" value={clientData.phoneNumber} onChange={handleChange} placeholder="Téléphone (optionnel)" className="input" maxLength={12}/>
            </div>
          </div>

          <div className="flex justify-center w-full mt-8 mb-5">
            <button
              type="submit"
              className="w-1/2 h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                        text-gold text-base font-normal font-cabin
                        flex items-center justify-center hover:cursor-pointer"
            >
              Créer mon compte
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
