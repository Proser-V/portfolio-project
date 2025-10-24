export async function markMessageAsRead(messageId, jwtToken) {
    try {
        const response = await fetch(
            `${process.env.NEXT_PUBLIC_API_URL}/api/messages/${messageId}/read`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    ...(jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}),
                },
                credentials: "include",
            }
        );

        return response.ok;
    } catch (error) {
        console.error("Erreur lors du marquage du message comme lu:", error);
        return false;
    }
}

export async function getUnreadMessages(jwtToken) {
    try {
        const response = await fetch(
            `${process.env.NEXT_PUBLIC_API_URL}/api/messages/unread`,
            {
                headers: {
                    ...(jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}), // Use Authorization header
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

export function getTotalUnreadCount(unreadMessages, currentUserId) {
    return unreadMessages.filter(msg => msg.receiverId === currentUserId).length;
}

export function countUnreadByUser(unreadMessages, currentUserId) {
    const counts = {};

    unreadMessages.forEach((msg) => {
        if (msg.receiverId === currentUserId && msg.isRead === false) {
            const senderId = msg.senderId;
            counts[senderId] = (counts[senderId] || 0) + 1;
        }
  });

    return counts;
}

export async function markConversationAsRead(messages, currentUserId, jwtToken) {
    const unreadMessages = messages.filter(
        (msg) => msg.receiverId === currentUserId && !msg.isRead
    );

    const promises = unreadMessages.map((msg) =>
        markMessageAsRead(msg.id, jwtToken)
    );

    await Promise.all(promises);
}