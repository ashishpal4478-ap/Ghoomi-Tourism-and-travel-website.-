
'use client'

import { Building2, Lock, Mail, MapPin, Phone, ShieldCheck, User } from 'lucide-react'
import { useId, useState, type CSSProperties, type FormEvent } from 'react'
import { AuthField } from './auth-field'
import { CATEGORIES, CategoryPicker, type CategoryId } from './category-picker'
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
import { emailError, passwordError } from './validation'
import { authApi } from '@/services/auth-api'

const FIELDS = ['name', 'email', 'phone', 'password', 'confirm'] as const
type Values = Record<(typeof FIELDS)[number], string>
type AdminStep = 'category' | 'account' | 'business'
type SlideDirection = 'forward' | 'back'

function validate(values: Values) {
  const errors: Partial<Values> = {}

  if (!values.name.trim()) errors.name = 'Name is required'

  errors.email = emailError(values.email)

  const phone = values.phone.trim()
  if (!phone) errors.phone = 'Phone number is required'
  else if (!/^[6-9]\d{9}$/.test(phone)) {
    errors.phone = 'Enter a valid 10-digit phone number'
  }

  errors.password = passwordError(values.password)

  if (!values.confirm) errors.confirm = 'Confirm password is required'
  else if (values.confirm !== values.password) {
    errors.confirm = 'Passwords do not match'
  }

  return errors
}

