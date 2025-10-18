"use client"
import { useState } from "react";
import Link from "next/link";

export default function AdminClient({ initialArtisans, initialCategories }) {
    const [artisans, setArtisans] = useState(initialArtisans);
    const [categories, setCategories] = useState(initialCategories);
    const [error, setError] = useState("");

    const handleDeleteArtisan = async (id) => {
        if (!confirm("Voulez-vous vraiment supprimer cet artisan ?")) return;

        try {
            const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/artisans/${id}/delete`, {
                method: "DELETE",
                credentials: "include",
            });
            if (response.ok) {
                setArtisans(artisans.filter((artisan) => artisan.id !== id));
                setError("");
            } else {
                setError("Erreur lors de la suppression de l'artisan");
            }
        } catch (err) {
            setError("Erreur réseau, impossible de supprimer l'artisan");
        }
    };

    const handleAddCategory = async (e) => {
        e.preventDefault();
        const name = e.target.categoryName.value;

        try {
            const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/artisan-category/creation`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name }),
                credentials: "include",
            });
            if (response.ok) {
                const newCategory = await response.json();
                setCategories([...categories, newCategory]);
                e.target.reset();
                setError("");
            } else {
                setError("Erreur lors de la création de la catégorie");
            }
        } catch (err) {
            setError("Erreur réseau, impossible de créer la catégorie.");
        }
    };

    return (
        <>
          {/* Section Artisans */}
          <div className="mb-12">
            <h2 className="text-blue text-lg mb-4">Gestion des artisans</h2>
            <div className="overflow-x-auto">
              <table className="w-full max-w-4xl mx-auto bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)] border-2 border-solid border-silver rounded-[42.5px]">
                <thead>
                  <tr className="text-blue text-sm">
                    <th className="p-4">Nom</th>
                    <th className="p-4">Email</th>
                    <th className="p-4">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {artisans.map((artisan) => (
                    <tr key={artisan.id} className="text-silver text-xs border-t border-silver">
                      <td className="p-4">{artisan.name}</td>
                      <td className="p-4">{artisan.email}</td>
                      <td className="p-4">
                        <Link
                          href={`/admin/artisans/${artisan.id}/edit`}
                          className="text-gold hover:underline mr-4"
                        >
                          Modifier
                        </Link>
                        <button
                          onClick={() => handleDeleteArtisan(artisan.id)}
                          className="text-red-500 hover:underline"
                        >
                          Supprimer
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
    
          {/* Section Ajouter une catégorie */}
          <div className="mb-12">
            <h2 className="text-blue text-lg mb-4">Ajouter une catégorie</h2>
            <form onSubmit={handleAddCategory}>
              <div className="md:w-[300px] h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)] border-2 border-solid border-silver flex items-center pl-4 pr-4 mb-6">
                <input
                  type="text"
                  name="categoryName"
                  placeholder="Nom de la catégorie"
                  className="w-full border-none outline-none text-silver text-xs"
                  required
                />
              </div>
              <button
                type="submit"
                className="w-1/2 h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold text-gold text-base font-normal font-cabin flex items-center justify-center mx-auto hover:cursor-pointer hover:bg-blue transition mt-8"
              >
                Ajouter
              </button>
            </form>
          </div>
    
          {error && (
            <div className="h-5 flex justify-center items-center mb-6">
              <p className="text-red-500 text-sm text-center">{error}</p>
            </div>
          )}
        </>
      );
}