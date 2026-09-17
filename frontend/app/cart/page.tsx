'use client'

import { useEffect, useState } from 'react'
import { ArrowLeft, IceCreamBowl, Minus, Plus, ShoppingBag, Trash2 } from 'lucide-react'
import { useRouter } from 'next/navigation'

type CartItem = { cartItemId: number; productId: number; name: string; price: number; quantity: number; status: string; createdAt: string; stock: number }
type ApiCartItem = Omit<CartItem, 'cartItemId'> & { cartItemsId: number }
type CartResponse = { content: CartItem[]; totalPrice: number }
type ApiCartResponse = { cartItemsResponseList?: ApiCartItem[]; totalPrice?: number }
const API_BASE_URL = 'http://localhost:8080'
const formatPrice = (price: number) => `${new Intl.NumberFormat('ko-KR').format(price)}원`

function CartSkeleton() { return <div className="flex flex-col gap-4">{[1, 2].map((item) => <div key={item} className="h-32 animate-pulse rounded-2xl bg-[#e5edf5]" />)}</div> }

export default function CartPage() {
  const router = useRouter()
  const [userName, setUserName] = useState('')
  const [cart, setCart] = useState<CartResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [unauthorized, setUnauthorized] = useState(false)
  const [updatingId, setUpdatingId] = useState<number | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    const name = sessionStorage.getItem('userName') || ''
    setUserName(name)
    fetch(`${API_BASE_URL}/api/cart`, { headers: { Authorization: `Bearer ${localStorage.getItem('accessToken') || ''}` } })
      .then(async (response) => { if (response.status === 401) { setUnauthorized(true); return null }; if (!response.ok) throw new Error(`Cart request failed: ${response.status}`); return response.json() })
      .then((data: ApiCartResponse | ApiCartItem[] | null) => {
        if (!data) return
        const apiItems = Array.isArray(data) ? data : data.cartItemsResponseList ?? []
        const content = apiItems.map((item) => ({ ...item, cartItemId: item.cartItemsId }))
        const totalPrice = Array.isArray(data) ? content.reduce((sum, item) => sum + item.price * item.quantity, 0) : data.totalPrice ?? 0
        setCart({ content, totalPrice })
      })
      .catch((requestError) => { console.error('[v0] Failed to load cart:', requestError); setError('장바구니를 불러오지 못했습니다') })
      .finally(() => setLoading(false))
  }, [])

  const updateQuantity = async (id: number, delta: number) => {
    if (updatingId !== null || !cart) return
    const item = cart.content.find((candidate) => candidate.cartItemId === id)
    if (!item) return
    const nextQuantity = item.quantity + delta
    if (nextQuantity < 1) return
    if (nextQuantity > item.stock) {
      setError('재고보다 많은 수량은 담을 수 없습니다')
      return
    }

    setError('')
    setUpdatingId(id)
    try {
      const response = await fetch(`${API_BASE_URL}/api/cart/items/${id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${localStorage.getItem('accessToken') || ''}` },
        body: JSON.stringify({ quantity: nextQuantity }),
      })
      if (response.status === 401) {
        router.push('/login')
        return
      }
      if (!response.ok) {
        const message = response.status === 400 ? '재고보다 많은 수량은 담을 수 없습니다' : response.status === 404 ? '존재하지 않는 장바구니 상품입니다' : response.status === 403 ? '권한이 없습니다' : '수량 변경에 실패했습니다'
        setError(message)
        return
      }
      const responseText = await response.text()
      let updatedQuantity = nextQuantity
      if (responseText.trim()) {
        try {
          const updated = JSON.parse(responseText) as { quantity?: number }
          if (typeof updated.quantity === 'number') updatedQuantity = updated.quantity
        } catch (parseError) {
          console.error('[v0] 장바구니 수량 응답 파싱 실패:', parseError)
        }
      }
      setCart((current) => {
        if (!current) return current
        const content = current.content.map((entry) => entry.cartItemId === id ? { ...entry, quantity: updatedQuantity } : entry)
        return { content, totalPrice: content.reduce((sum, entry) => sum + entry.price * entry.quantity, 0) }
      })
    } catch {
      setError('네트워크 오류가 발생했습니다. 다시 시도해주세요')
    } finally {
      setUpdatingId(null)
    }
  }
  const removeItem = async (id: number) => {
    setError('')
    setUpdatingId(id)
    try {
      const response = await fetch(`${API_BASE_URL}/api/cart/items/${id}`, { method: 'DELETE', headers: { Authorization: `Bearer ${localStorage.getItem('accessToken') || ''}` } })
      if (response.status === 401) { router.push('/login'); return }
      if (!response.ok) {
        setError(response.status === 403 ? '권한이 없습니다' : response.status === 404 ? '이미 삭제되었거나 존재하지 않는 상품입니다' : '상품 삭제에 실패했습니다')
        return
      }
      setCart((current) => {
        if (!current) return current
        const content = current.content.filter((item) => item.cartItemId !== id)
        return { content, totalPrice: content.reduce((sum, item) => sum + item.price * item.quantity, 0) }
      })
    } catch (requestError) { console.error('[v0] 장바구니 상품 삭제 실패:', requestError); setError('네트워크 오류가 발생했습니다. 다시 시도해주세요') } finally { setUpdatingId(null) }
  }

  const goToCheckout = () => { if (!cart?.content.length) return; sessionStorage.setItem('checkoutItems', JSON.stringify(cart.content.map((item) => ({ productId: item.productId, quantity: item.quantity })))); router.push('/orders/preview') }

  const clearCart = async () => {
    if (!cart?.content.length || !window.confirm('장바구니를 비우시겠습니까?')) return
    setError('')
    setUpdatingId(-1)
    try {
      const response = await fetch(`${API_BASE_URL}/api/cart`, { method: 'DELETE', headers: { Authorization: `Bearer ${localStorage.getItem('accessToken') || ''}` } })
      if (response.status === 401) { router.push('/login'); return }
      if (!response.ok) { setError('장바구니 전체 삭제에 실패했습니다'); return }
      setCart({ content: [], totalPrice: 0 })
    } catch (requestError) { console.error('[v0] 장바구니 전체 삭제 실패:', requestError); setError('네트워크 오류가 발생했습니다. 다시 시도해주세요') } finally { setUpdatingId(null) }
  }

  return <main className="min-h-screen bg-[#f5f7fa] text-[#0a1f3d]"><header className="border-b border-[#dce5ef] bg-white/90 px-6 py-5 backdrop-blur sm:px-10 lg:px-16"><div className="mx-auto flex max-w-7xl items-center justify-between"><button onClick={() => router.push('/')} className="flex items-center gap-3" aria-label="Two게더 홈으로 이동"><span className="grid size-10 place-items-center rounded-full bg-[#1265d8] text-white"><IceCreamBowl aria-hidden="true" /></span><span className="text-xl font-bold tracking-[-0.04em]">Two게더</span></button><div className="flex items-center gap-5 text-[#52657c]"><button className="text-sm font-medium" onClick={() => router.push('/login')}>{userName || '로그인'}</button><button className="text-sm font-medium" onClick={() => router.push('/orders')}>주문 내역</button><button onClick={() => router.push('/')} aria-label="상품 보기"><ShoppingBag aria-hidden="true" /></button></div></div></header><section className="mx-auto max-w-5xl px-6 pb-20 pt-12 sm:px-10 lg:px-16"><button onClick={() => router.push('/')} className="mb-8 inline-flex items-center gap-2 text-sm font-bold text-[#52657c] hover:text-[#1265d8]"><ArrowLeft aria-hidden="true" /> 상품 목록</button><p className="text-xs font-bold tracking-[0.22em] text-[#159c98]">YOUR PICKS</p><div className="mt-3 flex items-center justify-between gap-4"><h1 className="text-4xl font-bold tracking-[-0.06em]">장바구니</h1>{cart?.content.length ? <button type="button" onClick={clearCart} disabled={updatingId !== null} className="inline-flex items-center gap-2 rounded-xl border border-[#d8e1ec] bg-white px-4 py-2 text-sm font-bold text-[#52657c] transition hover:border-[#1265d8] hover:text-[#1265d8] disabled:cursor-not-allowed disabled:opacity-50"><Trash2 aria-hidden="true" /> 전체 삭제</button> : null}</div>{error && <p role="alert" className="mt-4 rounded-xl bg-red-50 px-4 py-3 text-sm font-medium text-red-700">{error}</p>}{loading ? <div className="mt-10"><CartSkeleton /></div> : unauthorized ? <div className="mt-10 rounded-[24px] border border-[#d8e1ec] bg-white px-6 py-20 text-center shadow-sm"><ShoppingBag className="mx-auto text-[#1265d8]" /><h2 className="mt-5 text-xl font-bold">로그인이 필요합니다</h2><p className="mt-2 text-sm text-[#64748b]">장바구니를 확인하려면 로그인해주세요.</p><button onClick={() => router.push('/login')} className="mt-7 rounded-xl bg-gradient-to-r from-[#0a1f3d] to-[#1265d8] px-6 py-3 text-sm font-bold text-white">로그인하러 가기</button></div> : !cart?.content.length ? <div className="mt-10 rounded-[24px] border border-dashed border-[#cbd8e7] bg-white px-6 py-20 text-center"><ShoppingBag className="mx-auto text-[#1265d8]" /><h2 className="mt-5 text-xl font-bold">장바구니가 비어있습니다</h2><button onClick={() => router.push('/')} className="mt-7 rounded-xl bg-[#0a1f3d] px-6 py-3 text-sm font-bold text-white">상품 보러가기</button></div> : <><div className="mt-10 flex flex-col gap-4">{cart.content.map((item) => { const lowStock = item.quantity > item.stock || item.status === '품절'; return <article key={item.cartItemId} className={`rounded-2xl border bg-white p-5 shadow-sm sm:flex sm:items-center sm:gap-5 ${lowStock ? 'border-red-200' : 'border-[#d8e1ec]'}`}><div className="flex min-w-0 flex-1 items-center gap-4"><div className="grid size-16 shrink-0 place-items-center rounded-xl bg-[#eaf3ff] text-xl font-bold text-[#1265d8]">{item.productId}</div><div className="min-w-0"><h2 className="truncate font-bold">{item.name}</h2><p className="mt-1 text-sm text-[#52657c]">{formatPrice(item.price)}</p><div className="mt-2 flex flex-wrap gap-2"><span className="rounded-full bg-[#eaf3ff] px-2.5 py-1 text-xs font-bold text-[#1265d8]">{item.status}</span>{lowStock && <span className="rounded-full bg-red-50 px-2.5 py-1 text-xs font-bold text-red-600">재고 부족</span>}</div></div></div><div className="mt-5 flex items-center justify-between gap-5 sm:mt-0"><div className="flex items-center rounded-lg border border-[#d8e1ec]"><button onClick={() => updateQuantity(item.cartItemId, -1)} disabled={updatingId === item.cartItemId || item.quantity <= 1} className="p-2" aria-label={`${item.name} 수량 줄이기`}><Minus /></button><span className="min-w-8 text-center text-sm font-bold">{item.quantity}</span><button onClick={() => updateQuantity(item.cartItemId, 1)} disabled={updatingId === item.cartItemId || item.quantity >= item.stock} className="p-2" aria-label={`${item.name} 수량 늘리기`}><Plus /></button></div><strong className="min-w-24 text-right">{formatPrice(item.price * item.quantity)}</strong><button onClick={() => removeItem(item.cartItemId)} className="text-[#64748b] hover:text-red-600" aria-label={`${item.name} 삭제`}><Trash2 /></button></div></article>})}</div><div className="mt-8 flex flex-col items-end gap-4 rounded-2xl bg-[#0a1f3d] p-6 text-white sm:flex-row sm:justify-between sm:items-center"><div><p className="text-sm text-[#b8c9df]">전체 합계</p><p className="mt-1 text-2xl font-bold">{formatPrice(cart.totalPrice)}</p></div><button className="rounded-xl bg-gradient-to-r from-[#0f2947] to-[#2979ff] px-7 py-3 text-sm font-bold shadow-lg shadow-[#2979ff]/20">주문하기</button></div></>}<button type="button" onClick={goToCheckout}  disabled={!cart?.content.length || updatingId !== null} className="mt-6 w-full rounded-2xl bg-gradient-to-r from-[#0a1f3d] to-[#2979ff] px-5 py-4 text-sm font-bold text-white transition hover:from-[#1265d8] disabled:cursor-not-allowed disabled:opacity-50">주문하기</button></section></main>
}
