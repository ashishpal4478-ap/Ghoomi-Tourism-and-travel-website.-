import type { Metadata, Viewport } from 'next'
import './globals.css'

export const metadata: Metadata = {
  title: 'GHOOMI | Explore More',
  description: 'Explore, plan and manage your journey with GHOOMI.',
  icons: { icon: '/brand/ghoomi-logo-navy.png' },
}
export const viewport: Viewport = { colorScheme: 'light', themeColor: '#f8fafc' }
export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return <html lang="en"><body className="antialiased">{children}</body></html>
}
