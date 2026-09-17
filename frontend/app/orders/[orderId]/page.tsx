'use client'

import { useEffect, useState } from 'react'
import { ArrowLeft, CheckCircle2, IceCreamBowl, X } from 'lucide-react'
import { useParams, useRouter } from 'next/navigation'

const API_BASE_URL = 'http://localhost:8080'
const money = (value: number) => `${new Intl.NumberFormat('ko-KR').format(value)}원`

type OrderItem = { productId: number; productName: string; quantity: number; orderPrice: number; totalPrice: number }
type Order = { orderId?: number; orderNumber: string; createdAt: string; status: string; totalAmount: number; items: OrderItem[] }
type Payment = { status?: string }

export default function DetailPage() {
  const router = useRouter()
  const { orderId } = useParams<{ orderId: string }>()
  const [order, setOrder] = useState<Order | null>(null)
  const [payment, setPayment] = useState<Payment | null>(null)
  const [error, setError] = useState('')
  const [refundOpen, setRefundOpen] = useState(false)
  const [refundReason, setRefundReason] = useState('')
  const [refundMessage, setRefundMessage] = useState('')
  const [refundLoading, setRefundLoading] = useState(false)

  const loadOrder = async () => {
    const headers = { Authorization: `Bearer ${localStorage.getItem('accessToken') || ''}` }
    const [orderResponse, paymentResponse] = await Promise.all([
      fetch(`${API_BASE_URL}/api/orders/${orderId}`, { headers }),
      fetch(`${API_BASE_URL}/api/payments/1`, { headers }),
    ])
    if (orderResponse.status === 401) { router.push('/login'); return }
    if (!orderResponse.ok) throw new Error('주문 정보를 불러오지 못했습니다.')
    const orderResult = await orderResponse.json()
    setOrder(orderResult.data ?? orderResult)
    if (paymentResponse.ok) setPayment(await paymentResponse.json())
  }

  useEffect(() => {
    loadOrder().catch((requestError) => {
      console.error('[v0] 주문 상세 조회 실패:', requestError)
      setError(requestError instanceof Error ? requestError.message : '주문 정보를 불러오지 못했습니다.')
    })
  }, [orderId])

  const requestRefund = async () => {
    const reason = refundReason.trim()
    if (!reason) { setRefundMessage('환불 사유를 입력해주세요.'); return }
    if (reason.length > 100) { setRefundMessage('환불 사유는 100자 이하로 입력해주세요.'); return }
    setRefundLoading(true); setRefundMessage('')
    try {
      const response = await fetch(`${API_BASE_URL}/api/refunds`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${localStorage.getItem('accessToken') || ''}` },
        body: JSON.stringify({ orderId: order?.orderId ?? Number(orderId), reason }),
      })
      const result = await response.json().catch(() => ({}))
      if (response.status === 401) { router.push('/login'); return }
      if (!response.ok) throw new Error(result.message || '환불 요청에 실패했습니다.')
      setRefundOpen(false); setRefundReason(''); setRefundMessage('환불이 요청되었습니다')
      await loadOrder()
    } catch (requestError) {
      console.error('[v0] 환불 요청 실패:', requestError)
      setRefundMessage(requestError instanceof Error ? requestError.message : '환불 요청에 실패했습니다.')
    } finally { setRefundLoading(false) }
  }

  if (error) return <main className="min-h-screen bg-[#f5f7fa] p-10 text-[#0a1f3d]"><p role="alert">{error}</p></main>
  if (!order) return <main className="min-h-screen bg-[#f5f7fa] p-10 text-[#0a1f3d]">주문 정보를 불러오는 중...</main>

  const paid = payment?.status === 'PAID' || order.status === 'COMPLETED'
  const refunded = payment?.status === 'REFUNDED' || order.status === 'REFUNDED'
  const pending = order.status === 'PENDING_PAYMENT' && !paid && !refunded

  return <main className="min-h-screen bg-[#f5f7fa] text-[#0a1f3d]"><header className="border-b border-[#dce5ef] bg-white px-6 py-5"><div className="mx-auto flex max-w-3xl items-center justify-between"><button onClick={() => router.push('/')} className="flex items-center gap-3"><span className="grid size-10 place-items-center rounded-full bg-[#1265d8] text-white"><IceCreamBowl /></span><b>Two게더</b></button><button onClick={() => router.push('/orders')} className="text-sm font-bold text-[#52657c]">주문 내역</button></div></header><section className="mx-auto max-w-3xl px-6 py-14"><button onClick={() => router.push('/orders')} className="flex items-center gap-2 text-sm font-bold text-[#52657c]"><ArrowLeft /> 주문 내역</button><div className="mt-10 flex items-start justify-between"><div><p className="text-xs font-bold tracking-[.22em] text-[#159c98]">ORDER DETAIL</p><h1 className="mt-3 text-4xl font-bold">{order.orderNumber}</h1><p className="mt-3 text-sm text-[#64748b]">{new Date(order.createdAt).toLocaleString('ko-KR')}</p></div><span className="rounded-full bg-[#eaf3ff] px-4 py-2 text-sm font-bold text-[#1265d8]">{refunded ? '환불' : paid ? '결제 완료' : order.status === 'CANCELLED' ? '취소됨' : '결제 대기'}</span></div><div className="mt-8 overflow-hidden rounded-3xl border border-[#d8e1ec] bg-white">{order.items.map((item) => <div key={item.productId} className="flex items-center justify-between border-b border-[#edf1f6] px-6 py-5 last:border-0"><div><p className="font-bold">{item.productName}</p><p className="mt-1 text-sm text-[#64748b]">{item.quantity}개 · {money(item.orderPrice)}</p></div><strong>{money(item.totalPrice)}</strong></div>)}<div className="flex justify-between bg-[#f8fafc] px-6 py-5"><b>총 결제 금액</b><strong className="text-xl">{money(order.totalAmount)}</strong></div></div><div className="mt-5 rounded-3xl border border-[#d8e1ec] bg-white p-6"><div className="flex items-center gap-3"><CheckCircle2 className={refunded ? 'text-[#64748b]' : 'text-[#159c98]'} /><div><p className="font-bold">결제 상태</p><p className="mt-1 text-sm text-[#64748b]">{refunded ? '환불 처리됨' : paid ? '결제가 완료된 주문입니다.' : '결제가 필요한 주문입니다.'}</p></div></div></div>{refundMessage && <p role="status" className="mt-5 rounded-xl bg-[#e9fbfa] px-4 py-3 text-sm font-bold text-[#0b7773]">{refundMessage}</p>}{(pending || paid) && !refunded && <div className="mt-6 flex flex-wrap gap-3">{pending && <button onClick={() => router.push(`/orders/payment?orderId=${order.orderId ?? orderId}`)} className="flex-1 rounded-2xl bg-[#1265d8] px-5 py-4 text-sm font-bold text-white">결제하기</button>}{paid && <button onClick={() => setRefundOpen(true)} className="flex-1 rounded-2xl border border-[#1265d8] bg-white px-5 py-4 text-sm font-bold text-[#1265d8]">환불 요청</button>}</div>}{refundOpen && <div className="fixed inset-0 z-50 grid place-items-center bg-[#0a1f3d]/45 p-6"><div role="dialog" aria-modal="true" aria-labelledby="refund-title" className="w-full max-w-md rounded-3xl bg-white p-6 shadow-2xl"><div className="flex items-center justify-between"><h2 id="refund-title" className="text-xl font-bold">환불 요청</h2><button onClick={() => setRefundOpen(false)} aria-label="닫기"><X /></button></div><label className="mt-6 block text-sm font-bold" htmlFor="refund-reason">환불 사유<textarea id="refund-reason" maxLength={100} value={refundReason} onChange={(event) => setRefundReason(event.target.value)} placeholder="환불 사유를 입력해주세요" className="mt-2 min-h-28 w-full rounded-xl border border-[#d8e1ec] p-3 text-sm outline-none focus:border-[#1265d8]" /></label>{refundMessage && <p role="alert" className="mt-3 text-sm text-red-600">{refundMessage}</p>}<button onClick={requestRefund} disabled={refundLoading} className="mt-5 w-full rounded-xl bg-[#0a1f3d] px-4 py-3 text-sm font-bold text-white disabled:opacity-50">{refundLoading ? '처리 중...' : '환불 요청 확인'}</button></div></div>}</section></main>
}
