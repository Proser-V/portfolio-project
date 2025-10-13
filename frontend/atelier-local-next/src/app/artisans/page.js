"use client";
import { useEffect, useState } from "react";
import ArtisanCard from "@/components/ArtisanCard";

export default function ArtisansPage({ user }) {
  const [allArtisanCategories, setAllArtisanCategories] = useState([]);
  const [allArtisans, setAllArtisans] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [nameFilter, setNameFilter] = useState("");

  // --- Fetch les catégories d'artisan ---
  useEffect(() => {
    fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/artisan-category/`)
      .then(res => res.json())
      .then(data => {
        console.log("Categories fetched:", data);
        setAllArtisanCategories(Array.isArray(data) ? data : []);
      })
      .catch(err => console.error("Fetch categories error:", err));
  }, []);

  // --- Fetch artisans ---
  useEffect(() => {
    fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/artisans/`)
      .then(async res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        const text = await res.text();
        const data = text ? JSON.parse(text) : [];
        console.log("Artisans fetched:", data);
        return data;
      })
      .then(data => setAllArtisans(Array.isArray(data) ? data : []))
      .catch(err => console.error("Fetch artisans error:", err));
  }, []);

  // --- Filtrer et trier les artisans ---
  const filteredArtisans = allArtisans
    .filter(artisan => {
      // Filtre par nom (insensible à la casse, réactif lettre par lettre)
      const matchesName = !nameFilter || 
        (artisan.name || "").toLowerCase().includes(nameFilter.toLowerCase());

      // Filtre par catégorie (comparer avec categoryName)
      const matchesCategory =
        selectedCategory === "" ||
        artisan.categoryName === selectedCategory;

      return matchesName && matchesCategory;
    })
    // --- Appliquer le tri uniquement si un filtre est actif ---
    .sort((a, b) => {
      // Si aucun filtre n'est actif, ne pas trier (retourner 0)
      if (!nameFilter && selectedCategory === "") {
        return 0;
      }
      // Si un filtre est actif, trier par catégorie puis par nom
      const catA = a.categoryName || "Sans catégorie";
      const catB = b.categoryName || "Sans catégorie";
      const catComparison = catA.localeCompare(catB);
      if (catComparison !== 0) return catComparison;
      return (a.name || "").localeCompare(b.name || "");
    });

  return (
    <div className="mt-6 flex flex-col items-center justify-center px-4 md:px-0">
      <h1 className="text-center text-blue text-xl font-normal font-cabin mb-4">
        Liste des artisans
      </h1>
      {/* Filtres */}
      <p className="text-sm">Filtrer les artisans par</p>
      <div className="flex flex-col justify-center items-center md:gap-4 gap-2">
        <div className="w-full flex flex-row justify-center items-center md:gap-4 gap-1">
          <label className="w-[25%] md:mb-2 text-sm text-right">Nom :</label>
          <input
            type="text"
            value={nameFilter}
            onChange={e => setNameFilter(e.target.value)}
            className="input"
          />
        </div>
        <div className="w-full flex flex-row justify-center items-center md:gap-4 gap-1">
          <label className="w-[25%] text-sm text-right">Catégorie :</label>
          <select
            value={selectedCategory}
            onChange={e => setSelectedCategory(e.target.value)}
            className="input"
          >
            <option value="">-- Sélectionnez une catégorie --</option>
            {allArtisanCategories.map(cat => (
              <option key={cat.id} value={cat.name}>
                {cat.name || "Sans nom"}
              </option>
            ))}
          </select>
        </div>
      </div>
      <div className="mt-4 flex flex-col gap-4 w-full max-w-4xl">
        {filteredArtisans.length > 0 ? (
          filteredArtisans.map(artisan => (
            <ArtisanCard
              key={artisan.id}
              artisan={artisan}
              className="w-full"
            />
          ))
        ) : (
          <p className="text-center text-silver">
            Aucun artisan ne correspond aux critères.
          </p>
        )}
      </div>
    </div>
  );
}