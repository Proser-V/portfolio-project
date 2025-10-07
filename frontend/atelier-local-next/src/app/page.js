import ClientHome from "@/components/ClientHome";

export default function Home() {
  let header;
  const user = {
    role: "client",
    firstName: "Valentin",
    avatar: "/tronche.jpg"
  };
  return (
    <>
      return <ClientHome client={user} />
    </>
  );
}