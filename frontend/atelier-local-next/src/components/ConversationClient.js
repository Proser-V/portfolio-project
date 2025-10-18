"use client";
import { useState } from "react";
import MessagesList from "./MessageList";
import MessageForm from "./MessageForm";

export default function ConversationClient({ initialMessages, user, otherUser, otherUserName, jwtToken }) {
  const [messages, setMessages] = useState(initialMessages || []);

  return (
    <>
      <MessagesList
        initialMessages={messages}
        user={user}
        otherUser={otherUser}
        otherUserName={otherUserName}
      />
      <MessageForm
        user={user}
        otherUser={otherUser}
        jwtToken={jwtToken}
        messages={messages}
        setMessages={setMessages}
      />
    </>
  );
}