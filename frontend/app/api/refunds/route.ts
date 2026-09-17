import { NextResponse } from 'next/server'
import { payment, getPaymentStatus, setPaymentStatus } from '../payments/confirm/route'

export async function POST(request: Request) {
  const body = await request.json().catch(() => ({}))
  if (body.orderId !== 10) return NextResponse.json({ message: '존재하지 않는 주문입니다.' }, { status: 404 })
  if (getPaymentStatus() === 'REFUNDED') return NextResponse.json({ message: '이미 환불된 주문입니다.' }, { status: 409 })
  if (getPaymentStatus() !== 'PAID') return NextResponse.json({ message: '결제 완료된 주문만 환불할 수 있습니다.' }, { status: 403 })
  setPaymentStatus('REFUNDED')
  return NextResponse.json({ orderId: 10, orderNumber: 'ORDER-001', orderStatus: '환불', paymentStatus: '환불', refundId: 1, refundAmount: payment.amount, refundedAt: new Date().toISOString(), reason: body.reason || '' })
}
