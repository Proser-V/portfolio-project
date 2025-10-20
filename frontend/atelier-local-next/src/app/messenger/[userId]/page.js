import { cookies } from "next/headers";
import { getUser } from "@/lib/getUser";
import ConversationClient from "@/components/ConversationClient";

export const dynamic = "force-dynamic";

export default async function ConversationPage({ params }) {
  const cookieStore = await cookies();
  const jwt = cookieStore.get("jwt")?.value;

  // Récupérer l'utilisateur connecté
  const user = await getUser();

  if (!user || !user.id) {
    return (
      <div className="mt-20 text-center text-red-500">
        Session expirée -{" "}
        <a href="/login" className="underline text-blue-600">
          Veuillez vous reconnecter
        </a>
        .
      </div>
    );
  }

  const { userId } = await params;
  const otherUserId = userId;

  // Récupérer les messages
  const messagesRes = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/api/messages/history?user1Id=${user.id}&user2Id=${otherUserId}`,
    {
      headers: jwt ? { Cookie: `jwt=${jwt}` } : {},
      credentials: "include",
      cache: "no-store",
    }
  );

  let messages = [];
  let otherUserName = "Utilisateur inconnu";
  let otherUser = null;

  // Déterminer le rôle et le nom de l'autre utilisateur
  try {
    let role = null;

    const artisanRes = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/artisans/${otherUserId}`,
      {
        headers: jwt ? { Cookie: `jwt=${jwt}` } : {},
        credentials: "include",
        cache: "no-store",
      }
    );

    if (artisanRes.ok) {
      otherUser = await artisanRes.json();
      role = "artisan";
    } else {
      const clientRes = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/api/clients/${otherUserId}`,
        {
          headers: jwt ? { Cookie: `jwt=${jwt}` } : {},
          credentials: "include",
          cache: "no-store",
        }
      );

      if (clientRes.ok) {
        otherUser = await clientRes.json();
        role = "client";
      }
    }

    if (otherUser && role) {
      if (role === "artisan") {
        otherUserName = otherUser.name || "Artisan inconnu";
      } else if (role === "client") {
        otherUserName = `${otherUser.firstName || ""} ${otherUser.lastName || ""}`.trim() || "Client inconnu";
      }
    } else {
      console.warn(`Impossible de récupérer les infos de l'utilisateur avec l'ID ${otherUserId}`);
    }
  } catch (e) {
    console.error("Erreur lors de la récupération de l'autre utilisateur :", e);
  }

  // Traiter la réponse des messages
  if (messagesRes.ok) {
    const data = await messagesRes.json();
    messages = Array.isArray(data) ? data : [];
  } else {
    return (
      <div className="mt-20 text-center text-red-500">
        Erreur de chargement de la conversation.
      </div>
    );
  }

  return (
    <div>
      <main className="w-[90vw] md:w-[60vw] mx-auto p-4">
        <h1 className="text-center text-blue text-xl font-semibold mb-6">
          Votre fil de discussion avec {otherUserName}
        </h1>
        <ConversationClient
          initialMessages={messages}
          user={user}
          otherUser={otherUser}
          otherUserName={otherUserName}
          jwtToken={jwt}
        />
      </main>
    </div>
  );
}

// Métadonnées pour le SEO
export const metadata = {
  title: "Fil de discussion - Atelier Local",
  description: "Votre fil de discussion dans l'Atelier Local.",
};
