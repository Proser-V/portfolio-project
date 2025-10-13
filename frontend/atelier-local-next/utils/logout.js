export async function logout() {
    try {
        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/users/logout`, {
            method: "POST",
            credentials: "include",
        });
        if (response.ok) {
            document.cookie = "jwt=; Max-Age=0; path=/; SameSite=Strict";
            console.log("Logout successful, cookie removed");
        } else {
            console.error("Logout failed with status:", response.status);
        }
    } catch (err) {
        console.error("Erreur lors de la déconnexion : ", err);
    }
}