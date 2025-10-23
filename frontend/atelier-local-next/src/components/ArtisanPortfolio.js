"use client";
import { useState } from "react";
import Image from "next/image";

export default function ArtisanPortfolio({ 
  artisanId, 
  initialPhotos, 
  isOwner 
}) {
  const [photos, setPhotos] = useState(initialPhotos || []);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isManaging, setIsManaging] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadError, setUploadError] = useState(null);
  const [isMobile, setIsMobile] = useState(false);

  // Si pas de photos et pas propriétaire, ne rien afficher
  if (photos.length === 0 && !isOwner) {
    return null;
  }

  // ============ UPLOAD ============
  const handleFileUpload = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Vérifie doublon par nom de fichier
    if (photos.some((p) => p.uploadedPhotoUrl?.endsWith(file.name))) {
        setUploadError("Vous avez déjà uploadé ce fichier.");
        return;
    }

    const allowedTypes = ["image/png", "image/jpeg", "image/jpg"];
    if (!allowedTypes.includes(file.type)) {
      setUploadError("Format non autorisé. Utilisez PNG ou JPEG.");
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      setUploadError("Fichier trop volumineux. Maximum 5 Mo.");
      return;
    }

    setIsUploading(true);
    setUploadError(null);

    try {
      const formData = new FormData();
      formData.append("file", file);

      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/api/artisans/${artisanId}/portfolio/upload`,
        {
          method: "POST",
          credentials: "include",
          body: formData,
        }
      );

      if (!res.ok) {
        const errorText = await res.text();
        throw new Error(errorText || "Erreur lors de l'upload");
      }

      const newPhoto = await res.json();

      setPhotos((prev) => [...prev, {
        id: newPhoto.id,
        uploadedPhotoUrl: newPhoto.fileUrl,
        extension: newPhoto.extension,
      }]);

      e.target.value = "";
      
    } catch (err) {
      console.error("Upload error:", err);
      setUploadError(err.message);
    } finally {
      setIsUploading(false);
    }
  };

  // ============ SUPPRESSION ============
  const handleDeletePhoto = async (photoId) => {
    if (!confirm("Êtes-vous sûr de vouloir supprimer cette photo ?")) {
      return;
    }

    try {
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/api/artisans/${artisanId}/portfolio/${photoId}/delete`,
        {
          method: "DELETE",
          credentials: "include",
        }
      );

      if (!res.ok) {
        throw new Error("Erreur lors de la suppression");
      }

      setPhotos((prev) => prev.filter((p) => p.id !== photoId));
      
      // Ajuste l'index si nécessaire
      if (currentIndex >= photos.length - 1 && currentIndex > 0) {
        setCurrentIndex(currentIndex - 1);
      }
      
    } catch (err) {
      console.error("Delete error:", err);
      alert("Erreur lors de la suppression de la photo");
    }
  };

  // ============ NAVIGATION CAROUSEL ============
  const goToPrevious = () => {
    setCurrentIndex((prev) => 
      prev === 0 ? photos.length - 1 : prev - 1
    );
  };

  const goToNext = () => {
    setCurrentIndex((prev) => 
      prev === photos.length - 1 ? 0 : prev + 1
    );
  };

