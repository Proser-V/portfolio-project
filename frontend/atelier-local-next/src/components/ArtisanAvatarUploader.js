"use client"
import { useState } from "react";
import Image from "next/image";

export default function ArtisanAvatarUploader({ artisan, isOwner, isAdmin }) {
  const [preview, setPreview] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const canEdit = isAdmin || isOwner;

  const handleFileChange = (e) => {
    if (!canEdit) return;
    const file = e.target.files[0];
    if (!file) return;
    setSelectedFile(file);
    setPreview(URL.createObjectURL(file));
  };

  const handleUpload = async () => {
    if (!canEdit || !selectedFile) return;
    setIsUploading(true);
    const formData = new FormData();
    formData.append("file", selectedFile);
    formData.append("userId", artisan.id);

    try {
      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/avatar/upload`, {
        method: "POST",
        body: formData,
      });
      const data = await res.json();
      if (res.ok) {
        console.log("Upload réussi :", data.url);
        setPreview(data.url);
        setSelectedFile(null);

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

  const handleCancel = () => {
    setPreview(null);
    setSelectedFile(null);
  };

  return (
    <div className="relative group w-[200px] h-[200px] md:w-[250px] md:h-[250px] overflow-hidden shadow-lg border-solid border-black">
      <label
        htmlFor={canEdit ? "avatarUpload" : undefined} 
        className={`block w-full h-full ${canEdit ? "cursor-pointer" : "cursor-default"}`}
      >
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

      {/* Boutons visibles seulement quand un nouveau fichier est choisi */}
      {selectedFile && (
        <>
          {/* Bouton valider */}
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