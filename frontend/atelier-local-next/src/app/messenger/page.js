import { cookies } from "next/headers";
import { getUser } from "@/lib/getUser";
import MessengerList from "@/components/MessengerList";

export const dynamic = "force-dynamic";

async function fetchUnreadMessages(jwtToken) {
    try {
        const response = await fetch(
            `${process.env.NEXT_PUBLIC_API_URL}/api/messages/unread`,
            {
                headers: {
                    ...(jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}),
                },
                credentials: "include",
                cache: "no-store",
            }
        );

        if (response.ok) {
            const data = await response.json();
            return Array.isArray(data) ? data : [];
        }

        if (response.status === 404) {
            return [];
        }

        return [];
    } catch (error) {
        console.error("Erreur lors de la récupération des messages non lus:", error);
        return [];
    }
}

export default async function MessengerPage() {
  const cookieStore = await cookies();
  const jwt = cookieStore.get("jwt")?.value;

  const user = await getUser();

  if (!user || !user.id) {
      return (
          <div className="mt-20 text-center text-red-500">
              Session expirée - <a href="/login" className="underline text-blue-600">Veuillez vous reconnecter</a>.
          </div>
      );
  }

  let conversations = [];
  try {
      const convRes = await fetch(
          `${process.env.NEXT_PUBLIC_API_URL}/api/messages/conversations/${user.id}`,
          {
              headers: {
                  Authorization: jwt ? `Bearer ${jwt}` : "",
              },
              credentials: "include",
              cache: "no-store",
          }
      );
      const data = await convRes.json();

      if (!convRes.ok) {
          return (
              <div className="mt-20 text-center text-red-500">
                  Erreur de chargement des conversations : {convRes.status === 403 ? "Accès non autorisé" : `Erreur ${convRes.status}`}
              </div>
          );
      }

      conversations = Array.isArray(data) ? data : [];
  } catch (err) {
      console.error("Erreur lors de la récupération des conversations:", err);
      return (
          <div className="mt-20 text-center text-red-500">
              Erreur de chargement des conversations : {err.message}
          </div>
      );
  }

  const unreadMessages = await fetchUnreadMessages(jwt);

  return (
      <div>
          <main className="max-w-4xl mx-auto px-4">
              <h1 className="text-center text-blue text-2xl font-cabin font-normal mb-0">
                  Vos messages
              </h1>
              <MessengerList 
                  initialConversations={conversations} 
                  conversationsPerPage={10}
                  currentUserId={user.id}
                  initialUnreadMessages={unreadMessages} // ✅ Maintenant rempli
              />
          </main>
      </div>
  );
}