'use client'

import { Building2, UserRound } from 'lucide-react'
import { useRef, type KeyboardEvent } from 'react'
import { cn } from '@/lib/utils'

export type Role = 'user' | 'admin'

const OPTIONS: { value: Role; label: string; icon: typeof UserRound }[] = [
  { value: 'user', label: 'User', icon: UserRound },
  { value: 'admin', label: 'Admin', icon: Building2 },
]

export function RoleSelector({ value, onChange }: { value: Role; onChange: (role: Role) => void }) {
  const refs = useRef<(HTMLButtonElement | null)[]>([])
  const index = OPTIONS.findIndex((o) => o.value === value)

  const handleKeyDown = (event: KeyboardEvent<HTMLDivElement>) => {
    if (!['ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown'].includes(event.key)) return
    event.preventDefault()
    const next = index === 0 ? 1 : 0
    onChange(OPTIONS[next].value)
    refs.current[next]?.focus()
  }

  return (
    <div
      role="radiogroup"
      aria-label="Account type"
      onKeyDown={handleKeyDown}
      className="relative grid grid-cols-2 rounded-full border border-border bg-muted p-1"
    >
      <span
        aria-hidden="true"
        style={{ transform: `translate3d(${index * 100}%, 0, 0)` }}
        className="absolute inset-y-1 left-1 w-[calc(50%-0.25rem)] rounded-full bg-card shadow-[0_1px_3px_rgb(23_32_51/0.12)] ring-1 ring-border transition-transform duration-500 ease-[cubic-bezier(0.65,0,0.35,1)]"
      />
      {OPTIONS.map((option, i) => {
        const checked = option.value === value
        const Icon = option.icon
        return (
          <button
            key={option.value}
            ref={(el) => {
              refs.current[i] = el
            }}
            type="button"
            role="radio"
            aria-checked={checked}
            tabIndex={checked ? 0 : -1}
            onClick={() => onChange(option.value)}
            className={cn(
              'relative z-10 flex h-9 items-center justify-center gap-1.5 rounded-full px-2 text-xs font-semibold transition-colors duration-300 focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-primary',
              checked ? 'text-foreground' : 'text-muted-foreground hover:text-foreground',
            )}
          >
            <Icon aria-hidden="true" className={cn('size-3.5 transition-colors duration-300', checked && 'text-primary')} />
            {option.label}
          </button>
        )
      })}
    </div>
  )
}
