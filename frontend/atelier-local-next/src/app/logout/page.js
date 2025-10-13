"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { logout } from "utils/logout";

export default function LogoutPage() {
  const router = useRouter();

  useEffect(() => {
    const doLogout = async () => {
      await logout();
      window.location.href ="/";
    };

    doLogout();
  }, [router]);

  return (
    <div className="flex items-center justify-center h-screen text-center text-xl p-4">
      Merci de votre visite,<br/>à bientôt dans l'Atelier Local !
    </div>
  );
}
