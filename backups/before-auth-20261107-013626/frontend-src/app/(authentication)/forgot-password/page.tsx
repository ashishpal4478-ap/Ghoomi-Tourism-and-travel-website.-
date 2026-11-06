'use client'
import { useRouter } from 'next/navigation'
import { useState, type FormEvent } from 'react'
import { RecoveryShell } from '@/components/auth/recovery-shell'
import { RecoveryInput, RecoveryButton, RecoveryFeedback } from '@/components/auth/recovery-controls'
import { authApi } from '@/services/auth-api'
import { emailError } from '@/components/auth/validation'
export default function ForgotPasswordPage() {
  const router = useRouter()
  const [email, setEmail] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [pending, setPending] = useState(false)
  const submit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault(); setError(null)
    const invalid = emailError(email)
    if (invalid) { setError(invalid); return }
    setPending(true)
    try {
      await authApi.forgotPassword(email.trim())
      // Only email (not OTP/token/password) is kept in browser tab session storage.
      sessionStorage.setItem('ghoomi-reset-email', email.trim())
      router.push('/verify-otp')
    } catch (err) { setError(err instanceof Error ? err.message : 'Unable to request OTP.') }
    finally { setPending(false) }
  }
  return <RecoveryShell title="Forgot your password?" subtitle="Enter your email to request a one-time verification code. If your account is eligible, the server will arrange delivery.">
    <form onSubmit={submit} className="space-y-5"><RecoveryInput label="Email address" type="email" autoComplete="email" required value={email} onChange={e => setEmail(e.target.value)} placeholder="Enter your email address" /><RecoveryFeedback error={error} /><RecoveryButton loading={pending}>Send OTP</RecoveryButton></form>
  </RecoveryShell>
}
