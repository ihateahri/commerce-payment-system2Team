import { NextResponse } from 'next/server'
export async function POST(request: Request, { params }: { params: Promise<{ orderId: string }> }) { const { reason } = await request.json(); const { orderId } = await params; return NextResponse.json({ success: true, data: { orderId: Number(orderId), status: 'CANCELLED', cancelReason: reason } }) }
