/**
 * Frontend-only integration contract. These are expected backend routes, NOT implemented APIs.
 * The backend must provide HTTP-only secure session cookies, CSRF protection, server-side
 * role validation and OTP issuing/verification. Never store a token in localStorage.
 */
export type ApiSuccess = { success: true; message?: string }
export type RegistrationInput = {
  name: string
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
export type LoginInput = { identifier: string; password: string; rememberMe: boolean }

const apiBase = process.env.NEXT_PUBLIC_AUTH_API_BASE_URL?.trim().replace(/\/$/, '')
export class AuthApiError extends Error {}

async function send<T extends ApiSuccess>(path: string, payload: object): Promise<T> {
  if (!apiBase) throw new AuthApiError('Authentication is not configured yet. Connect a real backend first.')
  let response: Response
  try {
    response = await fetch(`${apiBase}${path}`, {
      method: 'POST',
      credentials: 'include',
      cache: 'no-store',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    })
  } catch {
    throw new AuthApiError('Authentication service cannot be reached. Please try again later.')
  }
  const result: unknown = await response.json().catch(() => null)
  const body = result && typeof result === 'object' ? result as Record<string, unknown> : {}
  if (!response.ok || body.success !== true) {
    const message = typeof body.message === 'string' && body.message.length < 250 ? body.message : 'The request could not be completed.'
    throw new AuthApiError(message)
  }
  return body as T
}

export const authApi = {
  login: (input: LoginInput) => send<ApiSuccess>('/auth/login', input),
  register: (input: RegistrationInput) => send<ApiSuccess>('/auth/register', input),
  forgotPassword: (email: string) => send<ApiSuccess & { retryAfterSeconds?: number }>('/auth/password/forgot', { email }),
  verifyOtp: (email: string, otp: string) => send<ApiSuccess>('/auth/password/verify-otp', { email, otp }),
  /** Requires a short-lived server-issued reset grant in an HttpOnly cookie. */
  resetPassword: (newPassword: string) => send<ApiSuccess>('/auth/password/reset', { newPassword }),
  async startGoogleAuth(): Promise<void> {
    const response = await send<ApiSuccess & { authorizationUrl?: string }>('/auth/oauth/google/start', { intent: 'sign_in' })
    if (!response.authorizationUrl) throw new AuthApiError('Google authentication is not configured.')
    let url: URL
    try { url = new URL(response.authorizationUrl) }
    catch { throw new AuthApiError('Authentication provider returned an invalid URL.') }
    if (url.protocol !== 'https:' && !(url.protocol === 'http:' && url.hostname === 'localhost')) {
      throw new AuthApiError('Authentication provider returned an unsafe URL.')
    }
    window.location.assign(url.toString())
  },
}
