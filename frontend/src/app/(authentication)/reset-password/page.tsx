'use client'
import { useState, type FormEvent } from 'react'
import Link from 'next/link'
import { RecoveryShell } from '@/components/auth/recovery-shell'
import { RecoveryInput, RecoveryButton, RecoveryFeedback } from '@/components/auth/recovery-controls'
import { passwordError } from '@/components/auth/validation'
import { authApi } from '@/services/auth-api'
export default function ResetPasswordPage() {
  const [password, setPassword] = useState('')
  const [confirm, setConfirm] = useState('')
  const [pending, setPending] = useState(false)
  const [done, setDone] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const submit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault(); setError(null)
    const invalid = passwordError(password)
    if (invalid) { setError(invalid); return }
    if (password !== confirm) { setError('Passwords do not match.'); return }
    setPending(true)
    try {
      // This endpoint must reject requests lacking the server-issued verification grant.
      await authApi.resetPassword(password)
      sessionStorage.removeItem('ghoomi-reset-email')
      setDone(true)
      setPassword(''); setConfirm('')
    } catch (err) { setError(err instanceof Error ? err.message : 'Unable to reset password.') }
    finally { setPending(false) }
  }
  return <RecoveryShell title={done ? 'Password updated' : 'Create new password'} subtitle={done ? 'Your password was reset successfully by the authentication server.' : 'Choose a strong new password. A verified, unexpired reset authorization is required.'}>
    {done ? <Link href="/login" className="flex h-11 items-center justify-center rounded-full bg-primary font-semibold text-white">Return to Login</Link> :
      <form onSubmit={submit} className="space-y-5">
        <RecoveryInput label="New password" type="password" autoComplete="new-password" required value={password} onChange={e => setPassword(e.target.value)} />
        <RecoveryInput label="Confirm new password" type="password" autoComplete="new-password" required value={confirm} onChange={e => setConfirm(e.target.value)} />
        <p className="text-xs text-muted-foreground">At least 8 characters, uppercase, lowercase, number and symbol.</p>
        <RecoveryFeedback error={error} />
        <RecoveryButton loading={pending}>Change Password</RecoveryButton>
      </form>}
  </RecoveryShell>
}
