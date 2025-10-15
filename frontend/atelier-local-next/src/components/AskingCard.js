import Image from "next/image";
import Link from "next/link";
import logo from "../app/favicon.ico";
import placeholder from "../../public/tronche.jpg"

export default function AskingCard({ asking, className }) {
    <Link
      href={`/askings/${asking?.id}`}
      className={`flex flex-col sm:flex-row items-center sm:items-stretch justify-between bg-white border-2 border-gold shadow-md border-solid overflow-hidden w-full max-w-[1150px] ${className}`}
    ></Link>
}