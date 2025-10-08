"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import Link from "next/link";

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const router = useRouter();

    const handleSubmit = async (err) => {
        err.preventDefault();

        try {
            const response = await fetch("http://localhost:8080/api/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password }),
                credentials: "include",
            });

            if (!response.ok) {
                setError("Identifiants invalides");
                return;
            }

            setError("");
            console.log("Login success");
            router.push("/");
        } catch (err) {
            console.error("Erreur réseau :", err);
            setError("Serveur inaccessible. Vérifiez votre connexion.");
        }
    };

    return (
    <div className="mt-20 items-center justify-center">
        <div className="text-center text-blue text-xl">
            Connexion
        </div>

        <div className="h-5 flex justify-center items-center mb-6">
        {error && <p className="text-red-500 text-sm text-center -mb-4">{error}</p>}
        </div>

        <form onSubmit={handleSubmit}>
            <div className="md:w-[300px] h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)] border-2 border-solid border-silver flex items-center pl-4 pr-40 mb-6">
                <input
                    type="email"
                    placeholder="Adresse email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full border-none outline-none text-silver text-xs"
                    required
                />
            </div>

            <div className="h-10 rounded-[42.5px] bg-white shadow-[0px_4px_4px_0px_rgba(0,0,0,0.25)] border-2 border-solid border-silver flex items-center px-4 mb-6">
                <input
                    type="password"
                    placeholder="Mot de passe"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full border-none outline-none text-silver text-xs"
                    required
                />
            </div>

            <button
                type="submit"
                className="w-1/2 h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                            text-gold text-base font-normal font-cabin
                            flex items-center justify-center mx-auto hover:cursor-pointer 
                            hover:bg-blue transition mb-5 mt-8"
                >
                Connexion
            </button>
        </form>

        <div className="text-center text-blue text-lg mt-12">
            Vous n'avez pas encore de compte ?
        </div>
        <Link
            href="/registration"
            className="w-1/2 max-w-xs h-10 rounded-[42.5px] bg-blue border-2 border-solid border-gold 
                        text-gold text-base font-normal font-cabin
                        flex items-center justify-center mx-auto 
                        hover:bg-blue transition mt-4"
            >
            Créez un compte
        </Link>
    </div>
  );
}
