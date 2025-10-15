import { cookies } from "next/headers";
import { getUser } from "@/lib/getUser";
import MessageForm from "@/components/MessageForm";

export const dynamic = "force-dynamic";

export default async function ConversationPage({ params }) {
    const cookieStore = await cookies();
    const jwt = cookieStore.get("jwt")?.value;

    const user = await getUser();

    if (!user || !user.id) {
        return (
            <div className="mt-20 text-center text-red-500">
                Session expirée - <a href="/log" className="underline text-blue-600">Veuillez vous reconnecter</a>.
            </div>
        );
    }

    const { userId } = await params;
    const otherUserId = userId;

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
                  const isSentByUser = msg.senderId === user.id.toString(); // Ajuste selon le format de senderId
                    return (
                    <div key={msg.id} className={`flex ${isSentByUser ? "justify-end" : "justify-start"} mb-4`}>
                        {!isSentByUser && (
                        <img
                            src={`/avatars/${otherUserId}.jpg`}
                            alt={otherUserName}
                            className="w-10 h-10 rounded-full mr-2"
                        />
                        )}
                        <div
                        className={`max-w-[70%] p-3 rounded-lg ${
                            isSentByUser ? "bg-blue-900 text-white" : "bg-yellow-100 text-gray-800"
                        }`}
                        >
                        <p className="text-sm">
                            {msg.attachment && typeof msg.attachment === "string" ? (
                                <a href={msg.attachment} className="text-blue-500 underline">
                                    Pièce jointe: {msg.attachment.split("/").pop() || "Fichier inconnu"}
                                </a>
                            ) : (
                                msg.content
                            )}
                        </p>
                        <p className="text-xs text-gray-500 mt-1">
                            {new Date(msg.timestamp).toLocaleString("fr-FR", {
                                day: "2-digit",
                                month: "2-digit",
                                year: "numeric",
                                hour: "2-digit",
                                minute: "2-digit",
                            })}
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

            <MessageForm userId={user.id} otherUserId={otherUserId} />
            </main>
        </div>
    );
}