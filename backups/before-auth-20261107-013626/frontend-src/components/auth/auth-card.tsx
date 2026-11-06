
'use client'

import {
  useEffect,
  useRef,
  useState,
  type ReactNode,
} from 'react'

import { usePathname, useRouter } from 'next/navigation'
import { cn } from '@/lib/utils'

import { BrandPanel, MobileBrandHeader } from './brand-panel'
import { LoginForm } from './login-form'
import { SignupForm } from './signup-form'
import { FormShell } from './form-parts'

export type AuthMode = 'login' | 'signup'

const RECOVERY_ROUTES = [
  '/forgot-password',
  '/verify-otp',
  '/reset-password',
]

export function AuthCard({
  children,
}: {
  children: ReactNode
}) {
  const router = useRouter()
  const pathname = usePathname()

  const isRecovery = RECOVERY_ROUTES.includes(pathname)
  const isSignup = pathname === '/signup'

  // Recovery uses the same side as Signup.
  const mode: AuthMode =
    isSignup || isRecovery ? 'signup' : 'login'

  const [hasSwitched, setHasSwitched] = useState(false)
  const previousPath = useRef(pathname)

  // Detect navigation, including Forgot Password links.
  useEffect(() => {
    if (previousPath.current !== pathname) {
      previousPath.current = pathname
      setHasSwitched(true)
    }
  }, [pathname])

  const switchTo = (next: AuthMode) => {
    const destination =
      next === 'login' ? '/login' : '/signup'

    if (pathname === destination) return

    setHasSwitched(true)
    router.push(destination)
  }

  return (
    <section
      aria-label={
        isRecovery
          ? 'Password Recovery'
          : isSignup
            ? 'Create Account'
            : 'Login'
      }
      className={cn(
        'ghoomi-auth-card relative w-full max-w-[1040px] overflow-hidden rounded-2xl border border-border bg-card shadow-[0_1px_2px_rgb(23_32_51/0.04),0_30px_70px_-30px_rgb(23_32_51/0.35)]',
        !hasSwitched && 'auth-intro'
      )}
    >
      {/* Existing animated mountain panel */}
      <BrandPanel
      mode={mode}
      animate={hasSwitched}
      recovery={isRecovery}
/>
      <MobileBrandHeader recovery={isRecovery} mode={mode} />
      <div className="ghoomi-auth-grid relative z-10 grid md:h-full md:min-h-0 md:grid-cols-2 md:grid-rows-[minmax(0,1fr)]">

        {/* Original Login Form */}
        <LoginForm
          active={!isSignup && !isRecovery}
          onSwitch={() => switchTo('signup')}
          className="col-start-1 row-start-1 md:col-start-2"
        />

        {/* Original Signup Form */}
        {!isRecovery && (
          <SignupForm
            active={isSignup}
            onSwitch={() => switchTo('login')}
            className="col-start-1 row-start-1"
          />
        )}

        {/* Recovery forms inside same sliding card */}
        {isRecovery && (
          <FormShell
            active={true}
            className="ghoomi-auth-form col-start-1 row-start-1"
          >
            <div
              key={pathname}
              className="recovery-enter"
            >
              {children}
            </div>
          </FormShell>
        )}

      </div>
    </section>
  )
}
