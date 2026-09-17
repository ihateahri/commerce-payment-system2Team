import { NextResponse } from 'next/server'

const orders = [{ orderId: 10, orderNumber: 'ORDER-001', totalAmount: 25000, status: 'PENDING_PAYMENT', createdAt: '2026-09-09T14:30:00' }, { orderId: 9, orderNumber: 'ORDER-002', totalAmount: 40000, status: 'COMPLETED', createdAt: '2026-09-08T18:20:00' }]
const catalog = [{ productId: 1, productName: 'Galaxy 스마트폰', price: 890000 }, { productId: 2, productName: '초경량 노트북', price: 1290000 }, { productId: 3, productName: '고해상도 태블릿', price: 680000 }, { productId: 4, productName: '무선 이어폰', price: 189000 }]
export async function GET(request: Request) { const { searchParams } = new URL(request.url); const page = Number(searchParams.get('page') || 0); const size = Number(searchParams.get('size') || 10); return NextResponse.json({ success: true, data: { orders: orders.slice(page * size, (page + 1) * size), totalElements: orders.length, currentPage: page, pageSize: size } }) }
export async function POST(request: Request) { const body = await request.json(); const items = (body.items || []).map((item: { productId: number; quantity: number }) => { const product = catalog.find((p) => p.productId === item.productId) || catalog[0]; return { ...product, quantity: item.quantity, totalPrice: product.price * item.quantity } }); const totalAmount = items.reduce((sum: number, item: { totalPrice: number }) => sum + item.totalPrice, 0); return NextResponse.json({ success: true, data: { orderId: 10, orderNumber: 'ORDER-001', totalAmount, status: 'PENDING_PAYMENT' } }) }
export { catalog } 
export const orderItems = [{ productId: 1, productName: 'Galaxy 스마트폰', quantity: 2, orderPrice: 10000, totalPrice: 20000 }, { productId: 3, productName: '고해상도 태블릿', quantity: 1, orderPrice: 5000, totalPrice: 5000 }]
export const orderStore = orders
export const itemCatalog = catalog

export async function previewItems(items: { productId: number; quantity: number }[]) { return items.map((item) => { const product = catalog.find((p) => p.productId === item.productId) || catalog[0]; return { ...product, quantity: item.quantity, totalPrice: product.price * item.quantity } }) }

export async function OPTIONS() { return NextResponse.json({ success: true }) }

// Preview is kept as a separate endpoint to match the client contract.
