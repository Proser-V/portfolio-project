"use client";
import { UserProvider } from "@/context/UserContext";

export default function ClientProviderWrapper({ user, children }) {
  return <UserProvider user={user}>{children}</UserProvider>;
}
