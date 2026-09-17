'use client'

import { useEffect, useState } from 'react'
import { IceCreamBowl, LoaderCircle, UserRound } from 'lucide-react'
import { useRouter } from 'next/navigation'

type Member = {
  id: string | number
  name: string
  email: string
  phoneNumber: string
  createdAt: string
}

const API_BASE_URL = 'http://localhost:8080'

function formatDate(value: string) {
  return new Intl.DateTimeFormat('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' }).format(new Date(value))
}

export default function MyPage() {
  const router = useRouter()
  const [member, setMember] = useState<Member | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    if (!token) {
      router.replace('/login')
      return
    }

    fetch(`${API_BASE_URL}/api/members/me`, { headers: { Authorization: `Bearer ${token}` } })
      .then(async (response) => {
        if (response.status === 401) {
          router.replace('/login')
          return null
        }
        if (response.status === 404) throw new Error('사용자 정보를 찾을 수 없습니다')
        if (!response.ok) throw new Error('사용자 정보를 불러오지 못했습니다')
        return response.json() as Promise<Member>
      })
      .then((data) => { if (data) setMember(data) })
      .catch((requestError) => {
        console.error('[v0] 내 정보 조회 실패:', requestError)
        setError(requestError instanceof Error ? requestError.message : '사용자 정보를 불러오지 못했습니다')
      })
      .finally(() => setLoading(false))
  }, [router])

  return (
    <main className="min-h-screen bg-[#f5f7fa] text-[#0a1f3d]">
      <header className="border-b border-[#dce5ef] bg-white/90 px-6 py-5 backdrop-blur sm:px-10 lg:px-16">
        <div className="mx-auto flex max-w-7xl items-center justify-between">
          <button onClick={() => router.push('/')} className="flex items-center gap-3" aria-label="Two게더 홈으로 이동">
            <span className="grid size-10 place-items-center rounded-full bg-[#1265d8] text-white shadow-[0_6px_16px_rgba(18,101,216,0.25)]"><IceCreamBowl aria-hidden="true" /></span>
            <span className="text-xl font-bold tracking-[-0.04em]">Two게더</span>
          </button>
          <nav className="flex items-center gap-4 text-sm font-semibold text-[#52657c]">
            <button onClick={() => router.push('/')} className="hover:text-[#1265d8]">상품 목록</button>
            <button onClick={() => router.push('/cart')} className="hover:text-[#1265d8]">장바구니</button>
            <span className="flex items-center gap-2 text-[#1265d8]" aria-current="page"><UserRound className="size-4" aria-hidden="true" />마이페이지</span>
          </nav>
        </div>
      </header>

      <section className="mx-auto max-w-3xl px-6 py-14 sm:px-10 lg:py-20">
        <p className="text-xs font-bold tracking-[0.22em] text-[#159c98]">MY TWO게더</p>
        <h1 className="mt-4 text-4xl font-bold tracking-[-0.06em]">내 정보</h1>
        <p className="mt-3 text-sm text-[#64748b]">Two게더에 가입한 회원 정보를 확인하세요.</p>
        {loading ? <div className="mt-10 animate-pulse rounded-3xl bg-white p-8 shadow-sm"><div className="h-8 w-40 rounded bg-slate-200" /><div className="mt-8 space-y-5"><div className="h-12 rounded bg-slate-200" /><div className="h-12 rounded bg-slate-200" /><div className="h-12 rounded bg-slate-200" /></div></div> : error ? <div role="alert" className="mt-10 rounded-3xl border border-red-200 bg-red-50 px-6 py-16 text-center"><h2 className="text-lg font-bold text-red-700">{error}</h2><button onClick={() => router.push('/login')} className="mt-6 rounded-xl bg-[#0a1f3d] px-5 py-3 text-sm font-bold text-white">로그인으로 이동</button></div> : member ? <div className="mt-10 overflow-hidden rounded-3xl border border-[#d8e1ec] bg-white shadow-sm"><div className="flex items-center gap-4 border-b border-[#e8eef5] bg-gradient-to-r from-[#eaf3ff] to-[#e9fbfa] p-7"><span className="grid size-14 place-items-center rounded-2xl bg-[#1265d8] text-white"><UserRound aria-hidden="true" /></span><div><p className="text-xs font-bold tracking-[0.16em] text-[#159c98]">MEMBER PROFILE</p><h2 className="mt-1 text-2xl font-bold">{member.name}님</h2></div></div><dl className="divide-y divide-[#e8eef5] p-2">{[['이름', member.name], ['이메일', member.email], ['전화번호', member.phoneNumber], ['가입일', formatDate(member.createdAt)]].map(([label, value]) => <div key={label} className="flex flex-col gap-1 px-5 py-5 sm:flex-row sm:items-center sm:justify-between"><dt className="text-sm font-semibold text-[#64748b]">{label}</dt><dd className="text-sm font-bold text-[#0a1f3d]">{value}</dd></div>)}</dl></div> : null}
      </section>
    </main>
  )
}
