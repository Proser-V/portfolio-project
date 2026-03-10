import Link from "next/link";

/**
 * Page de politique de confidentialité de L'Atelier Local.
 * Conforme au RGPD.
 *
 * @component
 * @returns {JSX.Element}
 */
export default function PrivacyPage() {
  return (
    <div className="max-w-3xl mx-auto px-6 py-12 font-cabin text-gray-800">
      <h1 className="text-3xl font-bold mb-2 text-blue">Politique de confidentialité</h1>
      <p className="text-sm text-silver mb-8">Dernière mise à jour : mars 2026</p>

      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-3 text-blue">1. Présentation</h2>
        <p>
          L'Atelier Local est une plateforme web de mise en relation entre particuliers et artisans locaux.
          Le responsable du traitement des données est le projet L'Atelier Local, développé dans le cadre
          d'une formation à Holberton School Dijon.
        </p>
      </section>

      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-3 text-blue">2. Données collectées</h2>
        <p className="mb-2">Dans le cadre de la création d'un compte et de la mise en relation, nous collectons :</p>
        <ul className="list-disc pl-6 space-y-1">
          <li>Nom et prénom</li>
          <li>Adresse e-mail</li>
          <li>Numéro de téléphone</li>
          <li>Adresse postale</li>
          <li>Numéro SIRET (pour les artisans)</li>
          <li>Photos et médias (ex : photos de réalisations pour les artisans)</li>
        </ul>
      </section>

      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-3 text-blue">3. Finalités du traitement</h2>
        <p className="mb-2">Vos données sont utilisées pour :</p>
        <ul className="list-disc pl-6 space-y-1">
          <li>Créer et gérer votre compte utilisateur</li>
          <li>Faciliter la mise en relation entre particuliers et artisans</li>
          <li>Permettre les échanges via la messagerie intégrée</li>
          <li>Assurer le bon fonctionnement de la plateforme</li>
        </ul>
      </section>

      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-3 text-blue">4. Base légale</h2>
        <p>
          Le traitement de vos données repose sur votre <strong>consentement explicite</strong>, recueilli
          lors de la création de votre compte via la case à cocher dédiée.
        </p>
      </section>

      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-3 text-blue">5. Cookies</h2>
        <p>
          Nous utilisons uniquement des <strong>cookies de session</strong>, nécessaires au fonctionnement
          de la plateforme (maintien de votre connexion). Ces cookies ne sont pas utilisés à des fins
          publicitaires ou de tracking. Ils sont automatiquement supprimés à la fermeture de votre navigateur.
        </p>
      </section>

      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-3 text-blue">6. Conservation des données</h2>
        <p>
          Vos données sont conservées pendant la durée de vie de votre compte. En cas de suppression
          de votre compte, vos données personnelles sont effacées dans un délai raisonnable.
        </p>
      </section>

      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-3 text-blue">7. Vos droits (RGPD)</h2>
        <p className="mb-2">Conformément au RGPD, vous disposez des droits suivants :</p>
        <ul className="list-disc pl-6 space-y-1">
          <li><strong>Droit d'accès</strong> : consulter vos données personnelles</li>
          <li><strong>Droit de rectification</strong> : corriger des données inexactes</li>
          <li><strong>Droit à l'effacement</strong> : demander la suppression de vos données</li>
          <li><strong>Droit d'opposition</strong> : vous opposer à un traitement</li>
          <li><strong>Droit à la portabilité</strong> : récupérer vos données dans un format structuré</li>
        </ul>
        <p className="mt-3">
          Pour exercer ces droits, contactez-nous à :{" "}
          <a href="mailto:contact@latelierlocal.fr" className="text-blue underline">
            contact@latelierlocal.fr
          </a>
        </p>
      </section>

      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-3 text-blue">8. Sécurité</h2>
        <p>
          Nous mettons en œuvre les mesures techniques appropriées pour protéger vos données contre
          tout accès non autorisé, modification ou divulgation.
        </p>
      </section>

      <section className="mb-8">
        <h2 className="text-xl font-semibold mb-3 text-blue">9. Contact & réclamation</h2>
        <p>
          Pour toute question relative à cette politique, vous pouvez nous contacter par e-mail.
          Vous avez également le droit d'introduire une réclamation auprès de la{" "}
          <a href="https://www.cnil.fr" className="text-blue underline" target="_blank" rel="noopener noreferrer">
            CNIL
          </a>.
        </p>
      </section>

      <div className="border-t pt-6 mt-8">
        <Link href="/" className="text-sm text-blue underline">
          ← Retour à l'accueil
        </Link>
      </div>
    </div>
  );
}