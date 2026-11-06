import { AuthCard } from '@/components/auth/auth-card'
export default function AuthenticationLayout({ children }: { children: React.ReactNode }) {
  return (
    <main className="ghoomi-auth-viewport relative flex min-h-dvh items-center justify-center overflow-x-hidden px-4 py-4 md:h-dvh md:min-h-0 md:py-3">
      <div aria-hidden="true" className="pointer-events-none absolute inset-0 bg-[radial-gradient(55%_45%_at_30%_30%,rgb(37_99_235/0.08),transparent_70%),radial-gradient(45%_40%_at_75%_75%,rgb(15_118_110/0.07),transparent_70%)]" />
      <h1 className="sr-only">GHOOMI account access</h1>
     <AuthCard>{children}</AuthCard>
    </main>
  )
}
