"use client"
import { useState } from "react";
import Link from "next/link";
import { useEffect } from "react";
import fetchCoordinates from "utils/fetchCoordinates";
import getApiUrl from "@/lib/api";

/**
 * Composant d'administration principal pour la gestion des artisans, clients et catégories.
 * 
 * @component
 * @param {Object} props - Les propriétés du composant
 * @param {Array} props.initialArtisans - Liste initiale des artisans
 * @param {Array} props.initialArtisanCategories - Liste initiale des catégories d'artisan
 * @param {Array} props.initialEventCategories - Liste initiale des catégories d'événement
 * @param {Object} props.currentUser - Utilisateur actuellement connecté
 * @returns {JSX.Element} Interface d'administration
 */
export default function AdminClient({ initialArtisans, initialArtisanCategories, initialEventCategories, currentUser }) {
  // ============================================================================
  // ÉTATS LOCAUX
  // ============================================================================
  
  /** @type {[Array, Function]} Liste des artisans */
  const [artisans, setArtisans] = useState(initialArtisans);
  
  /** @type {[Array, Function]} Liste des clients */
  const [clients, setClients] = useState([]);
  
  /** @type {[Array, Function]} Liste des demandes clients */
  const [askings, setAskings] = useState([]);
  
  /** @type {[Array, Function]} Liste des catégories d'artisan */
  const [artisanCategories, setArtisanCategories] = useState(initialArtisanCategories);
  
  /** @type {[Array, Function]} Liste des catégories d'événement */
  const [eventCategories, setEventCategories] = useState(initialEventCategories);
  
  /** @type {[string, Function]} ID de la catégorie d'artisan sélectionnée */
  const [selectedId, setSelectedId] = useState("");
  
  /** @type {[Array, Function]} Catégories d'artisan sélectionnées pour un événement */
  const [selectedArtisanCategories, setSelectedArtisanCategories] = useState([]);
  
  /** @type {[string, Function]} Message d'erreur à afficher */
  const [error, setError] = useState("");
  
  /** @type {[boolean, Function]} État d'ouverture de la section artisans */
  const [isOpenArtisan, setIsOpenArtisan] = useState(false);
  
  /** @type {[boolean, Function]} État d'ouverture de la section clients */
  const [isOpenClient, setIsOpenClient] = useState(false);
  
  /** @type {[boolean, Function]} État d'ouverture de la section catégories d'artisan */
  const [isOpenArtisanCategories, setIsOpenArtisanCategories] = useState(false);
  
  /** @type {[boolean, Function]} État d'ouverture de la section catégories d'événement */
  const [isOpenEventCategories, setIsOpenEventCategories] = useState(false);
  
  /** @type {[boolean, Function]} État d'ouverture de la section demandes */
  const [isOpenAsking, setIsOpenAsking] = useState(false);
  
  // ============================================================================
  // FONCTIONS DE BASCULEMENT D'AFFICHAGE
  // ============================================================================
  
  /** Bascule l'affichage de la section artisans */
  const toggleOpenArtisan = () => setIsOpenArtisan(!isOpenArtisan);
  
  /** Bascule l'affichage de la section clients */
  const toggleOpenClient = () => setIsOpenClient(!isOpenClient);
  
  /** Bascule l'affichage de la section catégories d'artisan */
  const toggleOpenArtisanCategories = () => setIsOpenArtisanCategories(!isOpenArtisanCategories);
  
  /** Bascule l'affichage de la section catégories d'événement */
  const toggleOpenEventCategories = () => setIsOpenEventCategories(!isOpenEventCategories);
  
  /** Bascule l'affichage de la section demandes */
  const toggleOpenAsking = () => setIsOpenAsking(!isOpenAsking);

  // ============================================================================
  // EFFETS - CHARGEMENT DES DONNÉES
  // ============================================================================
  
  /**
   * Récupère la liste des clients au montage du composant
   */
  useEffect(() => {
    const fetchClients = async () => {
      try {
        const res = await fetch(`${getApiUrl()}/api/clients/`, {
          method: "GET",
          credentials: "include",
        });
        if (!res.ok) throw new Error("Erreur lors de la récupération des clients");
        const data = await res.json();
        setClients(data);
        setError("");
      } catch (err) {
        setError(err.message || "Erreur réseau, impossible de récupérer les clients");
      }
    };

    fetchClients();
  }, [])

  /**
   * Récupère la liste des demandes clients au montage du composant
   */
  useEffect(() => {
    const fetchAskings = async () => {
      try {
        const res = await fetch(`${getApiUrl()}/api/askings/`, {
          method: "GET",
          credentials: "include",
        });
        if (!res.ok) throw new Error("Erreur lors de la récupération des demandes client");
        const data = await res.json();
        setAskings(data);
        setError("");
      } catch (err) {
        setError(err.message || "Erreur réseau, impossible de récupérer les demandes client");
      }
    };

    fetchAskings();
  }, [])

  // ============================================================================
  // FONCTIONS UTILITAIRES
  // ============================================================================
  
  /**
   * Récupère le nom complet d'un client à partir de son ID
   * 
   * @param {string} clientId - L'identifiant du client
   * @returns {string} Le nom complet du client ou "—" si non trouvé
   */
  function getClientName(clientId) {
    const c = clients.find(cl => cl.id === clientId);
    return c ? `${c.firstName} ${c.lastName}` : "—";
  };

  /**
   * Récupère le nom d'une catégorie d'artisan à partir de son ID
   * 
   * @param {string} categoryId - L'identifiant de la catégorie
   * @returns {string} Le nom de la catégorie ou "—" si non trouvée
   */
  function getArtisanCategoryName(categoryId) {
    const cat = artisanCategories.find(c => c.id === categoryId);
    return cat ? cat.name : "—";
  };

  /**
   * Récupère le nom d'une catégorie d'événement à partir de son ID
   * 
   * @param {string} categoryId - L'identifiant de la catégorie
   * @returns {string} Le nom de la catégorie ou "—" si non trouvée
   */
  function getEventCategoryName(categoryId) {
    const cat = eventCategories.find(c => c.id === categoryId);
    return cat ? cat.name : "—";
  };

  // ============================================================================
  // GESTIONNAIRES D'ÉVÉNEMENTS - CLIENTS
  // ============================================================================
  
  /**
   * Crée un nouveau compte administrateur
   * 
   * @async
   * @param {Event} e - L'événement de soumission du formulaire
   * @returns {Promise<void>}
   */
  const handleAddAdmin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const formData = new FormData(e.target);

      // Géocodage de l'adresse
      const address = formData.get("address");
      const coords = await fetchCoordinates(address);

      // Préparation des données client
      const clientData = {
        firstName: formData.get("firstName"),
        lastName: formData.get("lastName"),
        email: formData.get("email"),
        password: formData.get("password"),
        latitude: coords.latitude,
        longitude: coords.longitude,
        role: "ADMIN"
      };

      // Envoi de la requête
      const multipartFormData = new FormData();
      multipartFormData.append(
        "client",
        new Blob([JSON.stringify(clientData)], { type: "application/json" })
      );

      const res = await fetch(`${getApiUrl()}/api/clients/admin/create`, {
        method: "POST",
        body: multipartFormData,
        credentials: "include"
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "Erreur lors de la création de l'administrateur.");
      }

      alert("Administrateur créé avec succès !");
      e.target.reset();
    } catch (err) {
      console.error(err);
      setError(err.message || "Erreur réseau, veuillez réessayer.");
    }
  };

  /**
   * Bascule le statut actif/inactif d'un client (bannissement)
   * 
   * @async
   * @param {string} id - L'identifiant du client
   * @returns {Promise<void>}
   */
  const handleBanClient = async (id) => {

    // Vérification que l'utilisateur ne se bannit pas lui-même
    if (!currentUser) {
      alert("Utilisateur non chargé.");
      return;
    }

    if (id === currentUser.id) {
      alert("Vous ne pouvez pas vous bannir vous-même.");
      return;
    }
    
    const confirmAction = window.confirm("Voulez-vous vraiment modifier le statut de ce client ?");
    if (!confirmAction) return;

    try {
      const response = await fetch(`${getApiUrl()}/api/clients/${id}/moderate`,{
        method: "PATCH",
        credentials: "include"
      });
      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "Erreur lors de la création de l'administrateur.");
      }
      const patchedClient = await response.json();
      
      // Mise à jour de l'état local
      setClients((prevClients) =>
        prevClients.map((client) =>
          client.id === patchedClient.id
            ? { ...client, active: patchedClient.active }
            : client
        )
      );

      alert(`Le client ${patchedClient.firstName} ${patchedClient.lastName} est maintenant ${patchedClient.active ? "actif" : "banni"}`);
      setError("");
    } catch(err) {
      setError("Erreur réseau, impossible de mettre à jour le client.");
    }
  };

  /**
   * Supprime définitivement un client
   * 
   * @async
   * @param {string} id - L'identifiant du client
   * @returns {Promise<void>}
   */
  const handleDeleteClient = async (id) => {
    // Empêche l'auto-suppression
    if (id === currentUser.id) {
      alert("Vous ne pouvez pas supprimer votre compte.");
      return;
    }
    if (!confirm("Voulez-vous vraiment supprimer ce client ?")) return;

    try {
      const response = await fetch(`${getApiUrl()}/api/clients/${id}/delete`, {
        method: "DELETE",
        credentials: "include",
      });
      if (response.ok) {
        setClients(clients.filter((client) => client.id !== id));
        setError("");
      } else {
        setError("Erreur lors de la suppression du client");
      }
    } catch (err) {
      setError("Erreur réseau, impossible de supprimer le client");
    }
  };

  // ============================================================================
  // GESTIONNAIRES D'ÉVÉNEMENTS - ARTISANS
  // ============================================================================
  
  /**
   * Supprime définitivement un artisan
   * 
   * @async
   * @param {string} id - L'identifiant de l'artisan
   * @returns {Promise<void>}
   */
  const handleDeleteArtisan = async (id) => {
      if (!confirm("Voulez-vous vraiment supprimer cet artisan ?")) return;

      try {
          const response = await fetch(`${getApiUrl()}/api/artisans/${id}/delete`, {
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

  // ============================================================================
  // GESTIONNAIRES D'ÉVÉNEMENTS - CATÉGORIES D'ARTISAN
  // ============================================================================
  
  /**
   * Crée une nouvelle catégorie d'artisan
   * 
   * @async
   * @param {Event} e - L'événement de soumission du formulaire
   * @returns {Promise<void>}
   */
  const handleAddArtisanCategory = async (e) => {
      e.preventDefault();
      const name = e.target.categoryName.value;
      const description = e.target.description.value;

      try {
          const response = await fetch(`${getApiUrl()}/api/artisan-category/creation`, {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ name, description }),
              credentials: "include",
          });
          if (response.ok) {
              const newCategory = await response.json();
              setArtisanCategories([...artisanCategories, newCategory]);
              e.target.reset();
              setError("");
          } else {
              setError("Erreur lors de la création de la catégorie d'artisan");
          }
      } catch (err) {
          setError("Erreur réseau, impossible de créer la catégorie d'artisan.");
      }
  };

  /**
   * Supprime définitivement une catégorie d'artisan
   * 
   * @async
   * @param {string} id - L'identifiant de la catégorie
   * @returns {Promise<void>}
   */
  const handleDeleteArtisanCategory = async (id) => {
      if (!confirm("Voulez-vous vraiment supprimer cet catégorie ?")) return;

      try {
          const response = await fetch(`${getApiUrl()}/api/artisan-category/${id}/delete`, {
              method: "DELETE",
              credentials: "include",
          });
          if (response.ok) {
              setArtisanCategories(artisanCategories.filter((artisanCategory) => artisanCategory.id !== id));
              setError("");
          } else {
              setError("Erreur lors de la suppression de la catégorie d'artisan");
          }
      } catch (err) {
          setError("Erreur réseau, impossible de supprimer la catégorie d'artisan");
      }
  };

  // ============================================================================
  // GESTIONNAIRES D'ÉVÉNEMENTS - CATÉGORIES D'ÉVÉNEMENT
  // ============================================================================
  
  /**
   * Crée une nouvelle catégorie d'événement avec des catégories d'artisan associées
   * 
   * @async
   * @param {Event} e - L'événement de soumission du formulaire
   * @returns {Promise<void>}
   */
  const handleAddEventCategory = async (e) => {
      e.preventDefault();
      const name = e.target.name.value;
      const artisanCategoryIds = selectedArtisanCategories.map((cat) => cat.id);

      try {
          const response = await fetch(`${getApiUrl()}/api/event-categories/creation`, {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ name, artisanCategoryIds }),
              credentials: "include",
          });
          if (response.ok) {
              const newCategory = await response.json();
              setEventCategories([...eventCategories, newCategory]);
              e.target.reset();
              setSelectedArtisanCategories([]);
              setError("");
          } else {
              setError("Erreur lors de la création de la catégorie d'évènement");
          }
      } catch (err) {
          setError("Erreur réseau, impossible de créer la catégorie d'évènement.");
      }
  };

  /**
   * Supprime définitivement une catégorie d'événement
   * 
   * @async
   * @param {string} id - L'identifiant de la catégorie
   * @returns {Promise<void>}
   */
  const handleDeleteEventCategory = async (id) => {
      if (!confirm("Voulez-vous vraiment supprimer cette catégorie ?")) return;

      try {
          const response = await fetch(`${getApiUrl()}/api/event-categories/${id}/delete`, {
              method: "DELETE",
              credentials: "include",
          });
          if (response.ok) {
              setEventCategories(eventCategories.filter((eventCategory) => eventCategory.id !== id));
              setError("");
          } else {
              setError("Erreur lors de la suppression de la catégorie d'évènement");
          }
      } catch (err) {
          setError("Erreur réseau, impossible de supprimer la catégorie d'évènement");
      }
  };

  // ============================================================================
  // GESTIONNAIRES D'ÉVÉNEMENTS - DEMANDES CLIENT
  // ============================================================================
  
  /**
   * Supprime définitivement une demande client
   * 
   * @async
   * @param {string} id - L'identifiant de la demande
   * @returns {Promise<void>}
   */
  const handleDeleteAsking = async(id) => {
    if (!confirm("Voulez vous supprimer cette demande ?")) return;

    try {
      const response = await fetch(`${getApiUrl()}/api/askings/${id}/delete`, {
        method: "DELETE",
        credentials: "include",
      });
      if (response.ok) {
        setAskings(askings.filter((asking) => asking.id !== id));
        setError("");
      } else {
        setError("Erreur lors de la suppression de la demande.");
      }
    } catch (err) {
      setError("Erreur réseau, impossible de supprimer la demande.");
    }
  };

  // ============================================================================
  // RENDU DU COMPOSANT
  // ============================================================================
  
  return (
    <>
      {/* Affichage des erreurs */}
      {error && (
        <div className="h-5 flex justify-center items-center mb-6">
          <p className="text-red-500 text-sm text-center">{error}</p>
        </div>
      )}
      
      {/* ====================================================================== */}
      {/* SECTION: GESTION DES CATÉGORIES D'ARTISAN */}
      {/* ====================================================================== */}
      <div className="mb-4 w-[90%] mx-auto">
        <h2 className="text-blue text-lg text-center mb-4 cursor-pointer select-none flex items-center justify-center gap-2"
                        onClick={toggleOpenArtisanCategories}
        >
          Gestion des catégories d'artisan
          <span
            style={{
              display: "inline-block",
              transform: isOpenArtisanCategories ? "rotate(90deg)" : "rotate(0deg)",
              transition: "transform 0.3s",
              transformOrigin: "center",
            }}
          >
            <svg
              className="w-4 h-4"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </span>
        </h2>
        {isOpenArtisanCategories && (
          <>
            {/* Formulaire d'ajout de catégorie d'artisan */}
            <h3 className="text-gold text-base mb-4 text-center">
              Ajouter une catégorie
            </h3>

            <form
              onSubmit={handleAddArtisanCategory}
              className="flex flex-col md:flex-row items-center justify-center"
            >
              <div className="flex flex-col md:flex-row items-center justify-center gap-4 md:w-full w-3/4">
                <input
                  type="text"
                  name="categoryName"
                  placeholder="Nom de la catégorie"
                  className="input text-xs self-center"
                  maxLength={50}
                  required
                />
                <textarea
                  className="w-3/4 h-10 rounded-lg bg-white shadow-[0px_4px_4px_rgba(0,0,0,0.25)] font-cabin
                    border-2 border-solid border-silver px-4 py-2 text-xs text-blue outline-none resize-none"
                  placeholder="Courte description de la catégorie..."
                  name="description"
                  maxLength={200}
                ></textarea>
                <button
                  type="submit"
                  className="w-full h-10 rounded-full bg-blue border-2 border-solid border-gold text-gold text-base font-normal font-cabin flex items-center justify-center hover:cursor-pointer"
                >
                  Ajouter une catégorie
                </button>
              </div>
            </form>
            
            {/* Tableau des catégories existantes */}
            <h3 className="text-gold text-base mb-4 text-center">
              Catégories existantes
            </h3>
            <div className="overflow-x-auto transition-all duration-300">
            <table className="w-[90%] max-w-4xl mx-auto">
              <thead>
                <tr className="text-blue text-sm">
                  <th className="p-4">Nom</th>
                  <th className="p-4">Description</th>
                  <th className="p-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {artisanCategories?.map((artisanCategory) => (
                  <tr key={artisanCategory.id} className="text-blue text-xs border-t border-silver">
                    <td className="p-4 text-center">{artisanCategory.name}</td>
                    <td className="p-4 text-center">{artisanCategory.description}</td>
                    <td className="p-4 text-center">
                      <button
                        onClick={() => handleDeleteArtisanCategory(artisanCategory.id)}
                        className="text-red-500 text-xs hover:underline border-none bg-inherit font-cabin"
                      >
                        Supprimer
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          </>
        )}
      </div>

      {/* ====================================================================== */}
      {/* SECTION: GESTION DES CATÉGORIES D'ÉVÉNEMENT */}
      {/* ====================================================================== */}
      <div className="mb-4 w-[90%] mx-auto">
        <h2 className="text-blue text-lg text-center mb-4 cursor-pointer select-none flex items-center justify-center gap-2"
                        onClick={toggleOpenEventCategories}
        >
          Gestion des catégories d'évènement
          <span
            style={{
              display: "inline-block",
              transform: isOpenEventCategories ? "rotate(90deg)" : "rotate(0deg)",
              transition: "transform 0.3s",
              transformOrigin: "center",
            }}
          >
            <svg
              className="w-4 h-4"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </span>
        </h2>
        {isOpenEventCategories && (
          <>
            {/* Formulaire d'ajout de catégorie d'événement */}
            <h3 className="text-gold text-base mb-4 text-center">
              Ajouter une catégorie
            </h3>

            <form
              onSubmit={handleAddEventCategory}
              className="flex flex-col items-center justify-center"
            >
              <div className="flex flex-col md:flex-row items-center justify-center gap-4 md:w-full w-3/4">

                {/* Champ: Nom de la catégorie d'événement */}
                <input
                  type="text"
                  name="name"
                  placeholder="Nom de l'évènement..."
                  className="md:w-1/2 w-3/4 h-8 rounded-full bg-white shadow-[0px_4px_4px_rgba(0,0,0,0.25)] font-cabin
                              border-2 border-solid border-silver px-4 text-xs text-blue outline-none"
                  required
                />

                {/* Sélecteur: Catégories d'artisans associées */}
                <select
                  value={selectedId}
                  onChange={(e) => {
                    const id = e.target.value;
                    setSelectedId(id);

                    if (!id) return;
                    const selected = artisanCategories.find((c) => c.id === id);
                    if (selected && !selectedArtisanCategories.some((c) => c.id === selected.id)) {
                      setSelectedArtisanCategories([...selectedArtisanCategories, selected]);
                    }

                    // Réinitialise le select après sélection
                    setSelectedId("");
                  }}
                  className="input text-xs w-full md:flex-1 h-8 px-4 rounded-full border-2 border-solid border-silver text-blue outline-none"
                >
                  <option value="">-- Catégories d'artisan --</option>
                  {artisanCategories?.map((cat) => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name}
                    </option>
                  ))}
                </select>
              </div>

              {/* Affichage des catégories artisan sélectionnées (badges) */}
              {selectedArtisanCategories.length > 0 && (
                <div className="flex flex-wrap gap-2 justify-center mt-4 w-full">
                  {selectedArtisanCategories.map((cat) => (
                    <span
                      key={cat.id}
                      className="flex items-center gap-2 bg-silver text-blue px-3 py-1 rounded-full text-xs"
                    >
                      {cat.name}
                      <button
                        type="button"
                        onClick={() =>
                          setSelectedArtisanCategories(
                            selectedArtisanCategories.filter((c) => c.id !== cat.id)
                          )
                        }
                        className="text-blue rounded-full border-none bg-inherit h-6 w-6"
                      >
                        ✕
                      </button>
                    </span>
                  ))}
                </div>
              )}

              {/* Bouton de soumission */}
              <button
                type="submit"
                className="w-1/2 h-10 rounded-full bg-blue border-2 border-solid border-gold text-gold text-base font-normal font-cabin flex items-center justify-center hover:cursor-pointer mt-4"
              >
                Ajouter une catégorie
              </button>
            </form>

            {/* Tableau des catégories d'événement existantes */}
            <h3 className="text-gold text-base mb-4 text-center">
              Catégories existantes
            </h3>
            <div className="overflow-x-auto transition-all duration-300">
            <table className="w-[90%] max-w-4xl mx-auto">
              <thead>
                <tr className="text-blue text-sm">
                  <th className="p-4">Nom</th>
                  <th className="p-4">Catégories d'artisan</th>
                  <th className="p-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {eventCategories?.map((eventCategory) => (
                  <tr key={eventCategory.id} className="text-blue text-xs border-t border-silver">
                    <td className="p-4 text-center">{eventCategory.name}</td>
                    <td className="p-4 text-center">
                      {eventCategory.artisanCategoryIds
                        .map(id => {
                          const cat = artisanCategories.find(c => c.id === id);
                          return cat ? cat.name : null;
                        })
                        .filter(Boolean)
                        .join(", ") || "Aucune catégorie"}
                    </td>
                    <td className="p-4 text-center">
                      <button
                        onClick={() => handleDeleteEventCategory(eventCategory.id)}
                        className="text-red-500 text-xs hover:underline border-none bg-inherit font-cabin hover:cursor-pointer"
                      >
                        Supprimer
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          </>
        )}
      </div>

      {/* ====================================================================== */}
      {/* SECTION: GESTION DES ARTISANS */}
      {/* ====================================================================== */}
      <div className="mb-4 w-[90%] mx-auto">
        <h2
          className="text-blue text-lg text-center mb-4 cursor-pointer select-none flex items-center justify-center gap-2"
          onClick={toggleOpenArtisan}
        >
          Gestion des artisans
          <span
            style={{
              display: "inline-block",
              transform: isOpenArtisan ? "rotate(90deg)" : "rotate(0deg)",
              transition: "transform 0.3s",
              transformOrigin: "center",
            }}
          >
            <svg
              className="w-4 h-4"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </span>
        </h2>

        {/* Tableau des artisans */}
        {isOpenArtisan && (
          <div className="overflow-x-auto transition-all duration-300">
            <table className="w-[90%] max-w-4xl mx-auto">
              <thead>
                <tr className="text-blue text-sm">
                  <th className="p-4">Nom</th>
                  <th className="p-4">Email</th>
                  <th className="p-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {artisans.map((artisan) => (
                  <tr key={artisan.id} className="text-blue text-xs border-t border-silver">
                    <td className="p-4 text-center">{artisan.name}</td>
                    <td className="p-4 text-center">{artisan.email}</td>
                    <td className="p-4 text-center">
                      <Link
                        href={`/artisans/${artisan.id}`}
                        className="text-gold hover:underline"
                      >
                        Voir profil
                      </Link>
                      <button
                        onClick={() => handleDeleteArtisan(artisan.id)}
                        className="text-red-500 text-xs hover:underline border-none bg-inherit font-cabin"
                      >
                        Supprimer
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* ====================================================================== */}
      {/* SECTION: GESTION DES CLIENTS */}
      {/* ====================================================================== */}
      <div className="mb-4 w-[90%] mx-auto">
        <h2
          className="text-blue text-lg text-center mb-4 cursor-pointer select-none flex items-center justify-center gap-2"
          onClick={toggleOpenClient}
        >
          Gestion des clients
          <span
            style={{
              display: "inline-block",
              transform: isOpenClient ? "rotate(90deg)" : "rotate(0deg)",
              transition: "transform 0.3s",
              transformOrigin: "center",
            }}
          >
            <svg
              className="w-4 h-4"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </span>
        </h2>

        {isOpenClient && (
          <div className="overflow-x-auto transition-all duration-300">
            {/* Formulaire de création d'administrateur */}
            <h3 className="text-gold text-base mb-4 text-center">
              Création d'un administrateur
            </h3>
            <form
              onSubmit={handleAddAdmin}
              className="flex flex-col md:flex-row items-center justify-center"
            >
              <div className="flex flex-col md:flex-row items-center justify-center gap-4 md:w-full w-3/4">
                <input
                  type="text"
                  name="firstName"
                  placeholder="Prénom"
                  className="input text-xs self-center"
                  required
                />
                <input
                  type="text"
                  name="lastName"
                  placeholder="Nom"
                  className="input text-xs self-center"
                  required
                />
                <input
                  type="email"
                  name="email"
                  placeholder="Email"
                  className="input text-xs self-center"
                  required
                />
                <input
                  type="password"
                  name="password"
                  placeholder="Mot de passe"
                  className="input text-xs self-center"
                  required
                />
                <input
                  type="text"
                  name="address"
                  placeholder="Addresse"
                  className="input text-xs self-center"
                  required
                />
                <button
                  type="submit"
                  className="w-full h-10 rounded-full bg-blue border-2 border-solid border-gold text-gold text-base font-normal font-cabin flex items-center justify-center hover:cursor-pointer"
                >
                  Créer un administrateur
                </button>
              </div>
            </form>
            
            {/* Tableau des clients */}
            <h3 className="text-gold text-base mb-4 text-center">
              Liste des clients
            </h3>
            <table className="w-[90%] max-w-4xl mx-auto">
              <thead>
                <tr className="text-blue text-sm">
                  <th className="p-4">Prénom</th>
                  <th className="p-4">Nom</th>
                  <th className="p-4">Email</th>
                  <th className="p-4">Role</th>
                  <th className="p-4">Actif</th>
                  <th className="p-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {clients.map((client) => (
                  <tr key={client.id} className="text-blue text-xs border-t border-silver">
                    <td className="p-4 text-center">{client.firstName}</td>
                    <td className="p-4 text-center">{client.lastName}</td>
                    <td className="p-4 text-center">{client.email}</td>
                    <td className="p-4 text-center">{client.role}</td>
                    <td className="p-4 text-center">{client.active ? "Actif" : "Inactif"}</td>
                    <td className="p-4 text-center">
                      <Link
                        href={`/clients/${client.id}`}
                        className="text-gold hover:underline"
                      >
                        Voir profil
                      </Link>
                      <button
                        onClick={() => handleDeleteClient(client.id)}
                        className="text-red-500 text-xs hover:underline border-none bg-inherit font-cabin cursor-pointer"
                      >
                        Supprimer
                      </button>
                      <button
                        onClick={() => handleBanClient(client.id)}
                        className="text-blue text-xs hover:underline border-none bg-inherit font-cabin cursor-pointer"
                      >
                        Ban / Unban
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* ====================================================================== */}
      {/* SECTION: GESTION DES DEMANDES CLIENT */}
      {/* ====================================================================== */}
      <div className="mb-4 w-[90%] mx-auto">
        <h2
          className="text-blue text-lg text-center mb-4 cursor-pointer select-none flex items-center justify-center gap-2"
          onClick={toggleOpenAsking}
        >
          Gestion des demandes client
          <span
            style={{
              display: "inline-block",
              transform: isOpenAsking ? "rotate(90deg)" : "rotate(0deg)",
              transition: "transform 0.3s",
              transformOrigin: "center",
            }}
          >
            <svg
              className="w-4 h-4"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </span>
        </h2>

        {/* Tableau des demandes client */}
        {isOpenAsking && (
          <div className="overflow-x-auto transition-all duration-300">
            <table className="w-[90%] max-w-4xl mx-auto">
              <thead>
                <tr className="text-blue text-sm">
                  <th className="p-4">Titre</th>
                  <th className="p-4">Contenu</th>
                  <th className="p-4">Emetteur</th>
                  <th className="p-4">Artisans</th>
                  <th className="p-4">Evènement</th>
                  <th className="p-4">Actions</th>
                </tr>
              </thead>
              <tbody>
                {askings.map((asking) => (
                  <tr key={asking.id} className="text-blue text-xs border-t border-silver">
                    <td className="p-4 text-center">{asking.title}</td>
                    <td className="p-4 text-center">{asking.content}</td>
                    <td className="p-4 text-center">{getClientName(asking.clientId)}</td>
                    <td className="p-4 text-center">{getArtisanCategoryName(asking.artisanCategoryId)}</td>
                    <td className="p-4 text-center">
                      {getEventCategoryName(asking.eventCategoryId)}<br/>
                      {asking.eventDate
                        ? new Date(asking.eventDate).toLocaleDateString("fr-FR")
                        : ""}<br/>
                      {asking.eventLocalisation}
                    </td>
                    <td className="p-4 text-center">
                      <button
                        onClick={() => handleDeleteAsking(asking.id)}
                        className="text-red-500 text-xs hover:underline border-none bg-inherit font-cabin"
                      >
                        Supprimer
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  );
}