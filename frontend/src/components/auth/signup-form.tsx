
'use client'

import {
  Lock,
  Phone,
  ShieldCheck,
  User,
  Building2,
  MapPin,
} from 'lucide-react'

import {
  useId,
  useState,
  type CSSProperties,
  type FormEvent,
} from 'react'

import { AuthField } from './auth-field'
import {
  CATEGORIES,
  CategoryPicker,
  type CategoryId,
} from './category-picker'

import {
  Collapsible,
  Divider,
  EmailButton,
  FormAlert,
  FormShell,
  FormStatus,
  GoogleButton,
  PrimaryButton,
  staggerStyle,
} from './form-parts'

import { PasswordStrength } from './password-strength'
import { RoleSelector, type Role } from './role-selector'
import { useFormState } from './use-form-state'
import { passwordError } from './validation'
import { authApi } from '@/services/auth-api'

const FIELDS = ['name', 'phone', 'password', 'confirm'] as const

type Values = Record<(typeof FIELDS)[number], string>

function validate(values: Values) {
  const errors: Partial<Values> = {}

  if (!values.name.trim()) {
    errors.name = 'Name is required'
  }

  const phone = values.phone.trim()

  if (!phone) {
    errors.phone = 'Phone number is required'
  } else if (!/^[6-9]\d{9}$/.test(phone)) {
    errors.phone = 'Enter a valid 10-digit phone number'
  }

  errors.password = passwordError(values.password)

  if (!values.confirm) {
    errors.confirm = 'Confirm password is required'
  } else if (values.confirm !== values.password) {
    errors.confirm = 'Passwords do not match'
  }

  return errors
}

// Dynamic fields according to selected business category.
function getBusinessFields(category: CategoryId) {
  if (category === 'hotels') {
    return {
      name: 'Hotel / Property Name',
      type: 'Property Type',
    }
  }

  if (category === 'restaurants') {
    return {
      name: 'Restaurant Name',
      type: 'Cuisine Type',
    }
  }

  return {
    name: 'Tour Company Name',
    type: 'Tour Service Type',
  }
}

