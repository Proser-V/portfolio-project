"use client";

import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import fetchCoordinates from "../../utils/fetchCoordinates";

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

      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/clients/register`, {
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
        await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/avatar/upload`, {
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
          <input name="firstName" value={clientData.firstName} onChange={handleChange} placeholder="Prénom" className="input" />
          <input name="lastName" value={clientData.lastName} onChange={handleChange} placeholder="Nom" className="input" />
          <input name="email" value={clientData.email} onChange={handleChange} placeholder="Email" className="input" />
          <input type="password" name="password" value={clientData.password} onChange={handleChange} placeholder="Mot de passe" className="input" />
          <input name="phoneNumber" value={clientData.phoneNumber} onChange={handleChange} placeholder="Téléphone (optionnel)" className="input" />
          <input name="address" value={clientData.address} onChange={handleChange} placeholder="Adresse (optionnel)" className="input" />
          <div className="flex items-center gap-2">
            <input type="file" name="avatar" onChange={handleChange} />
            {clientData.avatarPreview && (
              <>
                <img src={clientData.avatarPreview} alt="avatar preview" className="w-12 h-12 object-cover rounded-full" />
                <button type="button" onClick={handleRemoveAvatar}>&times;</button>
              </>
            )}
          </div>
          <button type="submit" className="mt-4 bg-blue text-gold py-2 rounded-full">Créer mon compte</button>
        </form>
      </div>
    </div>
  );
}
