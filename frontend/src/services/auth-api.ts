/**
 * Frontend auth API client.
 *
 * Security model:
 * - Session cookie is HttpOnly — the browser manages it automatically.
 * - CSRF: Spring Security sets XSRF-TOKEN cookie (readable by JS).
 *   Every mutating POST sends X-XSRF-TOKEN header back to the backend.
 * - credentials: 'include' is required on all cross-origin requests.
 * - Never store tokens in localStorage.
 */
export type ApiSuccess = { success: true; message?: string }

export type CurrentUser = {
  id: string
  email: string
  role: string
  displayName: string | null
  phoneE164: string | null
  accountType: string | null
  emailVerified: boolean
  createdAt: string | null
}

export type RegistrationInput = {
  name: string
  email: string
  phone: string
  password: string
  accountType: 'traveller' | 'partner_application'
  businessCategories?: string[]
  businessDetails?: {
    businessName: string
    businessType: string
    city: string
  }
}

export type LoginInput = { email: string; password: string; rememberMe: boolean }

const apiBase = process.env.NEXT_PUBLIC_AUTH_API_BASE_URL?.trim().replace(/\/$/, '')

export class AuthApiError extends Error {}

// ── CSRF helpers ─────────────────────────────────────────────────────────────

/**
 * Reads the XSRF-TOKEN cookie set by Spring Security.
 * Returns empty string if not present (GET requests don't need it).
 */
function getCsrfToken(): string {
  if (typeof document === 'undefined') return ''
  const match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]*)/)
  return match ? decodeURIComponent(match[1]) : ''
}

/**
 * Fetches the CSRF endpoint to initialize the XSRF-TOKEN cookie
 * before the first POST request in a session.
 */
async function ensureCsrfToken(): Promise<void> {
  if (!apiBase) return
  if (getCsrfToken()) return // Already initialized
  try {
    await fetch(`${apiBase}/auth/csrf`, {
      method: 'GET',
      credentials: 'include',
      cache: 'no-store',
    })
  } catch {
    // Ignore — the POST will fail with a network error anyway
  }
}

// ── Core fetch wrapper ────────────────────────────────────────────────────────

async function send<T extends ApiSuccess>(path: string, payload: object): Promise<T> {
  if (!apiBase)
    throw new AuthApiError('Authentication is not configured yet. Connect a real backend first.')

  // Initialize CSRF token before any mutating request
  await ensureCsrfToken()

  let response: Response
  try {
    response = await fetch(`${apiBase}${path}`, {
      method: 'POST',
      credentials: 'include',
      cache: 'no-store',
      headers: {
        'Content-Type': 'application/json',
        'X-XSRF-TOKEN': getCsrfToken(),
      },
      body: JSON.stringify(payload),
    })
  } catch {
    throw new AuthApiError('Authentication service cannot be reached. Please try again later.')
  }

  const result: unknown = await response.json().catch(() => null)
  const body =
    result && typeof result === 'object' ? (result as Record<string, unknown>) : {}

  if (!response.ok || body.success !== true) {
    const message =
      typeof body.message === 'string' && body.message.length < 250
        ? body.message
        : 'The request could not be completed.'
    throw new AuthApiError(message)
  }

  return body as T
}

async function get<T>(path: string): Promise<T> {
  if (!apiBase)
    throw new AuthApiError('Authentication is not configured yet. Connect a real backend first.')

  let response: Response
  try {
    response = await fetch(`${apiBase}${path}`, {
      method: 'GET',
      credentials: 'include',
      cache: 'no-store',
    })
  } catch {
    throw new AuthApiError('Authentication service cannot be reached. Please try again later.')
  }

  const result: unknown = await response.json().catch(() => null)
  const body =
    result && typeof result === 'object' ? (result as Record<string, unknown>) : {}

  if (!response.ok) {
    const message =
      typeof body.message === 'string' && body.message.length < 250
        ? body.message
        : 'The request could not be completed.'
    throw new AuthApiError(message)
  }

  return body as T
}

// ── Public API ────────────────────────────────────────────────────────────────

export const authApi = {
  login: (input: LoginInput) => send<ApiSuccess & { data?: CurrentUser }>('/auth/login', input),

  register: (input: RegistrationInput) =>
    send<ApiSuccess & { data?: CurrentUser }>('/auth/register', input),

  logout: () => send<ApiSuccess>('/auth/logout', {}),

  me: () => get<ApiSuccess & { data?: CurrentUser }>('/auth/me'),

  forgotPassword: (email: string) =>
    send<ApiSuccess & { retryAfterSeconds?: number }>('/auth/password/forgot', { email }),

  verifyOtp: (email: string, otp: string) =>
    send<ApiSuccess>('/auth/password/verify-otp', { email, otp }),

  /** Requires a short-lived server-issued reset grant in an HttpOnly cookie. */
  resetPassword: (newPassword: string) =>
    send<ApiSuccess>('/auth/password/reset', { newPassword }),

  startGoogleAuth(): void {
    const rawBase =
      process.env.NEXT_PUBLIC_AUTH_API_BASE_URL?.trim() || 'http://localhost:8080/api/v1'
    const backendOrigin = rawBase.replace(/\/api\/v1\/?$/, '')
    const targetUrl = `${backendOrigin}/oauth2/authorization/google`
    window.location.assign(targetUrl)
  },
}
