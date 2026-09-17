'use client'

import { FormEvent, useEffect, useState } from 'react'
import { ArrowRight, Eye, EyeOff, IceCreamBowl, LockKeyhole, Mail } from 'lucide-react'
import { useRouter } from 'next/navigation'

const API_BASE_URL = 'http://localhost:8080'

type LoginError = { message?: string; name?: string; userName?: string; token?: string; member?: { id?: number; name?: string; email?: string; phoneNumber?: string } }

export default function Page() {
  const router = useRouter()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    if (new URLSearchParams(window.location.search).get('signup') === 'success') {
      setSuccess('회원가입이 완료되었습니다. 로그인해주세요.')
      window.history.replaceState({}, '', '/login')
    }
  }, [])

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    if (!email.trim() || !password) {
      setError('이메일과 비밀번호를 모두 입력해주세요.')
      return
    }

    setIsLoading(true)
    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email.trim(), password }),
      })
      const data = (await response.json().catch(() => ({}))) as LoginError

      if (!response.ok) {
        setError(data.message || '로그인에 실패했습니다. 다시 시도해주세요.')
        return
      }

      if (data.token) localStorage.setItem('accessToken', data.token)
      if (data.member) localStorage.setItem('member', JSON.stringify(data.member))
      sessionStorage.setItem('accessToken', data.token || '')
      sessionStorage.setItem('userName', data.member?.name || data.name || data.userName || email.trim().split('@')[0])
      router.push('/')
    } catch {
      setError('서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <main className="min-h-screen overflow-hidden bg-[#F5F7FA] text-[#14213D]">
      <div className="mx-auto grid min-h-screen max-w-[1440px] lg:grid-cols-[1.08fr_0.92fr]">
        <section className="relative hidden overflow-hidden bg-[#0A1F3D] px-12 py-12 text-[#F5F7FA] lg:flex lg:flex-col lg:justify-between xl:px-20">
          <div className="relative flex items-center gap-3 text-sm font-semibold tracking-[0.08em]">
            <span className="grid size-10 place-items-center rounded-full bg-[#2979FF]" aria-hidden="true">
              <IceCreamBowl className="size-7" strokeWidth={1.8} aria-hidden="true" />
            </span>
            Two게더
          </div>
          <div className="relative max-w-lg pb-4">
            <p className="mb-7 text-sm font-medium uppercase tracking-[0.22em] text-[#7FDBDA]">COOL & CONNECTED</p>
            <h1 className="text-5xl font-medium leading-[1.08] tracking-[-0.05em] xl:text-6xl">당신의 취향을,<br />시원하게 연결하다.</h1>
            <p className="mt-7 max-w-sm text-base leading-7 text-[#D6E4F7]">필요한 전자기기를 한곳에서 발견하고, Two게더와 함께 시원하게 연결해보세요.</p>
          </div>
          <p className="relative text-xs text-[#8DBBFF]/70">© 2025 Two게더. All rights reserved.</p>
        </section>

        <section className="flex items-center justify-center px-6 py-12 sm:px-12">
          <div className="w-full max-w-[420px]">
            <div className="mb-12 lg:hidden"><div className="flex items-center gap-3 text-sm font-semibold tracking-[0.08em]"><span className="grid size-10 place-items-center rounded-full bg-[#2979FF]" aria-hidden="true"><IceCreamBowl className="size-7" strokeWidth={1.8} aria-hidden="true" /></span>Two게더</div></div>
            <div className="mb-10">
              <p className="mb-3 text-sm font-semibold tracking-[0.08em] text-[#159A9C]">WELCOME BACK</p>
              <h2 className="text-4xl font-medium tracking-[-0.05em]">다시 만나서 반가워요</h2>
              <p className="mt-3 text-sm text-[#64748B]">계정에 로그인하고 나만의 상품을 만나보세요.</p>
            </div>

            <form onSubmit={handleSubmit} className="flex flex-col gap-5" noValidate>
              <label className="flex flex-col gap-2 text-sm font-semibold" htmlFor="email">이메일
                <span className="relative"><Mail className="pointer-events-none absolute left-4 top-1/2 size-[18px] -translate-y-1/2 text-[#64748B]" /><input id="email" type="email" autoComplete="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="이메일을 입력해주세요" className="h-14 w-full rounded-2xl border border-[#D7E0EA] bg-white pl-12 pr-4 text-sm font-normal outline-none transition placeholder:text-[#94A3B8] focus:border-[#7FDBDA] focus:ring-4 focus:ring-[#7FDBDA]/25" /></span>
              </label>
              <label className="flex flex-col gap-2 text-sm font-semibold" htmlFor="password">비밀번호
                <span className="relative"><LockKeyhole className="pointer-events-none absolute left-4 top-1/2 size-[18px] -translate-y-1/2 text-[#64748B]" /><input id="password" type={showPassword ? 'text' : 'password'} autoComplete="current-password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="비밀번호를 입력해주세요" className="h-14 w-full rounded-2xl border border-[#D7E0EA] bg-white pl-12 pr-12 text-sm font-normal outline-none transition placeholder:text-[#94A3B8] focus:border-[#7FDBDA] focus:ring-4 focus:ring-[#7FDBDA]/25" /><button type="button" aria-label={showPassword ? '비밀번호 숨기기' : '비밀번호 보기'} onClick={() => setShowPassword(!showPassword)} className="absolute right-4 top-1/2 -translate-y-1/2 text-[#64748B] hover:text-[#25352f]">{showPassword ? <EyeOff className="size-[18px]" /> : <Eye className="size-[18px]" />}</button></span>
              </label>
              {error && <p role="alert" className="rounded-xl bg-[#FFF1F2] px-4 py-3 text-sm leading-5 text-[#BE123C]">{error}</p>}
              <button type="submit" disabled={isLoading} className="mt-2 flex h-14 items-center justify-center gap-2 rounded-2xl bg-gradient-to-r from-[#0A1F3D] to-[#2979FF] text-sm font-semibold text-white shadow-[0_10px_24px_rgba(41,121,255,0.24)] transition hover:from-[#12345F] hover:to-[#0066FF] disabled:cursor-not-allowed disabled:opacity-60">{isLoading ? '로그인 중...' : '로그인'} {!isLoading && <ArrowRight className="size-[17px]" />}</button>
            </form>
            {success && <p role="status" className="mt-5 rounded-xl bg-[#ECFEFF] px-4 py-3 text-sm leading-5 text-[#0F766E]">{success}</p>}
            <p className="mt-7 text-center text-sm text-[#64748B]">계정이 없으신가요? <button type="button" onClick={() => router.push('/signup')} className="font-semibold text-[#2979FF] hover:underline">회원가입</button></p>
            <p className="mt-4 text-center text-xs text-[#64748B]">로그인하면 Two게더의 서비스 이용약관에 동의하게 됩니다.</p>
            <button type="button" onClick={() => router.push('/')} className="mt-6 w-full text-center text-sm font-semibold text-[#2979FF] hover:underline">상품 목록으로 돌아가기</button>
          </div>
        </section>
      </div>
    </main>
  )
}

export { API_BASE_URL }
