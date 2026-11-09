'use client'
import { AtSign, Lock } from 'lucide-react'
import Link from 'next/link'
import { useEffect, useState, type FormEvent } from 'react'
import { AuthField } from './auth-field'
import { Checkbox, Divider, EmailButton, FormAlert, FormShell, FormStatus, GoogleButton, PrimaryButton, staggerStyle } from './form-parts'
import { useFormState } from './use-form-state'
import { emailError } from './validation'
import { authApi } from '@/services/auth-api'
const FIELDS = ['email', 'password'] as const
type Values = Record<(typeof FIELDS)[number], string>
function validate(values: Values) {
  const errors: Partial<Values> = {}
  errors.email = emailError(values.email)
  if (!values.password) errors.password = 'Password is required'
  return errors
}

export function LoginForm({ active, onSwitch, className }: { active: boolean; onSwitch: () => void; className?: string }) {
  const [error, setError] = useState<string | null>(null)
  const [status, setStatus] = useState<string | null>(null)
  const [pending, setPending] = useState(false)
  const form = useFormState(FIELDS, validate, () => { setError(null); setStatus(null) })
  const s = (i: number) => staggerStyle(i, 1)
  const email = form.field('email')

  useEffect(() => {
    if (typeof window === 'undefined') return
    const params = new URLSearchParams(window.location.search)
    const oauthStatus = params.get('oauth')
    if (oauthStatus === 'success') {
      setStatus('Successfully signed in with Google! Your dashboard will be available after it is built.')
      authApi.me().catch(() => null)
    } else if (oauthStatus === 'error') {
      const message = params.get('message')
      setError(message ? decodeURIComponent(message) : 'Google authentication was not completed.')
    }
  }, [])

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const element = event.currentTarget
    setStatus(null)
    if (!form.submit()) {
      requestAnimationFrame(() => element.querySelector<HTMLInputElement>('[aria-invalid="true"]')?.focus())
      return
    }
    setPending(true)
    setError(null)
    try {
      await authApi.login({ email: form.values.email.trim().toLowerCase(), password: form.values.password, rememberMe: Boolean(element.querySelector<HTMLInputElement>('[name="remember"]')?.checked) })
      setStatus('Sign in confirmed by the authentication service. Your dashboard will be available after it is built.')
    } catch (e) { setError(e instanceof Error ? e.message : 'Unable to sign in.') }
    finally { setPending(false) }
  }
  const handleGoogle = async () => {
    setPending(true); setError(null)
    try { await authApi.startGoogleAuth() }
    catch (e) { setError(e instanceof Error ? e.message : 'Google authentication is unavailable.'); setPending(false) }
  }
  return (
    <FormShell active={active} className={className}>
      <div className="mb-8 flex flex-col gap-2">
        <h2 className="stagger-item font-display text-3xl font-bold text-foreground" style={s(0)}>Welcome back</h2>
        <p className="stagger-item text-sm text-muted-foreground" style={s(1)}>Continue your journey with Ghoomi.</p>
      </div>
      <form noValidate onSubmit={handleSubmit} className="flex flex-col gap-5">
        <AuthField className="stagger-item" style={s(2)} {...email} invalid={Boolean(error)} type="email" label="Email Address" icon={AtSign} autoComplete="email" required />
        <div className="stagger-item flex flex-col" style={s(3)}>
          <AuthField {...form.field('password')} invalid={Boolean(error)} type="password" label="Password" icon={Lock} autoComplete="current-password" />
          <div className="mt-3 flex items-center justify-between">
            <Checkbox name="remember" label="Remember me" />
            <Link href="/forgot-password" className="text-xs text-muted-foreground transition-colors hover:text-primary">Forgot password?</Link>
          </div>
          <FormAlert message={error} />
        </div>
        <div className="mt-1 flex flex-col gap-3">
          <div className="stagger-item" style={s(4)}><PrimaryButton loading={pending}>Login</PrimaryButton></div>
          <div className="stagger-item" style={s(5)}><Divider /></div>
          <div className="stagger-item" style={s(6)}><GoogleButton disabled={pending} onClick={handleGoogle} /></div>
          <div className="stagger-item" style={s(7)}><EmailButton onClick={() => document.querySelector<HTMLInputElement>('input[name="email"]')?.focus()} /></div>
        </div>
        <FormStatus message={status} />
      </form>
      <p className="stagger-item mt-2 text-center text-sm text-muted-foreground" style={s(8)}>
        {"Don't have an account? "}
        <button type="button" onClick={onSwitch} className="font-semibold text-primary underline-offset-4 hover:underline focus-visible:underline">Create an account</button>
      </p>
    </FormShell>
  )
}
