'use client'

import { FormEvent, useEffect, useState } from 'react'
import { ArrowLeft, IceCreamBowl, Save } from 'lucide-react'
import { useParams, useRouter } from 'next/navigation'

type Product = {
  id: string
  name: string
  price: number
  stock: number
  description: string
  status: string
  category: string
  updatedAt?: string
}

const categories = ['SMARTPHONE', 'LAPTOP', 'TABLET', 'EARPHONES', 'SMARTWATCH', 'MONITOR']
const fallbackProducts: Record<string, Product> = {
  '1': { id: '1', name: 'Galaxy 스마트폰', price: 890000, stock: 18, description: '손안에 담긴 선명한 화면과 빠른 성능을 만나보세요.', status: '판매 중', category: 'SMARTPHONE' },
  '2': { id: '2', name: '초경량 노트북', price: 1290000, stock: 12, description: '가볍게 휴대하고 강력하게 작업하는 데일리 노트북입니다.', status: '판매 중', category: 'LAPTOP' },
  '3': { id: '3', name: '고해상도 태블릿', price: 680000, stock: 0, description: '콘텐츠 감상과 창작을 위한 몰입감 높은 디스플레이입니다.', status: '품절', category: 'TABLET' },
  '4': { id: '4', name: '무선 이어폰', price: 189000, stock: 24, description: '선명한 사운드와 편안한 착용감을 제공하는 무선 이어폰입니다.', status: '판매 중', category: 'EARPHONES' },
  '5': { id: '5', name: '스마트워치', price: 329000, stock: 8, description: '일상과 운동을 한 번에 관리하는 스마트한 파트너입니다.', status: '판매 중', category: 'SMARTWATCH' },
  '6': { id: '6', name: '울트라와이드 모니터', price: 549000, stock: 0, description: '넓은 화면으로 작업 공간을 더 여유롭게 확장합니다.', status: '판매 중지', category: 'MONITOR' },
}

export default function AdminProductEditPage() {
  const { id } = useParams<{ id: string }>()
  const router = useRouter()
  const [form, setForm] = useState({ name: '', price: '', description: '', category: 'SMARTPHONE' })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    fetch(`http://localhost:8080/api/products/${id}`)
      .then(async (response) => {
        if (!response.ok) throw new Error()
        return response.json()
      })
      .catch((requestError) => { console.error('[v0] 관리자 상품 조회 실패:', requestError); throw requestError })
      .then((product: Product | undefined) => {
        if (!product) setError('존재하지 않는 상품 요청입니다.')
        else setForm({ name: product.name, price: String(product.price), description: product.description, category: product.category })
      })
      .finally(() => setLoading(false))
  }, [id])

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    const price = Number(form.price)
    if (!form.name.trim() || !Number.isInteger(price) || price < 0 || !form.description.trim() || !categories.includes(form.category)) {
      setError('상품명, 가격, 설명, 카테고리를 올바르게 입력해주세요.')
      return
    }
    setSaving(true)
    try {
      const response = await fetch(`http://localhost:8080/api/products/${id}`, { method: 'PATCH', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ ...form, name: form.name.trim(), description: form.description.trim(), price }) })
      if (!response.ok) {
        const body = await response.json().catch(() => null)
        throw new Error(body?.message || '상품 수정에 실패했습니다.')
      }
      router.push(`/products/${id}?updated=1`)
    } catch (submissionError) {
      setError(submissionError instanceof Error ? submissionError.message : '상품 수정에 실패했습니다.')
    } finally {
      setSaving(false)
    }
  }

  return <main className="min-h-screen bg-[#eef3f8] text-[#0a1f3d]"><header className="bg-[#0a1f3d] px-6 py-5 text-white sm:px-10"><div className="mx-auto flex max-w-5xl items-center justify-between"><div className="flex items-center gap-3"><span className="grid size-10 place-items-center rounded-full bg-[#1265d8]"><IceCreamBowl aria-hidden="true" /></span><div><p className="text-xs font-bold tracking-[0.2em] text-[#7fdbda]">ADMIN MODE</p><h1 className="font-bold">상품 관리</h1></div></div><button onClick={() => router.push(`/products/${id}`)} className="flex items-center gap-2 text-sm text-white/75 hover:text-white"><ArrowLeft aria-hidden="true" />상세 페이지</button></div></header><section className="mx-auto max-w-5xl px-6 py-12 sm:px-10"><div className="mb-8"><p className="text-xs font-bold tracking-[0.2em] text-[#1265d8]">PRODUCT MANAGEMENT</p><h2 className="mt-3 text-3xl font-bold tracking-[-0.05em]">상품 정보 수정</h2><p className="mt-2 text-sm text-[#64748b]">관리자 전용 편집 화면입니다. 상품 ID: {id}</p></div><form onSubmit={handleSubmit} className="max-w-2xl rounded-3xl border border-[#d8e1ec] bg-white p-6 shadow-sm sm:p-8">{loading ? <p className="py-16 text-center text-sm text-[#64748b]">상품 정보를 불러오는 중입니다.</p> : <div className="flex flex-col gap-6"><label className="flex flex-col gap-2 text-sm font-bold">상품명<input value={form.name} onChange={(event) => setForm({ ...form, name: event.target.value })} className="rounded-xl border border-[#d8e1ec] px-4 py-3 font-normal outline-none focus:border-[#159c98] focus:ring-2 focus:ring-[#7fdbda]/40" required /></label><label className="flex flex-col gap-2 text-sm font-bold">가격<input type="number" min="0" step="1" value={form.price} onChange={(event) => setForm({ ...form, price: event.target.value })} className="rounded-xl border border-[#d8e1ec] px-4 py-3 font-normal outline-none focus:border-[#159c98] focus:ring-2 focus:ring-[#7fdbda]/40" required /></label><label className="flex flex-col gap-2 text-sm font-bold">설명<textarea value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} rows={5} className="resize-y rounded-xl border border-[#d8e1ec] px-4 py-3 font-normal outline-none focus:border-[#159c98] focus:ring-2 focus:ring-[#7fdbda]/40" required /></label><label className="flex flex-col gap-2 text-sm font-bold">카테고리<select value={form.category} onChange={(event) => setForm({ ...form, category: event.target.value })} className="rounded-xl border border-[#d8e1ec] bg-white px-4 py-3 font-normal outline-none focus:border-[#159c98] focus:ring-2 focus:ring-[#7fdbda]/40">{categories.map((category) => <option key={category} value={category}>{category}</option>)}</select></label>{error && <p role="alert" className="rounded-xl bg-[#fff1f1] px-4 py-3 text-sm font-semibold text-[#c24141]">{error}</p>}<button type="submit" disabled={saving} className="flex items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-[#0a1f3d] to-[#1265d8] px-5 py-3 font-bold text-white transition hover:brightness-110 disabled:opacity-60"><Save aria-hidden="true" />{saving ? '저장 중...' : '저장'}</button></div>}</form></section></main>
}
