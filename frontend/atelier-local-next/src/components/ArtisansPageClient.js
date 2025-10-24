"use client";
import { useState } from "react";
import ArtisanCard from "@/components/ArtisanCard";

/**
 * ArtisansPageClient
 *
 * Page affichant la liste des artisans pour un client.
 * Permet de filtrer par nom et par catégorie/métier.
 *
 * Fonctionnalités :
 * - Barre de filtrage par nom et catégorie
 * - Liste d’artisans filtrée et triée
 * - Affichage conditionnel si aucun artisan ne correspond
 * - Chaque artisan est affiché via le composant ArtisanCard
 *
 * @component
 *
 * @param {Object} props - Les props du composant
 * @param {Array<Object>} props.initialCategories - Liste des catégories d’artisans disponibles
 * @param {Array<Object>} props.initialArtisans - Liste initiale de tous les artisans
 *
 * @example
 * <ArtisansPageClient
 *    initialCategories={[{id: 1, name: "Plombier"}, {id: 2, name: "Électricien"}]}
 *    initialArtisans={[{id: 1, name: "Dupont", categoryName: "Plombier"}]}
 * />
 */
export default function ArtisansPageClient({ initialCategories, initialArtisans }) {
  // États pour les filtres
  const [selectedCategory, setSelectedCategory] = useState(""); // Catégorie sélectionnée
  const [nameFilter, setNameFilter] = useState("");             // Filtre par nom

  // Filtrage et tri des artisans selon les filtres
  const filteredArtisans = initialArtisans
    .filter(artisan => {
      const matchesName = !nameFilter || 
        (artisan.name || "").toLowerCase().includes(nameFilter.toLowerCase());

      const matchesCategory =
        selectedCategory === "" ||
        artisan.categoryName === selectedCategory;

      return matchesName && matchesCategory;
    })
    .sort((a, b) => {
      // Si pas de filtre, conserver l’ordre initial
      if (!nameFilter && selectedCategory === "") {
        return 0;
      }
      const catA = a.categoryName || "Sans catégorie";
      const catB = b.categoryName || "Sans catégorie";
      const catComparison = catA.localeCompare(catB);
      if (catComparison !== 0) return catComparison;
      return (a.name || "").localeCompare(b.name || "");
    });

  return (
    <div className="mt-6 flex flex-col items-center justify-center px-4 md:px-0">
      {/* Titre de la page */}
      <h1 className="text-center text-blue text-xl font-normal font-cabin mb-4">
        Liste des artisans
      </h1>
      
      {/* Section de filtrage */}
      <p className="text-sm">Filtrer les artisans par</p>
      <div className="flex flex-col justify-center items-center md:gap-4 gap-2">
        {/* Filtre par nom */}
        <div className="w-full flex flex-row justify-center items-center md:gap-4 gap-1">
          <label className="w-[30%] md:mb-2 text-sm text-right whitespace-nowrap">Nom :</label>
          <input
            type="text"
            value={nameFilter}
            onChange={e => setNameFilter(e.target.value)}
            className="input"
            placeholder="Rechercher par nom..."
          />
        </div>

        {/* Filtre par catégorie/métier */}
        <div className="w-full flex flex-row justify-center items-center md:gap-4 gap-1">
          <label className="w-[25%] text-sm text-right whitespace-nowrap">Métier :</label>
          <select
            value={selectedCategory}
            onChange={e => setSelectedCategory(e.target.value)}
            className="input"
          >
            <option value="">-- Toutes les catégories --</option>
            {initialCategories.map(cat => (
              <option key={cat.id} value={cat.name}>
                {cat.name || "Sans nom"}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Liste des artisans filtrés */}
      <div className="mt-4 flex flex-col gap-4 w-full max-w-4xl">
        {filteredArtisans.length > 0 ? (
          filteredArtisans.map(artisan => (
            <ArtisanCard
              key={artisan.id}   // Clé unique pour le rendu React
              artisan={artisan}  // Passage des données artisan au composant
              className="w-full" // Carte prend toute la largeur disponible
            />
          ))
        ) : (
          // Message si aucun artisan ne correspond aux critères
          <p className="text-center text-silver">
            Aucun artisan ne correspond aux critères.
          </p>
        )}
      </div>
    </div>
  );
}
