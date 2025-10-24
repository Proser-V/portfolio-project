import { cookies } from "next/headers";
import getApiUrl from "./api";

export async function getUser() {
  const cookieStore = await cookies();
  const token = cookieStore.get("jwt")?.value;

  if (!token) return null;

  const cookieHeader = cookieStore
    .getAll()
    .map((c) => `${c.name}=${c.value}`)
    .join("; ");

  try {
    const res = await fetch(`${getApiUrl()}/api/users/me`, {
      method: "GET",
      headers: {
        Cookie: cookieHeader,
      },
      cache: "no-store",
    });

    if (res.ok) {
      const data = await res.json();

      return {
        ...data.user,
        role: data.role?.toLowerCase() || data.user.role?.toLowerCase(),
        jwtToken: token,
      };
    }
  } catch (err) {
    console.error("Erreur lors de la récupération de l'utilisateur : ", err);
  }

  return null;
}