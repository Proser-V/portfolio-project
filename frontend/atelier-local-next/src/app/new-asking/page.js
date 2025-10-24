"use client";

import { useEffect, useState } from "react";
import RegistrationClientModal from "../../components/RegistrationClientModal";
import { useUser } from "../../context/UserContext"
import getApiUrl from "@/lib/api";

export default function AskingsForm() {
  const [eventCategories, setEventCategories] = useState([]);
  const [allArtisanCategories, setAllArtisanCategories] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState("");
  const [selectedArtisan, setSelectedArtisan] = useState("");
  const [lieu, setLieu] = useState("");
  const [date, setDate] = useState("");
  const [askings, setAskings] = useState([]);
  const [visibleCategories, setVisibleCategories] = useState([]);
  const [showRegistrationModal, setShowRegistrationModal] = useState(false);
  const user = useUser();

  // --- Fetch event categories ---
  useEffect(() => {
    fetch(`${getApiUrl()}/api/event-categories/`)
      .then(async (res) => {
        if (!res.ok) throw new Error(`HTTP error ${res.status}`);
        const text = await res.text(); // récupère la réponse brute
        return text ? JSON.parse(text) : []; // si vide, retourne []
      })
      .then(data => setEventCategories(data || []))
      .catch(err => console.error(err));
  }, []);

  // --- Fetch artisan categories ---
  useEffect(() => {
    fetch(`${getApiUrl()}/api/artisan-category/`)
      .then(res => res.json())
      .then(data => setAllArtisanCategories(data || []))
      .catch(err => console.error(err));
  }, []);

  // --- Quand un événement est sélectionné ---
  useEffect(() => {
    if (!selectedEvent) {
      setVisibleCategories([]);
      setAskings([]);
      return;
    }

    fetch(`${getApiUrl()}/api/event-categories/${selectedEvent}/artisan-categories`)
      .then(res => res.json())
      .then(eventCats => {
        setVisibleCategories(eventCats);
        // Initialisation des askings pour les catégories de l'événement
        const initialAskings = eventCats.map(cat => ({
          title: "",
          content: "",
          artisanCategoryId: cat.id
        }));
        setAskings(initialAskings);
      })
      .catch(err => console.error(err));
  }, [selectedEvent]);

  // --- Ajout manuel d'une catégorie d'artisan ---
  useEffect(() => {
    if (!selectedArtisan) return;

    const catObj = allArtisanCategories.find(c => String(c.id) === String(selectedArtisan));
    if (!catObj) return;

    // Evite les doublons
    if (askings.some(a => a.artisanCategoryId === catObj.id)) {
      setSelectedArtisan("");
      return;
    }

    setVisibleCategories(prev => [...prev, catObj]);
    setAskings(prev => [
      ...prev,
      { title: "", content: "", artisanCategoryId: catObj.id }
    ]);

    setSelectedArtisan("");
  }, [selectedArtisan]);

  const handleRemoveCategory = (catId) => {
    setVisibleCategories(prev => prev.filter(c => c.id !== catId));
    setAskings(prev => prev.filter(a => a.artisanCategoryId !== catId));
  };

  const handleSubmit = async () => {
    if (!user) {
      setShowRegistrationModal(true);
      return;
    }

    for (const asking of askings) {
      const payload = {
        clientId: user.id,
        title: asking.title,
        content: asking.content,
        artisanCategoryId: asking.artisanCategoryId,
        eventCategoryId: selectedEvent || null,
        eventDate: date ? `${date}T00:00:00` : null,
        eventLocalisation: lieu || null,
      };

      try {
        const res = await fetch(`${getApiUrl()}/api/askings/creation`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
          credentials: "include"
        });
        if (!res.ok) throw new Error(`Erreur HTTP ${res.status}`);
      } catch (err) {
        console.error(err);
      }
    }
    alert("Vos demandes ont été envoyées !");
    window.location.href="/";
  };

  return (
    <div className="mt-6 flex flex-col items-center justify-center px-4 md:px-0">
      <h1 className="text-center text-blue text-xl font-normal font-cabin mb-4">
        Postez une demande aux artisans locaux
      </h1>
      <p className="text-sm text-silver text-center">
        Choisissez une ou plusieurs catégories d'artisans.
        <br />
        Une notification sera envoyée à chaque artisan concerné.
      </p>

      {/* Type d'événement */}
      <div className="flex flex-col items-center">
        <label className="font-medium mb-2">
          Je prépare un événement (optionnel) :
        </label>
        <select
          value={selectedEvent}
          onChange={(e) => setSelectedEvent(e.target.value)}
          className="border border-silver rounded-full px-4 py-2 mb-4 font-cabin"
        >
          <option value="">-- Choisissez --</option>
          {eventCategories.map(cat => (
            <option key={cat.id} value={cat.id}>{cat.name}</option>
          ))}
        </select>
      </div>

      {/* Date + Lieu */}
      <div className="flex flex-col md:flex-row justify-center items-center md:gap-4 gap-1">
        <label className="block md:mb-2 text-sm">Date :</label>
        <input
          type="date"
          value={date}
          onChange={e => setDate(e.target.value)}
          className={`border border-silver rounded-full px-4 py-2 font-cabin ${!selectedEvent ? "bg-silver cursor-not-allowed" : ""}`}
          disabled={!selectedEvent}
        />
        <label className="block md:mb-2 text-sm">Lieu :</label>
        <input
          type="text"
          value={lieu}
          onChange={e => setLieu(e.target.value)}
          placeholder="Ex: Dijon"
          className={`border border-silver rounded-full px-4 py-2 font-cabin ${!selectedEvent ? "bg-silver cursor-not-allowed" : ""}`}
          disabled={!selectedEvent}
        />
      </div>

      <div className="w-[80%] h-px bg-silver my-4 mx-auto"></div>

      {/* Sélecteur de catégorie */}
      <div className="flex flex-row gap-4 items-center justify-center w-full">
        <label className="font-medium font-cabin mb-2">Vous cherchez :</label>
        <div className="flex gap-2">
          <select
            value={selectedArtisan}
            onChange={e => setSelectedArtisan(e.target.value)}
            className="border border-silver rounded-full h-10 px-8 font-cabin"
          >
            <option value="">-- Ajoutez --</option>
            {allArtisanCategories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Liste des besoins */}
      <div className="w-full mt-4 items-center">
        <p className="font-cabin text-base text-center mb-3">
          Décrivez votre besoin pour chaque catégorie :
        </p>
        <div className="space-y-2 w-full max-w-xl mx-auto">
          {askings.map((asking, index) => {
            const cat = visibleCategories.find(c => c.id === asking.artisanCategoryId);
            if (!cat) return null;

            return (
              <div
                key={cat.id}
                className="relative bg-white rounded-3xl shadow-md border-2 border-solid border-silver px-5"
              >
                {/* En-tête */}
                <div className="flex items-center justify-center relative h-10">
                  <h4 className="text-gold font-semibold text-center text-lg leading-tight">
                    {cat.name}
                  </h4>
                  <button
                    type="button"
                    onClick={() => handleRemoveCategory(cat.id)}
                    className="absolute right-0 text-blue border border-blue rounded-full w-6 h-6 flex items-center justify-center hover:bg-blue hover:text-white transition"
                  >
                    &times;
                  </button>
                </div>

                {/* Titre */}
                <input
                  type="text"
                  value={asking.title}
                  onChange={e => {
                    const newAskings = [...askings];
                    newAskings[index].title = e.target.value;
                    setAskings(newAskings);
                  }}
                  className="text-blue text-sm font-cabin text-left h-10 w-full border-0 border-b-2 border-silver"
                  placeholder="Donnez un titre à votre demande..."
                />

                {/* Contenu */}
                <textarea
                  value={asking.content}
                  onChange={e => {
                    const newAskings = [...askings];
                    newAskings[index].content = e.target.value;
                    setAskings(newAskings);
                  }}
                  className="w-full h-28 resize-none border-none bg-transparent 
                             text-blue text-sm leading-relaxed font-cabin text-left 
                             focus:outline-none"
                  placeholder="Décrivez votre besoin en détail..."
                  rows={3}
                />
              </div>
            );
          })}
        </div>
      </div>

      {/* Bouton final */}
      <button
        type="submit"
        onClick={(e) => {
          e.preventDefault();
          if (!user) {
            setShowRegistrationModal(true);
            return;
          }
          handleSubmit();
        }}
        disabled={visibleCategories.length === 0}
        className={`mt-8 w-3/4 h-10 rounded-[42.5px] font-cabin border-2 border-solid text-base text-center px-8 shadow-md transition
          ${visibleCategories.length === 0
            ? "bg-white text-silver border-silver cursor-not-allowed"
            : "bg-blue text-gold border-gold hover:bg-blue cursor-pointer"
          }`}
      >
        Poster ma demande
      </button>

      {/* Modal inscription */}
      <RegistrationClientModal
        isOpen={showRegistrationModal}
        onClose={() => setShowRegistrationModal(false)}
        onSuccess={() => {
          setUser({ id: "nouvelUtilisateur" }); // simulation user
          setShowRegistrationModal(false);
          handleSubmit(); // relance l'envoi
        }}
      />
    </div>
  );
}
