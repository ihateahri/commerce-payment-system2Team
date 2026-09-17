import { NextResponse } from 'next/server'

const cart = {
  content: [
    { cartItemId: 1, productId: 1, name: 'Galaxy 스마트폰', price: 890000, quantity: 1, status: '판매 중', createdAt: '2026-08-01T10:00:00', stock: 30 },
    { cartItemId: 2, productId: 4, name: '무선 이어폰', price: 189000, quantity: 2, status: '판매 중', createdAt: '2026-08-02T10:00:00', stock: 10 },
  ],
  totalPrice: 1268000,
}

export async function GET(request: Request) {
  if (!request.headers.get('x-demo-user')) {
    return NextResponse.json({ errorCode: 'cart_001', message: '로그인이 필요합니다' }, { status: 401 })
  }
  return NextResponse.json(cart)
}
