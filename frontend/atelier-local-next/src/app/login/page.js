"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { useEffect } from "react";
import Link from "next/link";
import getApiUrl from "@/lib/api";

/**
 * Page de connexion utilisateur.
 * 
 * Cette page permet à un utilisateur de se connecter à son compte en envoyant
 * ses identifiants (email et mot de passe) à l'API backend.
 * En cas de succès, l'utilisateur est redirigé vers la page d'accueil.
 *
 * @component
 * @param {Object} props - Les propriétés du composant.
 * @param {Object} [props.user] - L'utilisateur actuellement connecté (si déjà authentifié).
 * @returns {JSX.Element} Composant React représentant la page de connexion.
 */
export default function LoginPage({ user }) {
    // --- États locaux pour gérer les champs du formulaire ---
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
  
    // --- Hook Next.js pour gérer la navigation ---
    const router = useRouter();

    // --- Effet secondaire pour définir le titre de la page ---
    useEffect(() => {
        document.title = "Connexion - Atelier Local";
    }, []);

    /**
     * Gère la soumission du formulaire de connexion.
     *
     * Cette fonction :
     * 1. Empêche le rechargement par défaut du formulaire.
     * 2. Envoie une requête POST vers l'API de connexion.
     * 3. Gère les erreurs éventuelles (identifiants invalides ou serveur indisponible).
     * 4. Redirige l'utilisateur vers la page d'accueil en cas de succès.
     *
     * @async
     * @function
     * @param {Event} err - L'événement de soumission du formulaire.
     * @returns {Promise<void>}
     */
    const handleSubmit = async (err) => {
        err.preventDefault(); // Empêche le rechargement de la page

        try {
            // --- Requête POST envoyée à l'API de connexion ---
            const response = await fetch(`${getApiUrl()}/api/users/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password }),
                credentials: "include", // Inclut les cookies pour la session
            });

            // --- Gestion des erreurs de réponse ---
            if (!response.ok) {
                setError("Identifiants invalides");
                return;
            }

            // --- Réinitialisation des erreurs et redirection ---
            setError("");
            window.location.href = "/"; // Redirection après connexion réussie
        } catch (err) {
            // --- Gestion des erreurs réseau ---
            console.error("Erreur réseau :", err);
            setError("Serveur inaccessible. Vérifiez votre connexion.");
        }
    };

    // --- Rendu JSX du formulaire de connexion ---
    return (
    <div className="mt-20 items-center justify-center">
        {/* Titre principal */}
        <div className="text-center text-blue text-xl">
            Connexion
        </div>

        {/* Message d’erreur éventuel */}
        <div className="h-5 flex justify-center items-center mb-6">
            {error && <p className="text-red-500 text-sm text-center -mb-4">{error}</p>}
        </div>

        {/* Formulaire de connexion */}
        <form onSubmit={handleSubmit}>
            {/* Champ email */}
            <div className="md:w-[300px] h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)] border-2 border-solid border-silver flex items-center pl-4 pr-40 mb-6">
                <input
                    type="email"
                    placeholder="Adresse email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full border-none outline-none text-blue text-xs"
                    required
                />
            </div>

            {/* Champ mot de passe */}
            <div className="h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)] border-2 border-solid border-silver flex items-center px-4 mb-6">
                <input
                    type="password"
                    placeholder="Mot de passe"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full border-none outline-none text-blue text-xs"
                    required
                />
            </div>

            {/* Bouton de soumission */}
            <button
                type="submit"
                className="w-1/2 h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                            text-gold text-base font-normal font-cabin
                            flex items-center justify-center mx-auto hover:cursor-pointer 
                            mb-5 mt-8"
                >
                Connexion
            </button>
        </form>

        {/* Redirection vers la page d’inscription */}
        <div className="text-center text-blue text-lg mt-12">
            Vous n'avez pas encore de compte ?
        </div>
        <Link
            href="/registration"
            className="w-1/2 max-w-xs h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                        text-gold text-base font-normal font-cabin
                        flex items-center justify-center mx-auto mt-4"
            >
            Créez un compte
        </Link>
    </div>
  );
}
