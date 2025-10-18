"use client";
import { useState } from "react";
import Image from "next/image";

export default function EditProfileModal({ artisan, address, isOpen, onClose, onSuccess }) {
  const [formData, setFormData] = useState({
    name: artisan.name || "",
    email: artisan.email || "",
    phoneNumber: artisan.phoneNumber || "",
    bio: artisan.bio || "",
    siret: artisan.siret || "",
    address: address || "",
    activityStartDate: artisan.activityStartDate || "",
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState(null);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);

    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/api/artisans/${artisan.id}/update`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
          body: JSON.stringify(formData),
        }
      );

      if (!res.ok) {
        const errorText = await res.text();
        throw new Error(errorText || "Erreur lors de la mise à jour");
      }

      // Succès
      alert("Profil mis à jour avec succès !");
      onSuccess();
      onClose();
      
      // Recharge la page pour voir les changements
      window.location.reload();
      
    } catch (err) {
      console.error("Update error:", err);
      setError(err.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed font-cabin inset-0 z-50 flex items-center justify-center p-4 bg-black/50">
      <div className="bg-white shadow-xl w-full max-w-[70vw] overflow-y-auto">
        {/* En-tête */}
        <div className="sticky top-0 bg-white border-b border-silver px-6 pt-4 flex justify-between items-center">
          <h2 className="text-2xl font-cabin text-blue">Modifier mon profil</h2>
          <button
            onClick={onClose}
            className="flex items-center justify-center w-8 h-8 rounded-full bg-blue text-gold border border-gold text-2xl font-cabin"
            disabled={isSubmitting}
          >
            &times;
          </button>
        </div>

        {/* Formulaire */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
              {error}
            </div>
          )}

          {/* Nom */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              Nom de l'entreprise <span className="text-red-700">*</span>
            </label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              className="w-[90%] px-4 py-2 border-solid border-silver rounded-full focus:ring-2 focus:ring-blue focus:border-transparent font-cabin"
            />
          </div>

          {/* Email */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              Email de contact <span className="text-red-700">*</span>
            </label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              className="w-[90%] px-4 py-2 border-solid border-silver rounded-full focus:ring-2 focus:ring-blue focus:border-transparent font-cabin"
            />
          </div>

          {/* SIRET */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              SIRET <span className="text-red-700">*</span>
            </label>
            <input
              type="text"
              name="siret"
              value={formData.siret}
              onChange={handleChange}
              required
              className="w-[90%] px-4 py-2 border-solid border-silver rounded-full focus:ring-2 focus:ring-blue focus:border-transparent font-cabin"
            />
          </div>

          {/* Adresse */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              Addresse <span className="text-red-700">*</span>
            </label>
            <input
              type="text"
              name="siret"
              value={formData.address}
              onChange={handleChange}
              required
              className="w-[90%] px-4 py-2 border-solid border-silver rounded-full focus:ring-2 focus:ring-blue focus:border-transparent font-cabin"
            />
          </div>

          {/* Date de début d'activité */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              Date de début d'activité <span className="text-red-700">*</span>
            </label>
            <input
              type="date"
              name="activityStartDate"
              value={formData.activityStartDate}
              onChange={handleChange}
              required
              className="w-[90%] px-4 py-2 border-solid border-silver rounded-full focus:ring-2 focus:ring-blue focus:border-transparent font-cabin"
            />
          </div>

          {/* Téléphone */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              Téléphone
            </label>
            <input
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              className="w-[90%] px-4 py-2 border-solid border-silver rounded-full focus:ring-2 focus:ring-blue focus:border-transparent font-cabin"
            />
          </div>

          {/* Bio */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              Bio
            </label>
            <textarea
              name="bio"
              value={formData.bio}
              onChange={handleChange}
              rows={5}
              className="w-[90%] px-4 py-2 border-solid border-silver rounded-lg focus:ring-2 focus:ring-blue focus:border-transparent resize-none font-cabin"
              placeholder="Parlez de votre entreprise, vos spécialités..."
            />
          </div>

          {/* Boutons */}
          <div className="flex flex-col md:flex-row gap-4 pt-4">
            <button
              type="submit"
              disabled={isSubmitting}
              className="flex-1 px-6 py-3 font-cabin bg-blue text-gold border-gold border-solid rounded-full transition disabled:opacity-50"
            >
              {isSubmitting ? "Enregistrement..." : "Sauvegarder les modifications"}
            </button>
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="flex-1 px-6 py-3 font-cabin border-solid border-silver text-gray-700 rounded-full hover:bg-gray-100 transition disabled:opacity-50"
            >
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
