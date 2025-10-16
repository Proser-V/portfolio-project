import { cookies } from "next/headers";
import Link from "next/link";
import { getUser } from "@/lib/getUser";

export const dynamic = "force-dynamic";

export default async function MessengerPage({ searchParams }) {
  const cookieStore = await cookies();
  const jwt = cookieStore.get("jwt")?.value;

  // Attendre searchParams pour résoudre les paramètres dynamiques
  const resolvedSearchParams = await searchParams;
  const search = (resolvedSearchParams?.search || "").toLowerCase();
  const sort = resolvedSearchParams?.sort === "asc" ? "asc" : "desc";

  // Récupérer l'utilisateur avec la même fonction que le layout
  // Next.js va déduplicater automatiquement cet appel
  const user = await getUser();

  // Vérification de l'utilisateur
  if (!user || !user.id) {
    return (
      <div className="mt-20 text-center text-red-500">
        Session expirée - <a href="/login" className="underline text-blue-600">Veuillez vous reconnecter</a>.
      </div>
    );
  }

  // Fetching des conversations
  const convRes = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/api/messages/conversations/${user.id}`,
    {
      headers: jwt ? { Cookie: `jwt=${jwt}` } : {},
      credentials: "include",
      cache: "no-store",
    }
  );

  // Gestion des conversations vides ou erreurs
  let conversations = [];
  
  if (convRes.ok) {
    const data = await convRes.json();
    conversations = Array.isArray(data) ? data : [];
  } else if (convRes.status !== 404) {
    // Seulement afficher une erreur si ce n'est pas un 404
    return (
      <div className="mt-20 text-center text-red-500">
        Erreur de chargement des conversations.
      </div>
    );
  }

  // Filtrage et tri
  if (conversations.length > 0) {
    conversations = conversations
      .filter((c) => c.otherUserName.toLowerCase().includes(search))
      .sort((a, b) => {
        const da = new Date(a.lastTimestamp);
        const db = new Date(b.lastTimestamp);
        return sort === "asc" ? da - db : db - da;
      });
  }

  // Ajustement du nom d'utilisateur en fonction du rôle
  let displayName;
  if (user.role === "artisan") {
    displayName = user.name;
  } else if (user.firstName && user.lastName) {
    displayName = `${user.firstName} ${user.lastName}`;
  } else {
    displayName = user.email || "Utilisateur";
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-4xl mx-auto mt-8 p-4">
        <h1 className="text-center text-blue-900 text-3xl font-semibold mb-6">
          Vos messages
        </h1>

        <form method="GET" className="flex flex-col md:flex-row gap-4 mb-8">
          <div className="flex-1">
            <input
              type="text"
              name="search"
              defaultValue={search}
              placeholder="Rechercher par nom..."
              className="w-full rounded-full border border-gray-300 px-4 py-2 text-sm outline-none focus:border-blue-500"
            />
          </div>
          <div className="flex items-center gap-2">
            <span className="text-gray-600">Trier par:</span>
            <select
              name="sort"
              defaultValue={sort}
              className="rounded-full border border-gray-300 px-3 py-2 text-sm focus:border-blue-500"
            >
              <option value="desc">Plus récent</option>
              <option value="asc">Plus ancien</option>
            </select>
            <button
              type="submit"
              className="bg-blue-900 text-yellow-400 px-6 py-2 rounded-full border border-yellow-400 hover:bg-blue-800 transition"
            >
              Filtrer
            </button>
          </div>
        </form>

        <div className="bg-white border border-gray-200 rounded-xl shadow-lg p-4">
          {conversations.length === 0 ? (
            <p className="text-center text-gray-400 text-sm py-4">
              Aucune conversation trouvée.
            </p>
          ) : (
            conversations.map((conv) => (
              <Link
                key={conv.otherUserId}
                href={`/messenger/${conv.otherUserId}`}
                className="block border-b border-gray-200 last:border-none py-4 hover:bg-gray-50 transition"
              >
                <div className="flex items-center gap-4">
                  <img
                    src={`/avatars/${conv.otherUserId}.jpg`}
                    alt={`${conv.otherUserName}`}
                    className="w-12 h-12 rounded-full object-cover"
                  />
                  <div className="flex-1">
                    <p className="font-medium text-blue-900">{conv.otherUserName}</p>
                    <p className="text-sm text-gray-600 truncate max-w-[250px]">
                      {conv.lastMessage ? (
                        [".pdf", ".jpg", ".jpeg", ".png"].some(ext => conv.lastMessage.toLowerCase().includes(ext))
                          ? "Pièce jointe : " + conv.lastMessage
                          : conv.lastMessage
                      ) : (
                        "(Aucun message)"
                      )}
                    </p>
                  </div>

                  <div className="text-xs text-gray-500">
                    {conv.lastTimestamp
                      ? new Date(conv.lastTimestamp).toLocaleString("fr-FR", {
                          day: "2-digit",
                          month: "2-digit",
                          year: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        })
                      : ""}
                  </div>
                </div>
              </Link>
            ))
          )}
        </div>

        <div className="mt-4 text-center">
          <button className="bg-blue-900 text-yellow-400 px-6 py-2 rounded-full border border-yellow-400 hover:bg-blue-800 transition">
            Page suivante
          </button>
        </div>
      </main>
    </div>
  );
}