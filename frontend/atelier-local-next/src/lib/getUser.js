import { cookies } from "next/headers";

export async function getUser() {
  const cookieStore = await cookies();
  const token = cookieStore.get("jwt")?.value;

  if (!token) return null;

  const cookieHeader = cookieStore
    .getAll()
    .map((c) => `${c.name}=${c.value}`)
    .join("; ");

  try {
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/users/me`, {
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
      };
    }
  } catch (err) {
    console.error("Erreur lors de la récupération de l'utilisateur : ", err);
  }

  return null;
}