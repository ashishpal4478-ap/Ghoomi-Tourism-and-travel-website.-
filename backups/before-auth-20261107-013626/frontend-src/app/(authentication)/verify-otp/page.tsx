'use client'
import { useEffect, useState, type FormEvent } from 'react'
import { useRouter } from 'next/navigation'
import { RecoveryShell } from '@/components/auth/recovery-shell'
import { RecoveryInput, RecoveryButton, RecoveryFeedback } from '@/components/auth/recovery-controls'
import { authApi } from '@/services/auth-api'
export default function VerifyOtpPage() {
  const router = useRouter()
  const [email, setEmail] = useState('')
  const [otp, setOtp] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [message, setMessage] = useState<string | null>(null)
  const [pending, setPending] = useState(false)
  const [resendUntil, setResendUntil] = useState(0)
  const [secondsLeft, setSecondsLeft] = useState(0)
  useEffect(() => { setEmail(sessionStorage.getItem('ghoomi-reset-email') ?? '') }, [])
  useEffect(() => {
    if (!resendUntil) return
    const refresh = () => setSecondsLeft(Math.max(0, Math.ceil((resendUntil - Date.now()) / 1000)))
    refresh(); const id = window.setInterval(refresh, 1000); return () => window.clearInterval(id)
  }, [resendUntil])
  const submit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault(); setError(null); setMessage(null)
    if (!email) { setError('Start with the Forgot Password form to request a code.'); return }
    if (!/^\d{6}$/.test(otp)) { setError('Enter the six-digit OTP.'); return }
    setPending(true)
    try {
      // Backend verifies OTP and MUST issue a short-lived HttpOnly reset grant cookie.
      await authApi.verifyOtp(email, otp)
      router.push('/reset-password')
    } catch (err) { setError(err instanceof Error ? err.message : 'OTP verification failed.') }
    finally { setPending(false) }
  }
  const resend = async () => {
    if (pending || secondsLeft > 0 || !email) return
    setError(null); setMessage(null); setPending(true)
    try {
      const result = await authApi.forgotPassword(email)
      const sec = Number.isFinite(result.retryAfterSeconds) ? Math.max(0, Number(result.retryAfterSeconds)) : 60
      setResendUntil(Date.now() + sec * 1000)
      setMessage('The server accepted your OTP resend request. Check your email if it is eligible.')
    } catch (err) { setError(err instanceof Error ? err.message : 'Unable to resend OTP.') }
    finally { setPending(false) }
  }
  return <RecoveryShell title="Verify your email" subtitle="Enter the six-digit code from your email. The code is validated by the server, not this page.">
    <form onSubmit={submit} className="space-y-5">
      <p className="text-xs text-muted-foreground">{email ? `Verification requested for ${email}` : 'No password reset request found in this browser tab.'}</p>
      <RecoveryInput label="Six-digit OTP" type="text" inputMode="numeric" autoComplete="one-time-code" maxLength={6} pattern="[0-9]{6}" required value={otp} onChange={e => setOtp(e.target.value.replace(/\D/g, '').slice(0,6))} placeholder="Enter code" />
      <RecoveryFeedback error={error} message={message} />
      <RecoveryButton loading={pending}>Verify OTP</RecoveryButton>
    </form>
    <button type="button" disabled={!email || pending || secondsLeft > 0} onClick={resend} className="mt-5 w-full text-center text-sm font-semibold text-primary disabled:text-muted-foreground">
      {secondsLeft > 0 ? `Resend available in ${secondsLeft}s` : 'Resend OTP'}
    </button>
  </RecoveryShell>
}
