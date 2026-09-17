import { NextResponse } from 'next/server'
import { payment, getPaymentStatus } from '../confirm/route'

export async function GET(_: Request, { params }: { params: Promise<{ paymentId: string }> }) {
  const { paymentId } = await params
  if (paymentId !== payment.paymentId) return NextResponse.json({ message: '존재하지 않는 결제입니다.' }, { status: 404 })
  return NextResponse.json({ paymentId: payment.paymentId, orderId: payment.orderId, amount: payment.amount, paymentMethod: payment.paymentMethod, status: getPaymentStatus(), approvedAt: getPaymentStatus() === 'PENDING' ? null : payment.approvedAt })
}
