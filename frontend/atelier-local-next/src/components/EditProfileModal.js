"use client";
import { useState } from "react";
import Image from "next/image";

export default function EditProfileModal({ artisan, isOpen, onClose, onSuccess }) {
  const [formData, setFormData] = useState({
    name: artisan.name || "",
    email: artisan.email || "",
    phoneNumber: artisan.phoneNumber || "",
    bio: artisan.bio || "",
    siret: artisan.siret || "",
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
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        {/* En-tête */}
        <div className="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex justify-between items-center">
          <h2 className="text-2xl font-cabin text-blue">Modifier mon profil</h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700 text-2xl"
            disabled={isSubmitting}
          >
            ×
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
              Nom de l'entreprise *
            </label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue focus:border-transparent"
            />
          </div>

          {/* Email */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              Email de contact *
            </label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue focus:border-transparent"
            />
          </div>

          {/* Téléphone */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              Téléphone *
            </label>
            <input
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              required
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue focus:border-transparent"
            />
          </div>

          {/* Bio */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              Biographie
            </label>
            <textarea
              name="bio"
              value={formData.bio}
              onChange={handleChange}
              rows={4}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue focus:border-transparent resize-none"
              placeholder="Parlez de votre entreprise, vos spécialités..."
            />
          </div>

          {/* SIRET */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              SIRET
            </label>
            <input
              type="text"
              name="siret"
              value={formData.siret}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue focus:border-transparent"
            />
          </div>

          {/* Date de début d'activité */}
          <div>
            <label className="block text-blue font-semibold mb-2">
              Date de début d'activité
            </label>
            <input
              type="date"
              name="activityStartDate"
              value={formData.activityStartDate}
              onChange={handleChange}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue focus:border-transparent"
            />
          </div>

          {/* Boutons */}
          <div className="flex gap-4 pt-4">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="flex-1 px-6 py-3 border-2 border-gray-300 text-gray-700 rounded-full hover:bg-gray-100 transition disabled:opacity-50"
            >
              Annuler
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="flex-1 px-6 py-3 bg-blue text-white rounded-full hover:bg-gold transition disabled:opacity-50"
            >
              {isSubmitting ? "Enregistrement..." : "Sauvegarder les modifications"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
