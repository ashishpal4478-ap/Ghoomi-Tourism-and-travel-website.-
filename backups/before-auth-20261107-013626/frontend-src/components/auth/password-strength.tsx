
import { AlertTriangle, CheckCircle2 } from 'lucide-react'
import { cn } from '@/lib/utils'
import { PASSWORD_RULES } from './validation'

const LEVELS = {
  empty: {
    label: 'Not set',
    bars: 0,
    bar: 'bg-border',
    text: 'text-muted-foreground',
  },
  weak: {
    label: 'Weak',
    bars: 1,
    bar: 'bg-red-500',
    text: 'text-red-500',
  },
  fair: {
    label: 'Fair',
    bars: 2,
    bar: 'bg-amber-500',
    text: 'text-amber-500',
  },
  strong: {
    label: 'Strong',
    bars: 3,
    bar: 'bg-emerald-500',
    text: 'text-emerald-600',
  },
}

export function PasswordStrength({
  id,
  password,
  className,
}: {
  id: string
  password: string
  className?: string
}) {
  const met = PASSWORD_RULES.map(rule => rule.test(password))
  const score = met.filter(Boolean).length

  const level =
    LEVELS[
      score === 5
        ? 'strong'
        : score >= 3
          ? 'fair'
          : score > 0
            ? 'weak'
            : 'empty'
    ]

  // Display only one missing requirement at a time.
  // Prioritize password length and special characters.
  const missing = PASSWORD_RULES.filter(
    rule => !rule.test(password)
  )

  const missingRule =
    missing.find(rule => rule.id === 'length') ??
    missing.find(rule => rule.id === 'special') ??
    missing[0]

  return (
    <div id={id} className={className}>

      {/* Password Strength Indicator */}
      <div className="flex items-center gap-3">
        <div className="grid flex-1 grid-cols-3 gap-1.5">
          {[0, 1, 2].map(i => (
            <span
              key={i}
              className="h-1.5 overflow-hidden rounded-full bg-border"
            >
              <span
                className={cn(
                  'block h-full rounded-full transition-all duration-300',
                  i < level.bars
                    ? cn(level.bar, 'w-full')
                    : 'w-0'
                )}
              />
            </span>
          ))}
        </div>

        <span
          className={cn(
            'w-14 text-right text-xs font-semibold',
            level.text
          )}
        >
          {level.label}
        </span>
      </div>

      {/* Dynamic Password Warning */}
      {password.length > 0 && (
        <div
          role="status"
          aria-live="polite"
          className={cn(
            'mt-2 flex items-start gap-2 text-xs leading-relaxed',
            missingRule
              ? 'text-amber-600'
              : 'text-emerald-600'
          )}
        >
          {missingRule ? (
            <>
              <AlertTriangle className="mt-0.5 size-3.5 shrink-0" />
              <span>
                {missingRule.message}
              </span>
            </>
          ) : (
            <>
              <CheckCircle2 className="size-3.5 shrink-0" />
              <span>
                Strong password! All requirements are met.
              </span>
            </>
          )}
        </div>
      )}
    </div>
  )
}
