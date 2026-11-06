export const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export type PasswordRule = {
  id: string
  label: string
  message: string
  test: (value: string) => boolean
}

export const PASSWORD_RULES: PasswordRule[] = [
  { id: 'length', label: '8+ characters', message: 'Password must contain at least 8 characters', test: (v) => v.length >= 8 },
  { id: 'upper', label: 'Uppercase letter', message: 'Password must include an uppercase letter', test: (v) => /[A-Z]/.test(v) },
  { id: 'lower', label: 'Lowercase letter', message: 'Password must include a lowercase letter', test: (v) => /[a-z]/.test(v) },
  { id: 'number', label: 'Number', message: 'Password must include a number', test: (v) => /\d/.test(v) },
  { id: 'special', label: 'Special character', message: 'Password must include a special character', test: (v) => /[^A-Za-z0-9\s]/.test(v) },
]

export function emailError(value: string) {
  const email = value.trim()
  if (!email) return 'Email is required'
  if (!EMAIL_PATTERN.test(email)) return 'Please enter a valid email'
  return undefined
}

export function phoneError(value: string) {
  const phone = value.trim()
  if (!phone) return 'Phone number is required'
  if (!/^[6-9]\d{9}$/.test(phone)) return 'Enter a valid 10-digit phone number'
  return undefined
}
export function passwordError(value: string) {
  if (!value) return 'Password is required'
  return PASSWORD_RULES.find((rule) => !rule.test(value))?.message
}

export const wait = (ms: number) => new Promise<void>((resolve) => setTimeout(resolve, ms))
