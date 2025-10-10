import { NextResponse } from "next/server";

export async function GET(req) {
    const cookie = req.headers.get("cookie");

    const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/users/me`, {
        headers: {
            "Content-Type": "application/json",
            cookie,
        },
    });

    if (!res.ok) {
        return NextResponse.json({ error: "Unauthorized"}, {status: 403});
    }

    const data = await res.json();
    return NextResponse.json(data);
}