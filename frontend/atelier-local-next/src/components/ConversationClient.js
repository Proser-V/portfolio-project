"use client";
import { useState } from "react";
import MessagesList from "./MessageList";
import MessageForm from "./MessageForm";

export default function ConversationClient({ initialMessages, userId, otherUserId, otherUserName, jwtToken }) {
  const [messages, setMessages] = useState(initialMessages || []);

  return (
    <>
      <MessagesList
        initialMessages={messages}
        userId={userId}
        otherUserId={otherUserId}
        otherUserName={otherUserName}
      />
      <MessageForm
        userId={userId}
        otherUserId={otherUserId}
        jwtToken={jwtToken}
        messages={messages}
        setMessages={setMessages}
      />
    </>
  );
}