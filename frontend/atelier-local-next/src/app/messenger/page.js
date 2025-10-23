import { cookies } from "next/headers";
import { getUser } from "@/lib/getUser";
import MessengerList from "@/components/MessengerList";

export const dynamic = "force-dynamic";

export default async function MessengerPage() {
  const cookieStore = await cookies();
  const jwt = cookieStore.get("jwt")?.value;
  console.log("JWT utilisé:", jwt);

  const user = await getUser();
  console.log("Utilisateur récupéré:", user);

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
      console.log("Statut de la réponse API:", convRes.status);
      const data = await convRes.json();
      console.log("Données reçues de l'API:", data);

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

  return (
      <div>
          <main className="max-w-4xl mx-auto px-4">
              <h1 className="text-center text-blue text-2xl font-cabin font-normal mb-0">
                  Vos messages
              </h1>
              <MessengerList 
                  initialConversations={conversations} 
                  conversationsPerPage={10}
                  jwtToken={jwt}
                  currentUserId={user.id}
              />
          </main>
      </div>
  );
}