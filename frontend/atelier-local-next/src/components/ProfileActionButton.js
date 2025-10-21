"use client";
import { useState } from "react";
import Link from "next/link";
import EditProfileModal from "./EditProfileModal";

export default function ProfileActionButton({ artisan, isOwner, address, isAdmin }) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const canEdit = isAdmin || isOwner;

  // Si c'est le propriétaire : bouton "Modifier mon profil"
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

        <EditProfileModal
          artisan={artisan}
          address={address}
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          onSuccess={() => {
            // Callback optionnel après succès
          }}
        />
      </>
    );
  }

  // Sinon : bouton "Contactez-moi"
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