export function SignupForm({
  active,
  onSwitch,
  className,
}: {
  active: boolean
  onSwitch: () => void
  className?: string
}) {
  const [role, setRole] = useState<Role>('user')

  // A — Two-step Admin registration
  const [adminStep, setAdminStep] = useState<
    'category' | 'details'
  >('category')

  const [categories, setCategories] = useState<CategoryId[]>([])
  const [categoriesTouched, setCategoriesTouched] = useState(false)

  // A — Business-specific information
  const [businessName, setBusinessName] = useState('')
  const [businessType, setBusinessType] = useState('')
  const [businessCity, setBusinessCity] = useState('')

  const [passwordFocused, setPasswordFocused] = useState(false)
  const [status, setStatus] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [pending, setPending] = useState(false)

  const form = useFormState(FIELDS, validate, () => {
    setStatus(null)
    setError(null)
  })

  const strengthId = useId()
  const s = (i: number) => staggerStyle(i, -1)

  const isAdmin = role === 'admin'
  const selectedCategory = categories[0] ?? null

  const businessFields = selectedCategory
    ? getBusinessFields(selectedCategory)
    : null

  const categoryError =
    isAdmin &&
    categoriesTouched &&
    categories.length === 0
      ? 'Please select a business category'
      : undefined

  // B — Reset Admin flow when switching roles.
  const changeRole = (next: Role) => {
    if (next === role) return

    setRole(next)
    setAdminStep('category')
    setStatus(null)
    setError(null)
  }

  // B — Select only one business category.
  const toggleCategory = (id: CategoryId) => {
    setCategories([id])
    setCategoriesTouched(true)

    setBusinessName('')
    setBusinessType('')
    setBusinessCity('')
    setError(null)
  }

  const handleSubmit = async (
    event: FormEvent<HTMLFormElement>
  ) => {
    event.preventDefault()

    const element = event.currentTarget

    setStatus(null)
    setError(null)

    const fieldsValid = form.submit()

    if (isAdmin) {
      setCategoriesTouched(true)
    }

    if (
      !fieldsValid ||
      (isAdmin && categories.length === 0)
    ) {
      requestAnimationFrame(() =>
        element
          .querySelector<HTMLElement>('[aria-invalid="true"]')
          ?.focus()
      )
      return
    }

    // G — Validate category-specific fields.
    if (
      isAdmin &&
      (!selectedCategory ||
        !businessName.trim() ||
        !businessType.trim() ||
        !businessCity.trim())
    ) {
      setError('Please complete all business details.')
      return
    }

    setPending(true)

    try {
      // Public registration never grants admin privileges.
      // Business applications require backend approval.

      // H — Send selected category and business details.
      await authApi.register({
        name: form.values.name.trim(),
        phone: `+91${form.values.phone.trim()}`,
        password: form.values.password,
        accountType: isAdmin
          ? 'partner_application'
          : 'traveller',

        ...(isAdmin
          ? {
              businessCategories: categories,
              businessDetails: {
                businessName: businessName.trim(),
                businessType: businessType.trim(),
                city: businessCity.trim(),
              },
            }
          : {}),
      })

      setStatus(
        isAdmin
          ? 'Your business application was accepted by the server for review. No admin access has been granted.'
          : 'Your account registration was accepted by the server.'
      )
    } catch (e) {
      setError(
        e instanceof Error
          ? e.message
          : 'Unable to create account.'
      )
    } finally {
      setPending(false)
    }
  }

  const handleGoogle = async () => {
    setPending(true)
    setError(null)

    try {
      await authApi.startGoogleAuth()
    } catch (e) {
      setError(
        e instanceof Error
          ? e.message
          : 'Google authentication is unavailable.'
      )
      setPending(false)
    }
  }

  const swapDir = {
    ['--dir' as string]: isAdmin ? 1 : -1,
  } as CSSProperties

  return (
    <FormShell active={active} className={className}>
      {/* Existing heading */}
      <div className="mb-5 flex flex-col gap-2">
        <h2
          className="stagger-item font-display text-3xl font-bold text-foreground"
          style={s(0)}
        >
          Create your account
        </h2>

        <div className="stagger-item overflow-hidden" style={s(1)}>
          <p
            key={role}
            className="mode-swap text-sm text-muted-foreground"
            style={swapDir}
          >
            {isAdmin
              ? 'Request a business partnership and welcome travellers.'
              : 'Start planning better journeys with Ghoomi.'}
          </p>
        </div>
      </div>

      {/* Existing User / Admin selector */}
      <div className="stagger-item mb-3" style={s(2)}>
        <RoleSelector value={role} onChange={changeRole} />
      </div>

      {/* C — Show business categories FIRST */}
      {isAdmin && adminStep === 'category' && (
      <div
      className="stagger-item mt-5 space-y-5"
      style={s(3)}
      >
          <CategoryPicker
            selected={categories}
            onToggle={toggleCategory}
            error={categoryError}
          />

          <button
            type="button"
            disabled={!selectedCategory}
            onClick={() => setAdminStep('details')}
            className="h-11 w-full rounded-full bg-primary font-semibold text-primary-foreground transition-opacity disabled:cursor-not-allowed disabled:opacity-50"
          >
            Continue
          </button>
        </div>
      )}

      {/* D — Show User form immediately.
          Show Admin form AFTER category selection. */}
      {(!isAdmin || adminStep === 'details') && (
        <form
          noValidate
          onSubmit={handleSubmit}
          className="flex flex-col gap-3"
        >
          <AuthField
            className="stagger-item"
            style={s(3)}
            {...form.field('name')}
            label="Name"
            icon={User}
            autoComplete="name"
          />

          <AuthField
            className="stagger-item"
            style={s(4)}
            {...form.field('phone')}
            type="tel"
            label="Phone Number"
            icon={Phone}
            autoComplete="tel"
          />

          <div className="stagger-item" style={s(5)}>
            <AuthField
              {...form.field('password')}
              onFocus={() => setPasswordFocused(true)}
              onBlur={() => {
                setPasswordFocused(false)
                form.field('password').onBlur()
              }}
              aria-describedby={strengthId}
              type="password"
              label="Password"
              icon={Lock}
              autoComplete="new-password"
            />

            <Collapsible
              open={
                passwordFocused ||
                form.values.password.length > 0
              }
            >
              <PasswordStrength
                id={strengthId}
                password={form.values.password}
                className="pt-3"
              />
            </Collapsible>
          </div>

          <div className="stagger-item" style={s(6)}>
            <AuthField
              {...form.field('confirm')}
              type="password"
              label="Confirm Password"
              icon={ShieldCheck}
              autoComplete="new-password"
            />
          </div>

          {/* E — Old category section removed from here. */}

          {/* F — Category-specific business fields */}
          {isAdmin && selectedCategory && businessFields && (
            <div className="stagger-item space-y-2">
              <p className="pt-3 text-sm font-semibold text-foreground">
                {
                  CATEGORIES.find(
                    (c) => c.id === selectedCategory
                  )?.title
                }{' '}
                Business Details
              </p>

              <AuthField
                name="businessName"
                label={businessFields.name}
                icon={Building2}
                value={businessName}
                required
                onChange={(e) => {
                  setBusinessName(e.target.value)
                  setError(null)
                }}
              />

              <AuthField
                name="businessType"
                label={businessFields.type}
                icon={Building2}
                value={businessType}
                required
                onChange={(e) => {
                  setBusinessType(e.target.value)
                  setError(null)
                }}
              />

              <AuthField
                name="businessCity"
                label="City"
                icon={MapPin}
                value={businessCity}
                required
                onChange={(e) => {
                  setBusinessCity(e.target.value)
                  setError(null)
                }}
              />

              <button
                type="button"
                onClick={() => setAdminStep('category')}
                className="mt-2 text-sm font-medium text-primary hover:underline"
              >
                Change Business Category
              </button>
            </div>
          )}

          {/* Existing buttons */}
          <div className="mt-3 flex flex-col gap-3">
            <div className="stagger-item" style={s(7)}>
              <PrimaryButton loading={pending}>
                Create Account
              </PrimaryButton>
            </div>

            <div className="stagger-item" style={s(8)}>
              <Divider />
            </div>

            <div className="stagger-item" style={s(9)}>
              <GoogleButton
                disabled={pending}
                onClick={handleGoogle}
              />
            </div>

            <div className="stagger-item" style={s(10)}>
              <EmailButton
                onClick={() =>
                  document
                    .querySelector<HTMLInputElement>(
                      'input[name="email"]'
                    )
                    ?.focus()
                }
              />
            </div>
          </div>

          <FormAlert message={error} />
          <FormStatus message={status} />
        </form>
      )}

      {/* Existing Login navigation */}
      <p
        className="stagger-item mt-1 text-center text-sm text-muted-foreground"
        style={s(11)}
      >
        {'Already have an account? '}

        <button
          type="button"
          onClick={onSwitch}
          className="font-semibold text-primary underline-offset-4 hover:underline focus-visible:underline"
        >
          Login
        </button>
      </p>
    </FormShell>
  )
}
