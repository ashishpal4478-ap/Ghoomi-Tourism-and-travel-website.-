'use client'
import { Eye, EyeOff, LoaderCircle } from 'lucide-react'
import { useState, type InputHTMLAttributes, type ReactNode } from 'react'
export function RecoveryInput({ label, type = 'text', ...props }: { label: string } & InputHTMLAttributes<HTMLInputElement>) {
  const [visible, setVisible] = useState(false)
  const password = type === 'password'
  return <label className="block"><span className="mb-2 block text-xs font-semibold text-foreground">{label}</span>
    <span className="relative block"><input {...props} type={password && visible ? 'text' : type} aria-label={label} className="recovery-input pr-11 text-sm" />
    {password && <button type="button" aria-label={visible ? 'Hide password' : 'Show password'} onClick={() => setVisible(!visible)} className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-primary">{visible ? <EyeOff className="size-4" /> : <Eye className="size-4" />}</button>}</span>
  </label>
}
export function RecoveryButton({ loading, children }: { loading: boolean; children: ReactNode }) {
  return <button disabled={loading} type="submit" className="flex h-11 w-full items-center justify-center gap-2 rounded-full bg-primary text-sm font-semibold text-white hover:bg-blue-700 disabled:opacity-60">
    {loading && <LoaderCircle aria-hidden="true" className="size-4 animate-spin" />}{children}
  </button>
}
export function RecoveryFeedback({ error, message }: { error?: string | null; message?: string | null }) {
  return <div aria-live="polite" className="space-y-2">{error && <p role="alert" className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-xs text-red-700">{error}</p>}{message && <p role="status" className="rounded-lg border border-teal-200 bg-teal-50 px-3 py-2 text-xs text-teal-800">{message}</p>}</div>
}
