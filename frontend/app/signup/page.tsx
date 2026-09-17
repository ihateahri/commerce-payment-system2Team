'use client'

import { FormEvent, useState } from 'react'
import { ArrowRight, Eye, EyeOff, IceCreamBowl, LockKeyhole, Mail, Phone, UserRound } from 'lucide-react'
import { useRouter } from 'next/navigation'

const API_BASE_URL = 'http://localhost:8080'

type SignupError = { message?: string }

export default function SignupPage() {
  const router = useRouter()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [phoneNumber, setPhoneNumber] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    if (!name.trim() || !email.trim() || !password || !phoneNumber.trim()) {
      setError('이메일, 비밀번호, 이름, 전화번호를 모두 입력해주세요.')
      return
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) {
      setError('올바른 이메일 형식을 입력해주세요.')
      return
    }

    setIsLoading(true)
    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/signup`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email.trim(), password, phoneNumber: phoneNumber.trim(), name: name.trim() }),
      })
      const data = (await response.json().catch(() => ({}))) as SignupError
      if (!response.ok) {
        setError(data.message || (response.status === 409 ? '이미 가입된 이메일입니다.' : '회원가입에 실패했습니다. 다시 시도해주세요.'))
        return
      }
      router.push('/login?signup=success')
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
          <div className="flex items-center gap-3 text-sm font-semibold tracking-[0.08em]"><span className="grid size-10 place-items-center rounded-full bg-[#2979FF]" aria-hidden="true"><IceCreamBowl className="size-7" strokeWidth={1.8} /></span>Two게더</div>
          <div className="max-w-lg pb-4"><p className="mb-7 text-sm font-medium uppercase tracking-[0.22em] text-[#7FDBDA]">COOL &amp; CONNECTED</p><h1 className="text-5xl font-medium leading-[1.08] tracking-[-0.05em] xl:text-6xl">당신의 취향을,<br />시원하게 연결하다.</h1><p className="mt-7 max-w-sm text-base leading-7 text-[#D6E4F7]">필요한 전자기기를 한곳에서 발견하고, Two게더와 함께 시원하게 연결해보세요.</p></div>
          <p className="text-xs text-[#8DBBFF]/70">© 2025 Two게더. All rights reserved.</p>
        </section>
        <section className="flex items-center justify-center px-6 py-12 sm:px-12"><div className="w-full max-w-[420px]">
          <div className="mb-12 lg:hidden"><div className="flex items-center gap-3 text-sm font-semibold tracking-[0.08em]"><span className="grid size-10 place-items-center rounded-full bg-[#2979FF]" aria-hidden="true"><IceCreamBowl className="size-7" strokeWidth={1.8} /></span>Two게더</div></div>
          <div className="mb-10"><p className="mb-3 text-sm font-semibold tracking-[0.08em] text-[#159A9C]">NEW ACCOUNT</p><h2 className="text-4xl font-medium tracking-[-0.05em]">새롭게 시작해요</h2><p className="mt-3 text-sm text-[#64748B]">Two게더에서 나만의 취향을 발견해보세요.</p></div>
          <form onSubmit={handleSubmit} className="flex flex-col gap-5" noValidate>
            <label className="flex flex-col gap-2 text-sm font-semibold" htmlFor="name">이름<span className="relative"><UserRound className="pointer-events-none absolute left-4 top-1/2 size-[18px] -translate-y-1/2 text-[#64748B]" /><input id="name" autoComplete="name" value={name} onChange={(e) => setName(e.target.value)} placeholder="이름을 입력해주세요" className="h-14 w-full rounded-2xl border border-[#D7E0EA] bg-white pl-12 pr-4 text-sm font-normal outline-none transition placeholder:text-[#94A3B8] focus:border-[#7FDBDA] focus:ring-4 focus:ring-[#7FDBDA]/25" /></span></label>
            <label className="flex flex-col gap-2 text-sm font-semibold" htmlFor="email">이메일<span className="relative"><Mail className="pointer-events-none absolute left-4 top-1/2 size-[18px] -translate-y-1/2 text-[#64748B]" /><input id="email" type="email" autoComplete="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="이메일을 입력해주세요" className="h-14 w-full rounded-2xl border border-[#D7E0EA] bg-white pl-12 pr-4 text-sm font-normal outline-none transition placeholder:text-[#94A3B8] focus:border-[#7FDBDA] focus:ring-4 focus:ring-[#7FDBDA]/25" /></span></label>
            <label className="flex flex-col gap-2 text-sm font-semibold" htmlFor="phoneNumber">전화번호<span className="relative"><Phone className="pointer-events-none absolute left-4 top-1/2 size-[18px] -translate-y-1/2 text-[#64748B]" /><input id="phoneNumber" type="tel" autoComplete="tel" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} placeholder="전화번호를 입력해주세요" className="h-14 w-full rounded-2xl border border-[#D7E0EA] bg-white pl-12 pr-4 text-sm font-normal outline-none transition placeholder:text-[#94A3B8] focus:border-[#7FDBDA] focus:ring-4 focus:ring-[#7FDBDA]/25" /></span></label>
            <label className="flex flex-col gap-2 text-sm font-semibold" htmlFor="password">비밀번호<span className="relative"><LockKeyhole className="pointer-events-none absolute left-4 top-1/2 size-[18px] -translate-y-1/2 text-[#64748B]" /><input id="password" type={showPassword ? 'text' : 'password'} autoComplete="new-password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="비밀번호를 입력해주세요" className="h-14 w-full rounded-2xl border border-[#D7E0EA] bg-white pl-12 pr-12 text-sm font-normal outline-none transition placeholder:text-[#94A3B8] focus:border-[#7FDBDA] focus:ring-4 focus:ring-[#7FDBDA]/25" /><button type="button" aria-label={showPassword ? '비밀번호 숨기기' : '비밀번호 보기'} onClick={() => setShowPassword(!showPassword)} className="absolute right-4 top-1/2 -translate-y-1/2 text-[#64748B]">{showPassword ? <EyeOff /> : <Eye />}</button></span></label>
            {error && <p role="alert" className="rounded-xl bg-[#FFF1F2] px-4 py-3 text-sm leading-5 text-[#BE123C]">{error}</p>}
            <button type="submit" disabled={isLoading} className="mt-2 flex h-14 items-center justify-center gap-2 rounded-2xl bg-gradient-to-r from-[#0A1F3D] to-[#2979FF] text-sm font-semibold text-white shadow-[0_10px_24px_rgba(41,121,255,0.24)] transition hover:from-[#12345F] hover:to-[#0066FF] disabled:cursor-not-allowed disabled:opacity-60">{isLoading ? '가입 중...' : '회원가입'} {!isLoading && <ArrowRight />}</button>
          </form>
          <p className="mt-9 text-center text-sm text-[#64748B]">이미 계정이 있으신가요? <button type="button" onClick={() => router.push('/')} className="font-semibold text-[#2979FF] hover:underline">로그인</button></p>
        </div></section>
      </div>
    </main>
  )
}

export { API_BASE_URL }
