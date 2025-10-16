import { cookies } from "next/headers";
import { getUser } from "@/lib/getUser";
import MessageForm from "@/components/MessageForm";

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

  // Déterminer le rôle et le nom de l'autre utilisateur
  try {
    let otherUser = null;
    let role = null;

    // Essayer de récupérer en tant qu'artisan
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
      // Essayer de récupérer en tant que client
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
      console.warn(`Impossible de récupérer les infos de l’utilisateur avec l’ID ${otherUserId}`);
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

  // Trier les messages par timestamp
  messages.sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());

  return (
    <div className="min-h-screen bg-gray-50">
      <main className="max-w-4xl mx-auto mt-8 p-4">
        <h1 className="text-center text-blue-900 text-3xl font-semibold mb-6">
          Votre fil de discussion avec {otherUserName}
        </h1>

        <div className="bg-white border border-gray-200 rounded-xl shadow-lg p-4 mb-4 h-[60vh] overflow-y-auto">
          {messages.length === 0 ? (
            <p className="text-center text-gray-400 text-sm py-4">
              Aucun message dans cette conversation.
            </p>
          ) : (
            messages.map((msg) => {
              const isSentByUser = msg.senderId.toString() === user.id.toString();

              // Formatter la date
              let messageDate = "Date inconnue";
              if (msg.timestamp) {
                const parsedDate = new Date(msg.timestamp.replace(" ", "T"));
                if (!isNaN(parsedDate.getTime())) {
                  messageDate = parsedDate.toLocaleString("fr-FR", {
                    day: "2-digit",
                    month: "2-digit",
                    year: "numeric",
                    hour: "2-digit",
                    minute: "2-digit",
                  });
                }
              }

              return (
                <div
                  key={msg.id}
                  className={`flex ${isSentByUser ? "justify-end" : "justify-start"} mb-4`}
                >
                  {!isSentByUser && (
                    <img
                      src={`/avatars/${otherUserId}.jpg`}
                      alt={otherUserName}
                      className="w-10 h-10 rounded-full mr-2"
                    />
                  )}
                  <div
                    className={`max-w-[70%] p-3 rounded-lg ${
                      isSentByUser ? "bg-blue text-white" : "bg-yellow-100 text-gray-800"
                    }`}
                  >
                    <p className="text-sm">
                      {msg.attachments && msg.attachments.length > 0 ? (
                        msg.attachments.map((attachment, index) => (
                          <div key={index} className="mb-1">
                            <a
                              href={attachment.fileUrl}
                              className="text-blue-500 underline"
                              target="_blank"
                              rel="noopener noreferrer"
                            >
                              Pièce jointe: {attachment.fileUrl.split("/").pop() || "Fichier inconnu"}
                            </a>
                          </div>
                        ))
                      ) : (
                        msg.content
                      )}
                    </p>
                    <p className={`text-xs mt-1 ${isSentByUser ? "text-gray-300" : "text-gray-500"}`}>
                      {messageDate}
                    </p>
                  </div>
                  {isSentByUser && (
                    <img
                      src={`/avatars/${user.id}.jpg`}
                      alt="Vous"
                      className="w-10 h-10 rounded-full ml-2"
                    />
                  )}
                </div>
              );
            })
          )}
        </div>

        <MessageForm userId={user.id} otherUserId={otherUserId} jwtToken={jwt} />
      </main>
    </div>
  );
}
