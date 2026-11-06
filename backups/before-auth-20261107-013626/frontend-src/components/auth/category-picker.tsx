'use client'

import { BedDouble, Check, ShieldCheck, Ticket, UtensilsCrossed } from 'lucide-react'
import { useId, type CSSProperties } from 'react'
import { cn } from '@/lib/utils'
import { FieldMessage } from './auth-field'

export const CATEGORIES = [
  { id: 'hotels', title: 'Hotels', description: 'Stays, rooms & resorts', icon: BedDouble },
  { id: 'restaurants', title: 'Restaurants', description: 'Dining & local food', icon: UtensilsCrossed },
  { id: 'tours', title: 'Tours', description: 'Tours, passes & services', icon: Ticket },
] as const

export type CategoryId = (typeof CATEGORIES)[number]['id']

export function CategoryPicker({
  selected,
  onToggle,
  error,
  className,
}: {
  selected: CategoryId[]
  onToggle: (id: CategoryId) => void
  error?: string
  className?: string
}) {
  const id = useId()
  const titleId = `${id}-title`
  const descId = `${id}-desc`
  const errorId = `${id}-error`

  return (
    <div role="group" aria-labelledby={titleId} aria-describedby={cn(descId, error && errorId)} className={className}>
      <div className="flex items-center justify-between gap-2">
        <p id={titleId} className="text-xs font-semibold text-foreground">
          Business category
        </p>
        <span className="inline-flex items-center gap-1 rounded-full bg-secondary/10 px-2 py-0.5 text-[10px] font-semibold text-secondary">
          <ShieldCheck aria-hidden="true" className="size-3" />
          Admin registration
        </span>
      </div>
      <p id={descId} className="mt-1 mb-3 text-[11px] leading-relaxed text-muted-foreground">
        Choose one business category to continue.
      </p>

      <div className="grid grid-cols-3 gap-2.5">
        {CATEGORIES.map((category, i) => {
          const checked = selected.includes(category.id)
          const Icon = category.icon
          return (
            <label
              key={category.id}
              data-checked={checked}
              style={{ ['--i' as string]: i } as CSSProperties}
              className={cn(
                'category-card relative flex cursor-pointer flex-col gap-2 rounded-xl border bg-card p-3 transition-[translate,scale,border-color,background-color,box-shadow] duration-300 ease-out',
                'hover:-translate-y-0.5 hover:shadow-[0_10px_24px_-14px_rgb(23_32_51/0.35)] has-focus-visible:ring-2 has-focus-visible:ring-primary/30',
                checked
                  ? 'scale-[1.03] border-primary bg-primary/[0.04] shadow-[0_10px_24px_-14px_rgb(37_99_235/0.55)]'
                  : error
                    ? 'border-destructive/40'
                    : 'border-border hover:border-primary/40',
              )}
            >
              <input
                type="radio"
                name="businessCategory" 
                value={category.id}
                checked={checked}
                onChange={() => onToggle(category.id)}
                className="sr-only"
              />
              <span
                aria-hidden="true"
                className={cn(
                  'absolute top-2 right-2 flex size-4 items-center justify-center rounded-full border transition-all duration-300',
                  checked ? 'border-primary bg-primary text-primary-foreground' : 'border-foreground/20',
                )}
              >
                <Check strokeWidth={3.5} className={cn('size-2.5 transition-[scale,opacity] duration-300', checked ? 'scale-100 opacity-100' : 'scale-50 opacity-0')} />
              </span>
              <span
                aria-hidden="true"
                className={cn(
                  'flex size-8 items-center justify-center rounded-lg transition-colors duration-300',
                  checked ? 'bg-primary text-primary-foreground' : 'bg-secondary/10 text-secondary',
                )}
              >
                <Icon className="size-4" />
              </span>
              <span className="flex flex-col gap-0.5">
                <span className="text-sm font-semibold text-foreground">{category.title}</span>
                <span className="text-[11px] leading-snug text-muted-foreground">{category.description}</span>
              </span>
            </label>
          )
        })}
      </div>
      <FieldMessage id={errorId} message={error} />
    </div>
  )
}
