import { NextResponse } from 'next/server'

let paymentStatus: 'PENDING' | 'PAID' | 'REFUNDED' = 'PENDING'
const payment = { paymentId: '1', orderId: '10', amount: 25000, paymentMethod: 'CARD', approvedAt: '2026-09-17T10:00:00' }

export async function POST(request: Request) {
  const body = await request.json().catch(() => ({}))
  if (!Number.isInteger(body.paymentId) || !Number.isInteger(body.amount) || body.paymentId !== 1 || body.amount !== payment.amount) return NextResponse.json({ message: '결제 정보가 올바르지 않습니다.' }, { status: 400 })
  if (paymentStatus === 'PAID') return NextResponse.json({ message: '이미 승인된 결제입니다.' }, { status: 409 })
  paymentStatus = 'PAID'
  return NextResponse.json({ paymentId: payment.paymentId, orderId: payment.orderId, amount: payment.amount, status: 'PAID', approvedAt: payment.approvedAt })
}

export { payment, getPaymentStatus, setPaymentStatus }
function getPaymentStatus() { return paymentStatus }
function setPaymentStatus(status: 'PENDING' | 'PAID' | 'REFUNDED') { paymentStatus = status }

export async function GET() { return NextResponse.json({ paymentId: payment.paymentId, orderId: payment.orderId, amount: payment.amount, paymentMethod: payment.paymentMethod, status: paymentStatus, approvedAt: paymentStatus === 'PENDING' ? null : payment.approvedAt }) }