function getBusinessFields(category: CategoryId) {
  if (category === 'hotels') {
    return { name: 'Hotel / Property Name', type: 'Property Type' }
  }

  if (category === 'restaurants') {
    return { name: 'Restaurant Name', type: 'Cuisine Type' }
  }

  return { name: 'Tour Company Name', type: 'Tour Service Type' }
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
  const [adminStep, setAdminStep] = useState<AdminStep>('category')
  const [slideDirection, setSlideDirection] = useState<SlideDirection>('forward')
  const [categories, setCategories] = useState<CategoryId[]>([])

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
  const fields = selectedCategory ? getBusinessFields(selectedCategory) : null
  const categoryTitle = CATEGORIES.find(
    (category) => category.id === selectedCategory
  )?.title

  const animationClass =
    slideDirection === 'back' ? 'admin-step-back' : 'admin-step-forward'

  const swapDir = {
    ['--dir' as string]: isAdmin ? 1 : -1,
  } as CSSProperties

  const changeRole = (next: Role) => {
    if (next === role || pending) return

    setRole(next)
    setAdminStep('category')
    setSlideDirection('forward')
    setStatus(null)
    setError(null)
  }

  // Selecting a business category automatically opens account details.
  const chooseCategory = (id: CategoryId) => {
    setCategories([id])
    setBusinessName('')
    setBusinessType('')
    setBusinessCity('')
    setError(null)
    setSlideDirection('forward')
    setAdminStep('account')
  }

  const previousStep = (step: AdminStep) => {
    setError(null)
    setStatus(null)
    setSlideDirection('back')
    setAdminStep(step)
  }

  const continueToBusiness = () => {
    setError(null)
    setStatus(null)

    if (!form.submit()) return

    setSlideDirection('forward')
    setAdminStep('business')
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (pending) return

    setError(null)
    setStatus(null)

    if (isAdmin && (adminStep !== 'business' || !selectedCategory)) {
      setError('Please choose a business category before registering.')
      return
    }

    if (!form.submit()) {
      if (isAdmin) {
        setSlideDirection('back')
        setAdminStep('account')
      }
      return
    }

    if (
      isAdmin &&
      (!businessName.trim() || !businessType.trim() || !businessCity.trim())
    ) {
      setError('Please complete all business details.')
      return
    }

    setPending(true)

    try {
      // Business registration creates a partner application.
      // It does not grant privileged administrator permissions.
      await authApi.register({
        name: form.values.name.trim(),
        email: form.values.email.trim().toLowerCase(),
        phone: `+91${form.values.phone.trim()}`,
        password: form.values.password,
        accountType: isAdmin ? 'partner_application' : 'traveller',

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
      setError(e instanceof Error ? e.message : 'Unable to create account.')
    } finally {
      setPending(false)
    }
  }

  const handleGoogle = async () => {
    if (pending) return

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

  return (
    <FormShell active={active} className={className}>
      <div className="mb-4 flex flex-col gap-2">
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

      <div className="stagger-item mb-3" style={s(2)}>
        <RoleSelector value={role} onChange={changeRole} />
      </div>

      {/* STEP 1 — BUSINESS CATEGORY */}
      {isAdmin && adminStep === 'category' && (
        <div className="stagger-item" style={s(3)}>
          <div key="category" className={`${animationClass} space-y-3`}>
            <p className="text-xs font-medium text-primary">
              Step 1 of 3 · Choose a business category
            </p>
            <p className="text-xs text-muted-foreground">
              Select Hotels, Restaurants, or Tours to continue.
            </p>
            <CategoryPicker
              selected={categories}
              onToggle={chooseCategory}
            />
          </div>
        </div>
      )}

      {(!isAdmin || adminStep !== 'category') && (
        <form
          noValidate
          onSubmit={handleSubmit}
          className="stagger-item flex flex-col gap-3"
          style={s(3)}
        >
          {/* USER SIGNUP / ADMIN STEP 2 */}
          {(!isAdmin || adminStep === 'account') && (
            <div
              key="account"
              className={`${isAdmin ? animationClass : ''} space-y-3`}
            >
              {isAdmin && (
                <p className="text-xs font-medium text-primary">
                  Step 2 of 3 · {categoryTitle} · Account details
                </p>
              )}

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
                {...form.field('email')}
  type="email"
  label="Email Address"
  icon={Mail}
  autoComplete="email"
  required
/>
<AuthField
  className="stagger-item"
  style={s(5)}
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
                  open={passwordFocused || form.values.password.length > 0}
                >
                  <PasswordStrength
                    id={strengthId}
                    password={form.values.password}
                    className="pt-2"
                  />
                </Collapsible>
              </div>

              <AuthField
                className="stagger-item"
                style={s(6)}
                {...form.field('confirm')}
                type="password"
                label="Confirm Password"
                icon={ShieldCheck}
                autoComplete="new-password"
              />
            </div>
          )}

          {/* ADMIN STEP 3 — BUSINESS DETAILS */}
          {isAdmin && adminStep === 'business' && selectedCategory && fields && (
            <div key="business" className={`${animationClass} space-y-3`}>
              <p className="text-xs font-medium text-primary">
                Step 3 of 3 · {categoryTitle} · Business details
              </p>

              <AuthField
                name="businessName"
                label={fields.name}
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
                label={fields.type}
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
                autoComplete="address-level2"
                onChange={(e) => {
                  setBusinessCity(e.target.value)
                  setError(null)
                }}
              />

              <button
                type="button"
                onClick={() => previousStep('account')}
                className="text-sm font-medium text-primary hover:underline"
              >
                Back to Account Details
              </button>
            </div>
          )}

          {/* WIZARD NAVIGATION */}
          {isAdmin && adminStep === 'account' ? (
            <div className="flex flex-col gap-2 pt-2">
              <button
                type="button"
                onClick={continueToBusiness}
                className="h-11 w-full rounded-full bg-primary text-sm font-semibold text-primary-foreground transition-opacity hover:opacity-90"
              >
                Continue to Business Details
              </button>

              <button
                type="button"
                onClick={() => previousStep('category')}
                className="text-sm font-medium text-primary hover:underline"
              >
                Back to Categories
              </button>
            </div>
          ) : (
            <div className="mt-2 flex flex-col gap-3">
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
                  onClick={() => {
                    setError('Email registration has not been configured yet.')
                  }}
                />
              </div>
            </div>
          )}

          <FormAlert message={error} />
          <FormStatus message={status} />
        </form>
      )}

      <p
        className="stagger-item mt-2 text-center text-sm text-muted-foreground"
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
