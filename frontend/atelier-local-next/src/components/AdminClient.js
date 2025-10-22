"use client"
import { useState } from "react";
import Link from "next/link";
import { useEffect } from "react";
import fetchCoordinates from "utils/fetchCoordinates";

export default function AdminClient({ initialArtisans, initialArtisanCategories, initialEventCategories }) {
  const [artisans, setArtisans] = useState(initialArtisans);
  const [clients, setClients] = useState([]);
  const [askings, setAskings] = useState([]);
  const [artisanCategories, setArtisanCategories] = useState(initialArtisanCategories);
  const [eventCategories, setEventCategories] = useState(initialEventCategories);
  const [selectedId, setSelectedId] = useState("");
  const [selectedArtisanCategories, setSelectedArtisanCategories] = useState([]);
  const [error, setError] = useState("");
  const [isOpenArtisan, setIsOpenArtisan] = useState(false);
  const [isOpenClient, setIsOpenClient] = useState(false);
  const [isOpenArtisanCategories, setIsOpenArtisanCategories] = useState(false);
  const [isOpenEventCategories, setIsOpenEventCategories] = useState(false);
  const [isOpenAsking, setIsOpenAsking] = useState(false);
  
  const toggleOpenArtisan = () => setIsOpenArtisan(!isOpenArtisan);
  const toggleOpenClient = () => setIsOpenClient(!isOpenClient);
  const toggleOpenArtisanCategories = () => setIsOpenArtisanCategories(!isOpenArtisanCategories);
  const toggleOpenEventCategories = () => setIsOpenEventCategories(!isOpenEventCategories);
  const toggleOpenAsking = () => setIsOpenAsking(!isOpenAsking);

  useEffect(() => {
    const fetchClients = async () => {
      try {
        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/clients/`, {
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

  useEffect(() => {
    const fetchAskings = async () => {
      try {
        const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/askings/`, {
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

  function getClientName(clientId) {
    const c = clients.find(cl => cl.id === clientId);
    return c ? `${c.firstName} ${c.lastName}` : "—";
  };

  function getArtisanCategoryName(categoryId) {
    const cat = artisanCategories.find(c => c.id === categoryId);
    return cat ? cat.name : "—";
  };

  function getEventCategoryName(categoryId) {
    const cat = eventCategories.find(c => c.id === categoryId);
    return cat ? cat.name : "—";
  };

  const handleAddAdmin = async (e) => {
    e.preventDefault();
    setError("");

    try {
      const formData = new FormData(e.target);

      const address = formData.get("address");
      const coords = await fetchCoordinates(address);

      const clientData = {
        firstName: formData.get("firstName"),
        lastName: formData.get("lastName"),
        email: formData.get("email"),
        password: formData.get("password"),
        latitude: coords.latitude,
        longitude: coords.longitude,
        role: "ADMIN"
      };

      const multipartFormData = new FormData();
      multipartFormData.append(
        "client",
        new Blob([JSON.stringify(clientData)], { type: "application/json" })
      );

      const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/clients/register`, {
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

  const handleBanClient = async (id) => {
    const confirmAction = window.confirm("Voulez-vous vraiment modifier le statut de ce client ?");
    if (!confirmAction) return;

    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/clients/${id}/moderate`,{
        method: "PATCH",
        credentials: "include"
      });
      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "Erreur lors de la création de l'administrateur.");
      }
      const patchedClient = await response.json();
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

  const handleDeleteClient = async (id) => {
    if (!confirm("Voulez-vous vraiment supprimer ce client ?")) return;

    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/clients/${id}/delete`, {
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

  const handleAddArtisanCategory = async (e) => {
      e.preventDefault();
      const name = e.target.categoryName.value;
      const description = e.target.description.value;

      try {
          const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/artisan-category/creation`, {
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


  const handleDeleteArtisanCategory = async (id) => {
      if (!confirm("Voulez-vous vraiment supprimer cet catégorie ?")) return;

      try {
          const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/artisan-category/${id}/delete`, {
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

  const handleAddEventCategory = async (e) => {
      e.preventDefault();
      const name = e.target.name.value;
      const artisanCategoryIds = selectedArtisanCategories.map((cat) => cat.id);

      try {
          const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/event-categories/creation`, {
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

  const handleDeleteEventCategory = async (id) => {
      if (!confirm("Voulez-vous vraiment supprimer cet catégorie ?")) return;

      try {
          const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/event-categories/${id}/delete`, {
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

  return (
    <>
      {error && (
        <div className="h-5 flex justify-center items-center mb-6">
          <p className="text-red-500 text-sm text-center">{error}</p>
        </div>
      )}
      {/* Section catégorie d'artisan */}
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
                  required
                />
                <textarea
                  className="w-3/4 h-10 rounded-lg bg-white shadow-[0px_4px_4px_rgba(0,0,0,0.25)] font-cabin
                    border-2 border-solid border-silver px-4 py-2 text-xs text-blue outline-none resize-none"
                  placeholder="Courte description de la catégorie..."
                  name="description"
                ></textarea>
                <button
                  type="submit"
                  className="w-full h-10 rounded-full bg-blue border-2 border-solid border-gold text-gold text-base font-normal font-cabin flex items-center justify-center hover:cursor-pointer"
                >
                  Ajouter une catégorie
                </button>
              </div>
            </form>
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

      {/* Section catégorie d'évènement */}
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
            <h3 className="text-gold text-base mb-4 text-center">
              Ajouter une catégorie
            </h3>

            <form
              onSubmit={handleAddEventCategory}
              className="flex flex-col items-center justify-center"
            >
              <div className="flex flex-col md:flex-row items-center justify-center gap-4 md:w-full w-3/4">

                {/* Nom de la catégorie d’événement */}
                <input
                  type="text"
                  name="name"
                  placeholder="Nom de l'évènement..."
                  className="md:w-1/2 w-3/4 h-8 rounded-full bg-white shadow-[0px_4px_4px_rgba(0,0,0,0.25)] font-cabin
                              border-2 border-solid border-silver px-4 text-xs text-blue outline-none"
                  required
                />

                {/* Sélecteur de catégories d’artisans */}
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

                    // Réinitialise le select
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

              {/* Liste des catégories artisan sélectionnées */}
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

              {/* Bouton d’ajout */}
              <button
                type="submit"
                className="w-1/2 h-10 rounded-full bg-blue border-2 border-solid border-gold text-gold text-base font-normal font-cabin flex items-center justify-center hover:cursor-pointer mt-4"
              >
                Ajouter une catégorie
              </button>
            </form>

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

      {/* Section Artisans */}
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

      {/* Section Clients */}
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

      {/* Section Askings */}
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