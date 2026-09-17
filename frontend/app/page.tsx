import ProductsPage from './products/page'

export default function HomePage() {
  return <ProductsPage />
}

/*
'use client'

import { useCallback, useEffect, useState } from 'react'
import { IceCreamBowl, ChevronDown, ChevronLeft, ChevronRight, Search, ShoppingBag, SlidersHorizontal } from 'lucide-react'
import { useRouter } from 'next/navigation'

type Product = {
  id: string
  name: string
  price: number
  status: string
  category: string
  createdAt: string
}

type ProductResponse = {
  content: Product[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

const fallbackProducts: Product[] = [
  { id: '1', name: 'Galaxy 스마트폰', price: 890000, status: '판매 중', category: 'SMARTPHONE', createdAt: '2026-08-01T10:00:00' },
  { id: '2', name: '초경량 노트북', price: 1290000, status: '판매 중', category: 'LAPTOP', createdAt: '2026-07-30T10:00:00' },
  { id: '3', name: '고해상도 태블릿', price: 680000, status: '품절', category: 'TABLET', createdAt: '2026-07-28T10:00:00' },
  { id: '4', name: '무선 이어폰', price: 189000, status: '판매 중', category: 'EARPHONES', createdAt: '2026-07-25T10:00:00' },
  { id: '5', name: '스마트워치', price: 329000, status: '판매 중', category: 'SMARTWATCH', createdAt: '2026-07-21T10:00:00' },
  { id: '6', name: '울트라와이드 모니터', price: 549000, status: '판매 중지', category: 'MONITOR', createdAt: '2026-07-18T10:00:00' },
]

const formatPrice = (price: number) => `${new Intl.NumberFormat('ko-KR').format(price)}원`

function ProductSkeleton() {
  return <div className="animate-pulse"><div className="aspect-[4/3] rounded-[22px] bg-slate-200" /><div className="mt-4 h-3 w-20 rounded bg-slate-200" /><div className="mt-3 h-5 w-3/4 rounded bg-slate-200" /><div className="mt-3 h-4 w-1/3 rounded bg-slate-200" /></div>
}

export default function ProductsPage() {
  const router = useRouter()
  const [products, setProducts] = useState<Product[]>([])
  const [totalPages, setTotalPages] = useState(1)
  const [page, setPage] = useState(0)
  const [category, setCategory] = useState('전체')
  const [sort, setSort] = useState('LATEST')
  const [minPrice, setMinPrice] = useState('')
  const [maxPrice, setMaxPrice] = useState('')
  const [appliedMinPrice, setAppliedMinPrice] = useState('')
  const [appliedMaxPrice, setAppliedMaxPrice] = useState('')
  const [loading, setLoading] = useState(true)
  const [usingFallback, setUsingFallback] = useState(false)
  const [userName, setUserName] = useState('')

  useEffect(() => {
    setUserName(sessionStorage.getItem('userName') || '')
  }, [])

  const loadProducts = useCallback(async () => {
    setLoading(true)
    try {
      const params = new URLSearchParams({ page: String(page), size: '20', sort })
      if (category !== '전체') params.set('category', category)
      if (appliedMinPrice) params.set('minPrice', appliedMinPrice)
      if (appliedMaxPrice) params.set('maxPrice', appliedMaxPrice)
      const response = await fetch(`http://localhost:8080/api/products?${params.toString()}`)
      if (!response.ok) throw new Error('상품 목록을 불러오지 못했습니다.')
      const data: ProductResponse = await response.json()
      setProducts(data.content ?? [])
      setTotalPages(Math.max(data.totalPages ?? 1, 1))
      setUsingFallback(false)
    } catch (requestError) {
      console.error('[v0] 상품 목록 API 요청 실패:', requestError)
      setProducts([])
      setTotalPages(1)
      setUsingFallback(false)
      setError('상품 목록을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.')
    } finally {
      setLoading(false)
    }
  }, [appliedMaxPrice, appliedMinPrice, category, page, sort])

  useEffect(() => { loadProducts() }, [loadProducts])

  const categories = ['전체', 'SMARTPHONE', 'LAPTOP', 'TABLET', 'EARPHONES', 'SMARTWATCH', 'MONITOR']
  const categoryLabels: Record<string, string> = { 전체: '전체', SMARTPHONE: '스마트폰', LAPTOP: '노트북', TABLET: '태블릿', EARPHONES: '이어폰', SMARTWATCH: '스마트워치', MONITOR: '모니터' }

  const chooseCategory = (value: string) => { setCategory(value); setPage(0) }
  const chooseSort = (value: string) => { setSort(value); setPage(0) }
  const applyPriceFilter = () => { setAppliedMinPrice(minPrice); setAppliedMaxPrice(maxPrice); setPage(0) }

  return (
    <main className="min-h-screen bg-[#f5f7fa] text-[#0a1f3d]">
      <header className="border-b border-[#dce5ef] bg-white/90 px-6 py-5 backdrop-blur sm:px-10 lg:px-16">
        <div className="mx-auto flex max-w-7xl items-center justify-between">
          <button onClick={() => router.push('/')} className="flex items-center gap-3" aria-label="Two게더 홈으로 이동">
            <span className="grid size-10 place-items-center rounded-full bg-[#1265d8] text-white shadow-[0_6px_16px_rgba(18,101,216,0.25)]"><IceCreamBowl aria-hidden="true" /></span>
            <span className="text-xl font-bold tracking-[-0.04em]">Two게더</span>
          </button>
          <div className="flex items-center gap-5 text-[#52657c]">
            <button className="hidden text-sm font-medium sm:block" onClick={() => router.push('/login')}>{userName || '로그인'}</button>
            <button aria-label="상품 검색"><Search aria-hidden="true" /></button>
            <button onClick={() => router.push('/cart')} aria-label="장바구니"><ShoppingBag aria-hidden="true" /></button>
          </div>
        </div>
      </header>

      <section className="mx-auto max-w-7xl px-6 pb-14 pt-14 sm:px-10 lg:px-16 lg:pt-20">
        <div className="flex flex-col justify-between gap-8 md:flex-row md:items-end">
          <div><p className="mb-4 text-xs font-bold tracking-[0.22em] text-[#159c98]">FIND YOUR FAVORITE</p><h1 className="text-4xl font-bold tracking-[-0.06em] sm:text-5xl">새로운 발견, <span className="text-[#1265d8]">Two게더</span></h1><p className="mt-4 text-sm text-[#64748b]">취향에 꼭 맞는 상품을 둘러보세요.</p></div>
          <div className="relative flex items-center self-start rounded-xl border border-[#d8e1ec] bg-white px-4 py-3 text-sm font-medium shadow-sm md:self-auto"><SlidersHorizontal className="mr-2 size-4 text-[#1265d8]" /><select value={sort} onChange={(event) => chooseSort(event.target.value)} className="appearance-none bg-transparent pr-7 outline-none"><option value="LATEST">최신순</option><option value="PRICE_ASC">가격 낮은순</option><option value="PRICE_DESC">가격 높은순</option></select><ChevronDown className="pointer-events-none absolute right-3 size-4 text-[#64748b]" /></div>
        </div>

        <nav className="mt-12 flex gap-2 overflow-x-auto pb-2" aria-label="상품 카테고리">{categories.map((item) => <button key={item} onClick={() => chooseCategory(item)} className={`shrink-0 rounded-full px-5 py-2.5 text-sm font-semibold transition ${category === item ? 'bg-[#0a1f3d] text-white shadow-lg shadow-[#0a1f3d]/15' : 'border border-[#d8e1ec] bg-white text-[#64748b] hover:border-[#1265d8] hover:text-[#1265d8]'}`}>{categoryLabels[item]}</button>)}</nav>

        <form onSubmit={(event) => { event.preventDefault(); applyPriceFilter() }} className="mt-4 flex flex-wrap items-end gap-3 rounded-2xl border border-[#d8e1ec] bg-white p-4 shadow-sm" aria-label="가격 범위 필터">
          <label className="flex min-w-[150px] flex-1 flex-col gap-2 text-xs font-semibold text-[#52657c]">최소가<input type="number" min="0" value={minPrice} onChange={(event) => setMinPrice(event.target.value)} placeholder="예: 100000" className="rounded-xl border border-[#d8e1ec] px-3 py-2.5 text-sm text-[#0a1f3d] outline-none transition focus:border-[#159c98] focus:ring-2 focus:ring-[#7fdbda]/40" /></label>
          <label className="flex min-w-[150px] flex-1 flex-col gap-2 text-xs font-semibold text-[#52657c]">최대가<input type="number" min="0" value={maxPrice} onChange={(event) => setMaxPrice(event.target.value)} placeholder="예: 500000" className="rounded-xl border border-[#d8e1ec] px-3 py-2.5 text-sm text-[#0a1f3d] outline-none transition focus:border-[#159c98] focus:ring-2 focus:ring-[#7fdbda]/40" /></label>
          <button type="submit" className="rounded-xl bg-[#0a1f3d] px-5 py-2.5 text-sm font-bold text-white transition hover:bg-[#1265d8]">적용</button>
        </form>

        {usingFallback && !loading && <p className="mt-5 text-xs text-[#64748b]">API 연결 전 미리보기 상품을 보여드리고 있습니다.</p>}
        {loading ? <div className="mt-8 grid gap-x-5 gap-y-12 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">{Array.from({ length: 8 }, (_, index) => <ProductSkeleton key={index} />)}</div> : products.length === 0 ? <div className="mt-8 rounded-[24px] border border-dashed border-[#cbd8e7] bg-white px-6 py-24 text-center"><ShoppingBag className="mx-auto size-10 text-[#9aacc0]" /><h2 className="mt-5 text-lg font-bold">상품이 아직 없어요</h2><p className="mt-2 text-sm text-[#64748b]">다른 카테고리를 선택해보세요.</p></div> : <div className="mt-8 grid gap-x-5 gap-y-12 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">{products.map((product, index) => { const unavailable = product.status === '품절' || product.status === '판매 중지'; return <button key={product.id} onClick={() => router.push(`/products/${product.id}`)} className={`group text-left ${unavailable ? 'opacity-55' : ''}`} aria-label={`${product.name} 상세 보기`}><div className="relative flex aspect-[4/3] items-end overflow-hidden rounded-[22px] bg-gradient-to-br from-[#dceeff] via-[#eef8fb] to-[#c8ddf2] p-5 transition duration-300 group-hover:-translate-y-1 group-hover:shadow-xl group-hover:shadow-[#1265d8]/10"><span className="text-6xl font-bold tracking-[-0.12em] text-[#1265d8]/15">{String(index + 1).padStart(2, '0')}</span><span className={`absolute right-4 top-4 rounded-full px-3 py-1 text-[11px] font-bold ${unavailable ? 'bg-slate-500/80 text-white' : 'bg-white/85 text-[#1265d8]'}`}>{product.status}</span></div><p className="mt-4 text-xs font-bold tracking-[0.16em] text-[#159c98]">{product.category}</p><h2 className="mt-2 text-lg font-bold tracking-[-0.03em]">{product.name}</h2><p className="mt-2 text-sm font-semibold text-[#52657c]">{formatPrice(product.price)}</p></button> })}</div>}

        {totalPages > 1 && <div className="mt-16 flex items-center justify-center gap-2"><button disabled={page === 0} onClick={() => setPage((current) => current - 1)} className="grid size-9 place-items-center rounded-full border border-[#d8e1ec] bg-white disabled:opacity-35" aria-label="이전 페이지"><ChevronLeft /></button>{Array.from({ length: totalPages }, (_, index) => <button key={index} onClick={() => setPage(index)} className={`grid size-9 place-items-center rounded-full text-sm font-semibold ${page === index ? 'bg-[#1265d8] text-white' : 'text-[#64748b] hover:bg-white'}`}>{index + 1}</button>)}<button disabled={page >= totalPages - 1} onClick={() => setPage((current) => current + 1)} className="grid size-9 place-items-center rounded-full border border-[#d8e1ec] bg-white disabled:opacity-35" aria-label="다음 페이지"><ChevronRight /></button></div>}
      </section>
    </main>
  )
}
*/
