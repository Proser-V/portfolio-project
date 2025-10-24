"use client";
import { UserProvider } from "@/context/UserContext";

export default function UserProviderWrapper({ user, children }) {
  return <UserProvider user={user}>{children}</UserProvider>;
}
