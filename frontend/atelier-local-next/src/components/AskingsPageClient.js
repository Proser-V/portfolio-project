"use client";
import { useState } from "react";
import AskingCard from "@/components/AskingCard";

/**
 * AskingsPageClient
 *
 * Page affichant la liste des demandes ("askings") pour un client.
 * La page est filtrée par catégorie et affiche un message si aucune demande n'est disponible.
 *
 * Fonctionnalités :
 * - Affichage du titre avec la catégorie sélectionnée
 * - Affichage conditionnel :
 *   - Message lorsque aucune demande n'est disponible
 *   - Liste de cartes AskingCard lorsque des demandes sont présentes
 * - Limite la largeur de la liste pour une meilleure lisibilité sur grand écran
 *
 * @component
 *
 * @param {Object} props - Les props du composant
 * @param {Array<Object>} props.initialAskings - Liste initiale des demandes à afficher
 * @param {string} props.category - Nom de la catégorie des demandes (affiché dans le titre)
 *
 * @example
 * <AskingsPageClient 
 *    initialAskings={[{id: 1, title: 'Réparer fuite d’eau', ...}]} 
 *    category="Plomberie" 
 * />
 */
export default function AskingsPageClient({ initialAskings, category }) {
  return (
    <div className="mt-6 flex flex-col items-center justify-center px-4 md:px-0">
      
      {/* Titre de la page affichant la catégorie */}
      <h1 className="text-center text-blue text-xl font-normal font-cabin mb-4">
        {category} - Liste des demandes
      </h1>

      {/* Affichage conditionnel selon la présence de demandes */}
      {initialAskings.length === 0 ? (
        <div className="w-full flex items-center justify-center">
          {/* Message lorsqu’aucune demande n’est disponible */}
          <p className="text-center text-blue">
            Aucune demande disponible pour l'instant. <br/>
            Revenez plus tard pour répondre aux habitants.
          </p>
        </div>
      ) : (
        <div className="mt-4 flex flex-col gap-4 w-full max-w-4xl">
          {/* Boucle sur les demandes et rendu des composants AskingCard */}
          {initialAskings.map(asking => (
            <AskingCard
              key={asking.id}       // Clé unique pour le rendu React
              asking={asking}       // Passage de la demande au composant AskingCard
              className="w-full"    // Prend toute la largeur disponible
            />
          ))}
        </div>
      )}
    </div>
  );
}
