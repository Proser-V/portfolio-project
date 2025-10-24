import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import getApiUrl from "./api";

let stompClient = null;

export function connectWebSocket(jwtToken, onMessageReceived) {
  if (stompClient && stompClient.connected) return stompClient;

  const socket = new SockJS(`${getApiUrl()}/ws`);
  stompClient = new Client({
    webSocketFactory: () => socket,
    debug: (str) => console.log("STOMP:", str),
    reconnectDelay: 5000,
    connectHeaders: {
      Authorization: `Bearer ${jwtToken}`,
    },
    onConnect: () => {
      console.log("STOMP connecté");
      // S'abonner aux messages personnels
      stompClient.subscribe("/user/queue/messages", (msg) => {
        const data = JSON.parse(msg.body);
        onMessageReceived(data);
      });
    },
  });

  stompClient.activate();
  return stompClient;
}

export function disconnectWebSocket() {
  if (stompClient) stompClient.deactivate();
}
