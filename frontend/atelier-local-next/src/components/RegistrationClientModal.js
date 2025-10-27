"use client";

import { useState } from "react";
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
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "avatar" && files[0]) {
      const file = files[0];
      setClientData((prev) => ({
        ...prev,
        avatarFile: file,
        avatarPreview: URL.createObjectURL(file),
      }));
    } else {
      setClientData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleRemoveAvatar = () => {
    if (clientData.avatarPreview) {
      URL.revokeObjectURL(clientData.avatarPreview);
    }
    setClientData((prev) => ({ ...prev, avatarFile: null, avatarPreview: null }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      // 1. Vérifie les champs obligatoires
      const { firstName, lastName, email, password, address } = clientData;
      if (!firstName || !lastName || !email || !password || !address) {
        setError("Tous les champs obligatoires doivent être remplis.");
        setIsLoading(false);
        return;
      }

      // 2. Récupère les coordonnées GPS
      let latitude, longitude;
      try {
        const coords = await fetchCoordinates(address);
        latitude = coords.latitude;
        longitude = coords.longitude;
      } catch (err) {
        console.error("Géocodage échoué :", err);
        setError("Adresse invalide ou service de géocodage indisponible.");
        setIsLoading(false);
        return;
      }

      // 3. Prépare le payload JSON
      const { avatarFile, avatarPreview, address: _, ...rest } = clientData;
      const payload = {
        ...rest,
        latitude,
        longitude,
      };

      // 4. Crée FormData
      const formData = new FormData();
      formData.append(
        "client",
        new Blob([JSON.stringify(payload)], { type: "application/json" })
      );
      if (avatarFile) {
        formData.append("avatar", avatarFile);
      }

      // 5. Envoi en une seule requête
      const response = await fetch(`${getApiUrl()}/api/clients/register`, {
        method: "POST",
        body: formData,
        credentials: "include",
      });

      if (!response.ok) {
        let errorMsg = "Erreur lors de l'inscription.";
        try {
          const errData = await response.json();
          errorMsg = errData.message || errorMsg;
        } catch {
          errorMsg = `Erreur ${response.status}`;
        }
        setError(errorMsg);
        setIsLoading(false);
        return;
      }

      const result = await response.json();
      console.log("Inscription réussie :", result);

      // 6. Nettoie la prévisualisation
      if (avatarPreview) {
        URL.revokeObjectURL(avatarPreview);
      }

      // 7. Succès
      onSuccess(result); // Passe l'utilisateur créé
      onClose();

    } catch (err) {
      console.error("Erreur inattendue :", err);
      setError("Une erreur est survenue. Veuillez réessayer.");
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="bg-white rounded-2xl w-full max-w-2xl p-6 relative max-h-screen overflow-y-auto">
        <button
          className="absolute top-4 right-4 text-2xl text-blue hover:text-gold"
          onClick={onClose}
          disabled={isLoading}
        >
          ×
        </button>

        <h2 className="text-xl text-blue font-cabin mb-4 text-center">
          Créer un compte
        </h2>

        {error && (
          <p className="text-red-500 text-sm mb-3 text-center bg-red-50 p-2 rounded">
            {error}
          </p>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="flex flex-col md:flex-row gap-6 items-start">
            {/* Avatar */}
            <div className="relative w-48 h-48 border-2 border-dashed border-silver rounded flex items-center justify-center">
              <input
                type="file"
                name="avatar"
                accept=".jpg,.jpeg,.png"
                onChange={handleChange}
                className="absolute inset-0 opacity-0 cursor-pointer"
                disabled={isLoading}
              />
              {clientData.avatarPreview ? (
                <>
                  <img
                    src={clientData.avatarPreview}
                    alt="Avatar"
                    className="w-full h-full object-cover rounded"
                  />
                  <button
                    type="button"
                    onClick={handleRemoveAvatar}
                    className="absolute top-2 right-2 bg-blue text-gold rounded-full w-6 h-6 text-sm"
                    disabled={isLoading}
                  >
                    ×
                  </button>
                </>
              ) : (
                <span className="text-silver text-sm text-center px-2">
                  Photo de profil<br />(optionnel)
                </span>
              )}
            </div>

            {/* Champs */}
            <div className="flex-1 space-y-3 w-full">
              <input
                name="firstName"
                value={clientData.firstName}
                onChange={handleChange}
                placeholder="Prénom *"
                className="input w-full"
                required
                disabled={isLoading}
              />
              <input
                name="lastName"
                value={clientData.lastName}
                onChange={handleChange}
                placeholder="Nom *"
                className="input w-full"
                required
                disabled={isLoading}
              />
              <input
                name="email"
                type="email"
                value={clientData.email}
                onChange={handleChange}
                placeholder="Email *"
                className="input w-full"
                required
                disabled={isLoading}
              />
              <input
                type="password"
                name="password"
                value={clientData.password}
                onChange={handleChange}
                placeholder="Mot de passe *"
                className="input w-full"
                required
                minLength={6}
                disabled={isLoading}
              />
              <input
                name="address"
                value={clientData.address}
                onChange={handleChange}
                placeholder="Adresse complète *"
                className="input w-full"
                required
                disabled={isLoading}
              />
              <input
                name="phoneNumber"
                value={clientData.phoneNumber}
                onChange={handleChange}
                placeholder="Téléphone (optionnel)"
                className="input w-full"
                disabled={isLoading}
              />
            </div>
          </div>

          <div className="flex justify-center mt-6">
            <button
              type="submit"
              disabled={isLoading}
              className={`w-1/2 h-10 rounded-[42.5px] font-cabin text-base transition
                ${isLoading 
                  ? "bg-gray-400 text-white cursor-not-allowed" 
                  : "bg-blue text-gold border-2 border-gold hover:bg-blue-dark"
                }`}
            >
              {isLoading ? "Création..." : "Créer mon compte"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}