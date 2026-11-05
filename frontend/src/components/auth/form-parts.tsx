import { Check, CircleAlert, Loader2, Mail } from 'lucide-react'
import type { CSSProperties, ReactNode } from 'react'
import { cn } from '@/lib/utils'

export function staggerStyle(i: number, dir: 1 | -1): CSSProperties {
  return { ['--i' as string]: i, ['--dir' as string]: dir }
}


export function FormShell({
  active,
  className,
  children,
}: {
  active: boolean
  className?: string
  children: ReactNode
}) {
  return (
    <div
      data-active={active}
      inert={!active}
      className={cn(
        'ghoomi-auth-form stagger-group flex min-h-0 flex-col justify-start overflow-visible px-6 py-6 sm:px-10 md:overflow-y-auto md:px-10 md:py-4 lg:px-12',
        className
      )}
    >
      <div className="my-auto mx-auto w-full max-w-sm">
        {children}
      </div>
    </div>
  )
}


export function Collapsible({ open, className, children }: { open: boolean; className?: string; children: ReactNode }) {
  return (
    <div className={cn('collapsible', className)} data-open={open} inert={!open} aria-hidden={!open}>
      <div className="-m-1 min-h-0 overflow-hidden p-1">{children}</div>
    </div>
  )
}

export function PrimaryButton({ children, loading = false }: { children: ReactNode; loading?: boolean }) {
  return (
    <button
      type="submit"
      disabled={loading}
      aria-busy={loading}
      className="relative flex h-11 w-full items-center justify-center gap-2 overflow-hidden rounded-full bg-primary text-sm font-semibold text-primary-foreground shadow-[0_8px_20px_-8px_rgb(37_99_235/0.55)] transition-[translate,scale,box-shadow,background-color] duration-200 ease-out hover:-translate-y-0.5 hover:bg-primary/90 hover:shadow-[0_14px_28px_-10px_rgb(37_99_235/0.6)] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary active:translate-y-0 active:scale-[0.98] disabled:cursor-progress disabled:hover:translate-y-0"
    >
      {loading && <Loader2 aria-hidden="true" className="size-4 animate-spin" />}
      {children}
    </button>
  )
}

function GoogleMark() {
  return (
    <svg aria-hidden="true" viewBox="0 0 24 24" className="size-4">
      <path fill="#EA4335" d="M12 10.2v3.9h5.5c-.24 1.4-1.66 4.1-5.5 4.1-3.31 0-6-2.74-6-6.1s2.690-6.1 6-6.1c1.88 0 3.15.8 3.87 1.49l2.64-2.54C16.84 3.38 14.65 2.4 12 2.4 6.7 2.4 2.4 6.7 2.4 12s4.3 9.6 9.6 9.6c5.54 0 9.21-3.9 9.21-9.38 0-.63-.07-1.11-.15-1.6H12z" />
      <path fill="#4285F4" d="M21.21 12.22c0-.63-.07-1.11-.15-1.6H12v3.9h5.5a4.7 4.7 0 0 1-2.04 3.08l3.16 2.45c1.85-1.71 2.6-4.230 2.6-7.83z" />
      <path fill="#FBBC05" d="M6.32 14.3A6 6 0 0 1 6 12.1c0-.77.13-1.5.36-2.2L3.1 7.38A9.56 9.56 0 0 0 2.4 12c0 1.55.37 3.01 1.03 4.3l2.89-2z" />
      <path fill="#34A853" d="M12 21.6c2.6 0 4.78-.86 6.37-2.35l-3.160-2.45c-.85.59-1.98 1-3.21 1-2.46 0-4.55-1.66-5.3-3.9l-3.27 2.4C5.03 19.4 8.25 21.6 12 21.6z" />
    </svg>
  )
}

export function GoogleButton({ onClick, disabled = false }: { onClick?: () => void; disabled?: boolean }) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      className="flex h-11 w-full items-center justify-center gap-2.5 rounded-full border border-border bg-card text-sm font-medium text-foreground transition-[translate,scale,border-color,background-color,box-shadow] duration-200 ease-out hover:-translate-y-0.5 hover:border-primary/40 hover:bg-primary/[0.03] hover:shadow-[0_8px_20px_-12px_rgb(23_32_51/0.25)] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary active:translate-y-0 active:scale-[0.98]"
    >
      <GoogleMark />
      Continue with Gmail
    </button>
  )
}

export function EmailButton({ onClick }: { onClick: () => void }) {
  return (
    <button type="button" onClick={onClick}
      className="flex h-11 w-full items-center justify-center gap-2.5 rounded-full border border-border bg-card text-sm font-medium text-foreground transition-colors hover:border-primary/40 hover:bg-primary/[0.03] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary">
      <Mail aria-hidden="true" className="size-4" />
      Continue with Email
    </button>
  )
}

export function Divider() {
  return (
    <div className="flex items-center gap-3 text-[11px] tracking-[0.2em] text-muted-foreground uppercase">
      <span className="h-px flex-1 bg-border" />
      or
      <span className="h-px flex-1 bg-border" />
    </div>
  )
}

export function Checkbox({ name, label }: { name: string; label: string }) {
  return (
    <label className="group inline-flex cursor-pointer items-center gap-2 text-xs text-muted-foreground select-none">
      <input type="checkbox" name={name} className="sr-only" />
      <span
        aria-hidden="true"
        className="flex size-4 items-center justify-center rounded-[5px] border border-foreground/25 bg-card transition-all duration-200 group-hover:border-primary/60 group-has-checked:border-primary group-has-checked:bg-primary group-has-focus-visible:ring-2 group-has-focus-visible:ring-primary/30"
      >
        <Check
          strokeWidth={3.5}
          className="size-2.5 scale-50 text-primary-foreground opacity-0 transition-all duration-200 group-has-checked:scale-100 group-has-checked:opacity-100"
        />
      </span>
      {label}
    </label>
  )
}

export function FormAlert({ message }: { message: string | null }) {
  return (
    <Collapsible open={Boolean(message)}>
      <p
        role="alert"
        className="mt-3 flex items-center gap-2 rounded-lg border border-destructive/20 bg-destructive/[0.06] px-3 py-2.5 text-xs font-medium text-destructive"
      >
        <CircleAlert aria-hidden="true" className="size-4 shrink-0" />
        {message ?? 'Incorrect email or password'}
      </p>
    </Collapsible>
  )
}

export function FormStatus({ message }: { message: string | null }) {
  return (
    <p role="status" aria-live="polite" className="min-h-4 text-center text-xs font-medium text-secondary">
      {message}
    </p>
  )
}
