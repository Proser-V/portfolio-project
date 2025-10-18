import { cookies } from "next/headers";
import { getUser } from "@/lib/getUser";
import MessengerList from "@/components/MessengerList"

export const dynamic = "force-dynamic";

export default async function MessengerPage() {
  const cookieStore = cookies();
  const jwt = cookieStore.get("jwt")?.value;

  const user = await getUser();

  if (!user || !user.id) {
    return (
      <div className="mt-20 text-center text-red-500">
        Session expirée - <a href="/login" className="underline text-blue-600">Veuillez vous reconnecter</a>.
      </div>
    );
  }

  // Fetch des conversations côté serveur
  let conversations = [];
  try {
    const convRes = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/messages/conversations/${user.id}`,
      {
        headers: jwt ? { Cookie: `jwt=${jwt}` } : {},
        credentials: "include",
        cache: "no-store",
      }
    );

    if (convRes.ok) {
      const data = await convRes.json();
      conversations = Array.isArray(data) ? data : [];
    }
  } catch (err) {
    return (
      <div className="mt-20 text-center text-red-500">
        Erreur de chargement des conversations.
      </div>
    );
  }

  return (
    <div>
      <main className="max-w-4xl mx-auto px-4">
        <h1 className="text-center text-blue text-2xl font-cabin font-normal mb-0">
          Vos messages
        </h1>

        {/* Composant client pour la liste + recherche dynamique */}
        <MessengerList initialConversations={conversations} conversationsPerPage={10} />
      </main>
    </div>
  );
}
