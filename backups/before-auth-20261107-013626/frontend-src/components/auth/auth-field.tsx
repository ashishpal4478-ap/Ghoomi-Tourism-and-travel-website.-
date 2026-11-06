'use client'

import { Check, CircleAlert, Eye, EyeOff, type LucideIcon } from 'lucide-react'
import { useId, useState, type InputHTMLAttributes } from 'react'
import { cn } from '@/lib/utils'

type AuthFieldProps = Omit<InputHTMLAttributes<HTMLInputElement>, 'placeholder'> & {
  label: string
  icon: LucideIcon
  error?: string
  valid?: boolean
  invalid?: boolean
}

export function FieldMessage({ id, message }: { id: string; message?: string }) {
  const [shown, setShown] = useState(message)
  if (message && message !== shown) setShown(message)

  return (
    <div className="field-message" data-show={Boolean(message)} aria-hidden={!message}>
      <div className="min-h-0 overflow-hidden">
        <p id={id} className="flex items-center gap-1 pt-1.5 text-[11px] font-medium text-destructive">
          <CircleAlert aria-hidden="true" className="size-3 shrink-0" />
          {shown}
        </p>
      </div>
    </div>
  )
}

export function AuthField({
  label,
  icon: Icon,
  error,
  valid,
  invalid,
  type = 'text',
  className,
  style,
  'aria-describedby': describedBy,
  ...props
}: AuthFieldProps) {
  const id = useId()
  const errorId = `${id}-error`
  const [revealed, setRevealed] = useState(false)
  const isPassword = type === 'password'
  const state = error || invalid ? 'error' : valid ? 'valid' : 'idle'

  return (
    <div className={cn('relative', className)} style={style}>
      <div className="relative">
        <input
          id={id}
          data-state={state}
          type={isPassword && revealed ? 'text' : type}
          placeholder=" "
          aria-invalid={state === 'error'}
          aria-describedby={cn(error && errorId, describedBy) || undefined}
          className={cn(
            'peer h-12 w-full border-0 border-b bg-transparent pt-4 text-sm text-foreground outline-none transition-[border-color] duration-300',
            isPassword ? 'pr-14' : 'pr-8',
            'border-foreground/20 hover:border-foreground/35 focus:border-foreground/10',
            'data-[state=error]:border-destructive/60 data-[state=valid]:border-secondary/50',
          )}
          {...props}
        />
        <label
          htmlFor={id}
          className={cn(
            'pointer-events-none absolute top-1/2 left-0 origin-left -translate-y-1/2 text-sm text-muted-foreground transition-all duration-300',
            'peer-focus:top-2 peer-focus:text-[11px] peer-focus:text-primary',
            'peer-[:not(:placeholder-shown)]:top-2 peer-[:not(:placeholder-shown)]:text-[11px]',
            'peer-data-[state=error]:text-destructive',
          )}
        >
          {label}
        </label>
        <span
          aria-hidden="true"
          className="absolute bottom-0 left-0 h-[1.5px] w-full origin-left scale-x-0 bg-primary transition-[scale,background-color] duration-500 ease-[cubic-bezier(0.65,0,0.35,1)] peer-focus:scale-x-100 peer-data-[state=error]:bg-destructive"
        />

        <span
          aria-hidden="true"
          className={cn('absolute bottom-3 size-4', isPassword ? 'right-7' : 'right-0.5')}
        >
          {!isPassword && (
            <Icon
              className={cn(
                'absolute inset-0 size-4 text-muted-foreground transition-[opacity,scale] duration-300',
                state === 'valid' ? 'scale-50 opacity-0' : 'scale-100 opacity-100',
                state === 'error' && 'text-destructive/80',
              )}
            />
          )}
          <Check
            strokeWidth={2.5}
            className={cn(
              'absolute inset-0 size-4 text-secondary transition-[opacity,scale] duration-300',
              state === 'valid' ? 'scale-100 opacity-100' : 'scale-50 opacity-0',
            )}
          />
        </span>

        {isPassword && (
          <button
            type="button"
            onClick={() => setRevealed((v) => !v)}
            aria-label={revealed ? `Hide ${label.toLowerCase()}` : `Show ${label.toLowerCase()}`}
            aria-pressed={revealed}
            className="absolute right-0 bottom-2.5 rounded p-0.5 text-muted-foreground transition-colors hover:text-primary focus-visible:text-primary focus-visible:outline-2 focus-visible:outline-primary"
          >
            {revealed ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
          </button>
        )}
      </div>
      <FieldMessage id={errorId} message={error} />
    </div>
  )
}
