"use client"
import { useState } from "react";
import Image from "next/image";
import getApiUrl from "@/lib/api";

/**
 * Composant ArtisanAvatarUploader
 * -------------------------------
 * Permet à un artisan ou à un administrateur de modifier l'avatar d'un artisan.
 * Affiche un aperçu de l'image sélectionnée et gère l'upload vers le serveur.
 *
 * @param {Object} props - Propriétés du composant
 * @param {Object} props.artisan - Artisan concerné
 * @param {boolean} props.isOwner - Indique si l'utilisateur est le propriétaire de l'artisan
 * @param {boolean} props.isAdmin - Indique si l'utilisateur est administrateur
 *
 * @returns {JSX.Element} Composant permettant de sélectionner, prévisualiser et uploader un avatar
 */
export default function ArtisanAvatarUploader({ artisan, isOwner, isAdmin }) {
  const [preview, setPreview] = useState(null); // URL de prévisualisation de l'image
  const [selectedFile, setSelectedFile] = useState(null); // Fichier sélectionné
  const [isUploading, setIsUploading] = useState(false); // Etat d'upload en cours
  const canEdit = isAdmin || isOwner; // Vérifie si l'utilisateur peut éditer l'avatar

  /**
   * Gestion du changement de fichier
   * Crée un aperçu et stocke le fichier sélectionné
   */
  const handleFileChange = (e) => {
    if (!canEdit) return;
    const file = e.target.files[0];
    if (!file) return;
    setSelectedFile(file);
    setPreview(URL.createObjectURL(file));
  };

  /**
   * Upload de l'image vers l'API
   */
  const handleUpload = async () => {
    if (!canEdit || !selectedFile) return;
    setIsUploading(true);
    const formData = new FormData();
    formData.append("file", selectedFile);
    formData.append("userId", artisan.id);

    try {
      const res = await fetch(`${getApiUrl()}/api/avatar/upload`, {
        method: "POST",
        body: formData,
      });
      const data = await res.json();
      if (res.ok) {
        console.log("Upload réussi :", data.url);
        setPreview(data.url);
        setSelectedFile(null);
        // Recharge la page pour mettre à jour l'avatar
        window.location.reload();
      } else {
        alert(data.error || "Erreur d'upload");
      }
    } catch (err) {
      console.error(err);
      alert("Erreur réseau lors de l'upload.");
    } finally {
      setIsUploading(false);
    }
  };

  /**
   * Annule la sélection d'image
   */
  const handleCancel = () => {
    setPreview(null);
    setSelectedFile(null);
  };

  return (
    <div className="relative group w-[200px] h-[200px] md:w-[250px] md:h-[250px] overflow-hidden shadow-lg border-solid border-black">
      
      {/* Label cliquable pour ouvrir l'input file */}
      <label
        htmlFor={canEdit ? "avatarUpload" : undefined} 
        className={`block w-full h-full ${canEdit ? "cursor-pointer" : "cursor-default"}`}
      >
        {/* Affiche l'image actuelle ou le preview */}
        <Image
          src={preview || artisan.avatar?.url || "/placeholder.png"}
          alt={`${artisan.name} avatar`}
          fill
          sizes="(max-width: 768px) 200px, 250px"
          className="object-cover"
        />
      </label>

      {/* Input caché pour choisir un fichier */}
      <input
        type="file"
        id="avatarUpload"
        accept="image/*"
        className="hidden"
        onChange={handleFileChange}
      />

      {/* Boutons valider / annuler affichés uniquement si un fichier est sélectionné */}
      {selectedFile && (
        <>
          {/* Bouton valider l'upload */}
          <div className="absolute bottom-2 left-0 w-full flex justify-center">
            <button
              onClick={handleUpload}
              disabled={isUploading}
              className="bborder-solid border-gold bg-blue text-gold px-4 py-1 rounded-full shadow-md cursor-pointer"
            >
              {isUploading ? "..." : "Valider"}
            </button>
          </div>

          {/* Bouton annuler */}
          <button
            onClick={handleCancel}
            className="absolute top-2 right-2 bg-blue text-gold border-solid border-gold rounded-full w-6 h-6 flex items-center justify-center"
          >
            ✕
          </button>
        </>
      )}
    </div>
  );
}