const getVisibleImages = () => {
  if (photos.length === 0) return [];

  if (photos.length === 1) {
    return [{ photo: photos[0], index: 0, position: "center" }];
  }

  if (photos.length === 2) {
    const firstIndex = currentIndex;
    const secondIndex = (currentIndex + 1) % 2;

    return [
      { photo: photos[secondIndex], index: secondIndex, position: "left", key: `${secondIndex}-left` },
      { photo: photos[firstIndex], index: firstIndex, position: "center", key: `${firstIndex}-center` },
      { photo: photos[secondIndex], index: secondIndex, position: "right", key: `${secondIndex}-right` },
    ];
  }

    const prevIndex = currentIndex === 0 ? photos.length - 1 : currentIndex - 1;
    const nextIndex = (currentIndex + 1) % photos.length;

    return [
      { photo: photos[prevIndex], index: prevIndex, position: "left" },
      { photo: photos[currentIndex], index: currentIndex, position: "center" },
      { photo: photos[nextIndex], index: nextIndex, position: "right" },
    ];
  };

  const visibleImages = getVisibleImages();

  return (
    <div className="w-full mt-8 mb-8">
      {/* En-tête avec titre et bouton gérer */}
      <div className="flex gap-4 mb-4">
        <p className="text-center text-blue font-cabin underline">
          {isOwner ? "Mon Portfolio" : "Mes réalisations"}
        </p>
        {isOwner && (
          <button
            onClick={() => setIsManaging(!isManaging)}
            className="px-4 py-2 font-cabin bg-blue text-gold border-gold border-2 border-solid rounded-full transition text-sm"
          >
            {isManaging ? "Retour à la gallerie photo" : "Ajouter / Supprimer des photos"}
          </button>
        )}
      </div>

      {/* ============ MODE GESTION (propriétaire uniquement) ============ */}
      {isManaging && isOwner && (
        <div className="max-w-4xl mx-auto mb-6 p-6 bg-white border-solid border-gold border-2">
          {/* Upload */}
          <div className="mb-6">
            <label className="block text-blue font-semibold mb-2">
              Ajouter une photo
            </label>
            <div className="flex items-center gap-4">
              <input
                type="file"
                accept="image/png, image/jpeg"
                onChange={handleFileUpload}
                disabled={isUploading}
                className="block w-full text-sm text-silver
                  file:mr-4 file:py-2 file:px-4
                  file:rounded-full file:border-2
                  file:text-sm file:font-semibold
                  file:bg-blue file:text-gold
                  file:border-gold file:border-solid
                  file:font-cabin
                  file:cursor-pointer font-cabin
                  disabled:opacity-50 disabled:cursor-not-allowed"
              />
              {isUploading && (
                <span className="text-sm text-blue">Upload en cours...</span>
              )}
            </div>
            {uploadError && (
              <p className="text-red-500 text-sm mt-2">{uploadError}</p>
            )}
            <p className="text-xs text-silver mt-2">
              Format accepté : PNG, JPEG, JPG • Taille max : 5 Mo
            </p>
          </div>

          {/* Grille de gestion des photos */}
          {photos.length > 0 && (
            <div>
              <p className="text-blue font-semibold mb-3">
                Mes photos ({photos.length})
              </p>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {photos.map((photo) => (
                  <div key={photo.id} className="relative group">
                    <div className="relative w-full h-32">
                      <Image
                        src={photo.uploadedPhotoUrl || photo.fileUrl}
                        alt="Portfolio"
                        fill
                        className="object-cover rounded-lg"
                      />
                    </div>
                    <button
                      onClick={() => handleDeletePhoto(photo.id)}
                      className="absolute top-2 right-2 bg-blue text-gold
                                rounded-full w-6 h-6 flex items-center justify-center
                                opacity-0 group-hover:opacity-100 transition-opacity
                                border-gold"
                      title="Supprimer"
                    >
                      ✕
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* ============ MODE VISUALISATION (carousel) ============ */}
      {!isManaging && photos.length > 0 && (
        <div className="relative flex items-center justify-center gap-4 px-0 md:px-0">
          {/* Flèche gauche */}
          {photos.length > 1 && (
            <button
              onClick={goToPrevious}
              className="flex-shrink-0 w-6 h-48 md:w-6 md:h-48 bg-[#E8D4B8] hover:bg-[#d4c0a4] 
                         flex items-center justify-center transition-colors duration-200
                         shadow-md"
              aria-label="Image précédente"
            >
              <svg 
                width="24" 
                height="24" 
                viewBox="0 0 24 24" 
                fill="currentColor" 
                className="text-black"
              >
                <polygon points="20,2 3,12 21,22"></polygon>
              </svg>
            </button>
          )}

          {/* Container des images */}
<div className="flex items-center justify-center flex-1 max-w-5xl overflow-hidden">
  {visibleImages.map((img) => (
    <div
      key={`${img.index}-${img.position}`}
      className={`relative transition-transform duration-300 ease-in-out
        ${img.position === "center"
          ? "w-[65vw] h-[280px] md:w-[600px] md:h-[450px] z-20 bg-white shadow-xl scale-100"
          : "w-[180px] h-[140px] md:w-[300px] md:h-[250px] z-10 opacity-70 hidden sm:block bg-white shadow-md scale-90"
        }`}
      style={{
        transform: img.position === "right"
          ? "translateX(-15%) scale(0.9)"
          : img.position === "left"
          ? "translateX(15%) scale(0.9)"
          : "translateX(0%) scale(1)",
        zIndex: img.position === "center" ? 20 : 10,
      }}
    >
      <Image
        src={img.photo.uploadedPhotoUrl || img.photo.fileUrl}
        alt={`Portfolio image ${img.index + 1}`}
        fill
        sizes={
          img.position === "center"
            ? "(min-width: 768px) 600px, 260px"
            : "(min-width: 768px) 280px, 200px"
        }
        style={{
          objectFit: "contain",
          objectPosition: "center"
        }}
      />
    </div>
  ))}
</div>
          {/* Flèche droite */}
          {photos.length > 1 && (
            <button
              onClick={goToNext}
              className="flex-shrink-0 w-6 h-48 md:w-6 md:h-48 bg-[#E8D4B8] hover:bg-[#d4c0a4]
                         flex items-center justify-center transition-colors duration-200
                         shadow-md"
              aria-label="Image suivante"
            >
              <svg 
                width="24" 
                height="24" 
                viewBox="0 0 24 24" 
                fill="currentColor" 
                className="text-black"
              >
                <polygon points="4,2 21,12 3,22"></polygon>
              </svg>
            </button>
          )}
        </div>
      )}

      {/* Indicateurs de pagination */}
      {!isManaging && photos.length > 1 && (
        <div className="flex justify-center gap-2 mt-6">
          {photos.map((_, index) => (
            <button
              key={index}
              onClick={() => setCurrentIndex(index)}
              className={`w-3 h-2 rounded-full transition-all duration-200 ${
                index === currentIndex
                  ? "bg-gold w-8"
                  : "bg-white hover:bg-gold/50"
              }`}
              aria-label={`Aller à l'image ${index + 1}`}
            />
          ))}
        </div>
      )}

      {/* Message si portfolio vide (propriétaire uniquement) */}
      {!isManaging && photos.length === 0 && isOwner && (
        <p className="text-center text-silver italic">
          Vous n'avez pas encore de photos dans votre portfolio.
          <br />
          Cliquez sur "Gérer" pour en ajouter.
        </p>
      )}
    </div>
  );
}
