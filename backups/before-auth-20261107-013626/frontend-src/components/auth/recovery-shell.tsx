
import Link from 'next/link'
import type { ReactNode } from 'react'

export function RecoveryShell({
  title,
  subtitle,
  children,
}: {
  title: string
  subtitle: string
  children: ReactNode
}) {
  return (
    <div className="w-full">

      <h1 className="font-display text-3xl font-bold text-foreground">
        {title}
      </h1>

      <p className="mt-3 mb-6 text-sm leading-relaxed text-muted-foreground">
        {subtitle}
      </p>

      {children}

      <Link
        href="/login"
        className="mt-6 block text-center text-sm font-semibold text-primary transition-colors hover:underline focus-visible:underline"
      >
        Back to Login
      </Link>

    </div>
  )
}
