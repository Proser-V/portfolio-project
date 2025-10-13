"use client";
import { useEffect, useState } from "react";
import ArtisanCard from "@/components/ArtisanCard";

export default function ArtisansPage({ user }) {
  const [allArtisanCategories, setAllArtisanCategories] = useState([]);
  const [allArtisans, setAllArtisans] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [nameFilter, setNameFilter] = useState("");

  // --- Fetch les categories d'artisan ---
  useEffect(() => {
    fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/artisan-category/`)
      .then(res => res.json())
      .then(data => setAllArtisanCategories(data || []))
      .catch(err => console.error(err));
  }, []);

    // --- Fetch artisans ---
  useEffect(() => {
    fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/artisans/`)
      .then(async res => {
        if (!res.ok) {
          throw new Error(`HTTP error! status: ${res.status}`);
        }
        // Vérifie que le corps n'est pas vide avant de parser
        const text = await res.text();
        return text ? JSON.parse(text) : [];
      })
      .then(data => setAllArtisans(data || []))
      .catch(err => console.error("Fetch artisans error:", err));
  }, []);

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
            <option value="">-- Selectionnez une catégorie --</option>
            {allArtisanCategories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
        </select>
      </div>
    </div>
      <div className="mt-4 flex flex-col gap-4 w-full max-w-4xl">
        {allArtisans.map(artisan => (
          <ArtisanCard key={artisan.id} artisan={artisan} className="w-full" />
        ))}
      </div>
  </div>
  );
}