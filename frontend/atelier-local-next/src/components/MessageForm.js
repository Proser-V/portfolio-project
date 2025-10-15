"use client";

import { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import Cookies from "js-cookie";

export default function MessageForm({ userId, otherUserId }) {
    const [message, setMessage] = useState("");
    const [attachment, setAttachment] = useState(null);
    const [stompClient, setStompClient] = useState(null);

    useEffect(() => {
        const wsUrl = `${process.env.NEXT_PUBLIC_API_URL}/ws`;
        console.log("WebSocket URL:", wsUrl);

        const socket = new SockJS(wsUrl);
        const client = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
            console.log("Connecté à WebSocket");
            setStompClient(client);
            client.subscribe("/user/queue/messages", (message) => {
                console.log("Nouveau message reçu:", message.body);
            });
            },
            onStompError: (frame) => console.error("Erreur STOMP:", frame),
        });

        client.activate();
        return () => client.deactivate();
        }, []);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!message && !attachment) return;

    const messageData = {
        senderId: userId,
        receiverId: otherUserId,
        content: message,
    };

    if (stompClient && stompClient.connected) {
        stompClient.publish({
            destination: "/app/chat",
            body: JSON.stringify(messageData),
        });
        setMessage("");
        setAttachment(null);
    } else {
        alert("Connexion WebSocket non établie");
    }
    };

    return (
    <form onSubmit={handleSubmit} className="flex items-center gap-2">
        <input
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Entrez votre message ici..."
            className="flex-1 rounded-full border border-gray-300 px-4 py-2 text-sm outline-none focus:border-blue-500"
            required
        />
        <label className="cursor-pointer">
            <input
                type="file"
                accept=".pdf"
                className="hidden"
                onChange={(e) => setAttachment(e.target.files?.[0] || null)}
            />
        <span className="text-blue-900 text-xl">📎</span>
        </label>
        <button
            type="submit"
            className="text-blue-900 text-xl bg-gray-100 rounded-full p-2 hover:bg-gray-200"
        >
            Send
        </button>
    </form>
    );
}