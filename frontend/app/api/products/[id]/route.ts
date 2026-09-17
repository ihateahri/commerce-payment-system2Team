import { NextResponse } from 'next/server'

type Product = { id: string; name: string; price: number; stock: number; description: string; status: string; category: string; updatedAt: string }

const products = new Map<string, Product>([
  ['1', { id: '1', name: 'Galaxy 스마트폰', price: 890000, stock: 18, description: '손안에 담긴 선명한 화면과 빠른 성능을 만나보세요.', status: '판매 중', category: 'SMARTPHONE', updatedAt: '2026-08-01T10:00:00' }],
  ['2', { id: '2', name: '초경량 노트북', price: 1290000, stock: 12, description: '가볍게 휴대하고 강력하게 작업하는 데일리 노트북입니다.', status: '판매 중', category: 'LAPTOP', updatedAt: '2026-07-30T10:00:00' }],
  ['3', { id: '3', name: '고해상도 태블릿', price: 680000, stock: 0, description: '콘텐츠 감상과 창작을 위한 몰입감 높은 디스플레이입니다.', status: '품절', category: 'TABLET', updatedAt: '2026-07-28T10:00:00' }],
  ['4', { id: '4', name: '무선 이어폰', price: 189000, stock: 24, description: '선명한 사운드와 편안한 착용감을 제공하는 무선 이어폰입니다.', status: '판매 중', category: 'EARPHONES', updatedAt: '2026-07-25T10:00:00' }],
  ['5', { id: '5', name: '스마트워치', price: 329000, stock: 8, description: '일상과 운동을 한 번에 관리하는 스마트한 파트너입니다.', status: '판매 중', category: 'SMARTWATCH', updatedAt: '2026-07-21T10:00:00' }],
  ['6', { id: '6', name: '울트라와이드 모니터', price: 549000, stock: 0, description: '넓은 화면으로 작업 공간을 더 여유롭게 확장합니다.', status: '판매 중지', category: 'MONITOR', updatedAt: '2026-07-18T10:00:00' }],
])

export async function GET(_: Request, { params }: { params: Promise<{ id: string }> }) {
  const { id } = await params
  const product = products.get(id)
  return product ? NextResponse.json(product) : NextResponse.json({ errorCode: 'product_004', message: '존재하지 않는 상품 요청' }, { status: 404 })
}

export async function PATCH(request: Request, { params }: { params: Promise<{ id: string }> }) {
  const { id } = await params
  const current = products.get(id)
  if (!current) return NextResponse.json({ errorCode: 'product_004', message: '존재하지 않는 상품 요청' }, { status: 404 })
  const body = await request.json().catch(() => null)
  const validCategories = ['SMARTPHONE', 'LAPTOP', 'TABLET', 'EARPHONES', 'SMARTWATCH', 'MONITOR']
  if (!body || typeof body.name !== 'string' || !body.name.trim() || !Number.isInteger(body.price) || body.price < 0 || typeof body.description !== 'string' || !body.description.trim() || !validCategories.includes(body.category)) {
    return NextResponse.json({ errorCode: 'product_000', message: '잘못된 필드로 요청' }, { status: 400 })
  }
  const updated = { ...current, name: body.name.trim(), price: body.price, description: body.description.trim(), category: body.category, updatedAt: new Date().toISOString() }
  products.set(id, updated)
  return NextResponse.json(updated)
}
