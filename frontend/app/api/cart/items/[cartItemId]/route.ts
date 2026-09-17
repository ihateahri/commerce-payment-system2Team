import { NextResponse } from 'next/server'

const quantities: Record<number, number> = { 1: 1, 2: 2 }
const stocks: Record<number, number> = { 1: 30, 2: 10 }

export async function PATCH(request: Request, { params }: { params: Promise<{ cartItemId: string }> }) {
  if (!request.headers.get('x-demo-user')) {
    return NextResponse.json({ errorCode: 'cart_001', message: '로그인이 필요합니다' }, { status: 401 })
  }

  const { cartItemId: rawId } = await params
  const cartItemId = Number(rawId)
  const body = await request.json().catch(() => null)
  const quantity = body?.quantity

  if (!Number.isInteger(cartItemId) || quantities[cartItemId] === undefined) {
    return NextResponse.json({ errorCode: 'cart_004', message: '존재하지 않는 장바구니 요청' }, { status: 404 })
  }
  if (!Number.isInteger(quantity) || quantity < 1) {
    return NextResponse.json({ errorCode: 'cart_000', message: '수량은 1 이상이어야 합니다' }, { status: 400 })
  }
  if (quantity > stocks[cartItemId]) {
    return NextResponse.json({ errorCode: 'cart_000', message: '재고보다 많은 수량 요청' }, { status: 400 })
  }

  quantities[cartItemId] = quantity
  return NextResponse.json({ cartItemId, quantity })
}
