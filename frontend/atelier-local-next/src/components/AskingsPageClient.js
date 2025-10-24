"use client";
import { useState } from "react";
import AskingCard from "@/components/AskingCard";

export default function AskingsPageClient({ initialAskings, category }) {
  return (
  <div className="mt-6 flex flex-col items-center justify-center px-4 md:px-0">
      <h1 className="text-center text-blue text-xl font-normal font-cabin mb-4">
        {category} - Liste des demandes
      </h1>

      {initialAskings.length === 0 ? (
      <div className="w-full flex items-center justify-center">
        <p className="text-center text-blue">
        Aucune demande disponible pour l'instant. <br/>
        Revenez plus tard pour répondre aux habitants.
        </p>
      </div>
      ) : (
      <div className="mt-4 flex flex-col gap-4 w-full max-w-4xl">
        {initialAskings.map(asking => (
          <AskingCard
              key={asking.id}
              asking={asking}
              className="w-full"
          />
          ))}
      </div>
      )}
    </div>
  );
}