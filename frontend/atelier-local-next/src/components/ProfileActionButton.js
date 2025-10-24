"use client";
// Directive Next.js : indique que ce composant s’exécute côté client
// (nécessaire car il utilise des hooks React)

import { useState } from "react";
import Link from "next/link";
import EditProfileModal from "./EditProfileModal";

/**
 * Bouton d'action pour le profil d'un artisan.
 *
 * Affiche :
 * - "Modifier mon profil" si l'utilisateur est le propriétaire ou un administrateur.
 * - "Contactez-moi" sinon.
 *
 * Props :
 * @param {Object} artisan - Objet représentant l'artisan (contient au minimum l'id)
 * @param {boolean} isOwner - Indique si l'utilisateur courant est le propriétaire du profil
 * @param {string} address - Adresse de l'artisan (utilisée pour l'édition du profil)
 * @param {boolean} isAdmin - Indique si l'utilisateur courant a un rôle admin
 */
export default function ProfileActionButton({ artisan, isOwner, address, isAdmin }) {
  // État pour gérer l'ouverture/fermeture de la modal d'édition
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Détermine si l'utilisateur peut éditer le profil
  const canEdit = isAdmin || isOwner;

  /**
   * Si l'utilisateur peut éditer le profil :
   * - Affiche un bouton "Modifier mon profil"
   * - Ouvre la modal d'édition au clic
   */
  if (canEdit) {
    return (
      <>
        <button
          onClick={() => setIsModalOpen(true)}
          className="btn-primary
                     relative z-10
                     mx-auto mb-2 md:mb-0
                     md:absolute md:bottom-4 md:right-4 md:mt-0 md:mx-0"
        >
          Modifier mon profil
        </button>

        {/* Modal d'édition du profil */}
        <EditProfileModal
          artisan={artisan}
          address={address}
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          onSuccess={() => {
            // Callback optionnel après succès de l'édition
          }}
        />
      </>
    );
  }

  /**
   * Si l'utilisateur n'a pas les droits d'édition :
   * - Affiche un bouton "Contactez-moi" redirigeant vers le messenger
   */
  return (
    <Link
      href={`/messenger/${artisan.id}`}
      className="btn-primary
                 relative z-10
                 mx-auto mb-2 md:mb-0
                 md:absolute md:bottom-4 md:right-4 md:mt-0 md:mx-0"
    >
      Contactez-moi
    </Link>
  );
}
