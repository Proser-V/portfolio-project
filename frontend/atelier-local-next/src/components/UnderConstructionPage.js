import Image from "next/image";

export default function UnserConstructionPage() {
    return (
        <div className="relative w-full h-[580px] flex items-center justify-center -z-10">
            <Image
                src="/under-construction.png"
                alt="Under construction part"
                width={1024}
                height={1536}
                className="object-contain max-h-full w-auto"
                priority
            />
        </div>
    );
}