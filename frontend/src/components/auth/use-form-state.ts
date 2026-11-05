'use client'

import { useState, type ChangeEvent } from 'react'

type Errors<F extends string> = Partial<Record<F, string>>

/**
 * Errors are derived live from values but only surfaced once a field is touched
 * (on blur or on submit), so corrections clear the error as the user types.
 */
export function useFormState<F extends string>(
  fields: readonly F[],
  validate: (values: Record<F, string>) => Errors<F>,
  onEdit?: () => void,
) {
  const [values, setValues] = useState(() => Object.fromEntries(fields.map((f) => [f, ''])) as Record<F, string>)
  const [touched, setTouched] = useState<Partial<Record<F, boolean>>>({})

  const errors = validate(values)
  const isValid = Object.values(errors).every((message) => !message)

  const field = (name: F) => ({
    name,
    value: values[name],
    onChange: (event: ChangeEvent<HTMLInputElement>) => {
      const next = event.target.value
      setValues((prev) => ({ ...prev, [name]: next }))
      onEdit?.()
    },
    onBlur: () => setTouched((prev) => (prev[name] ? prev : { ...prev, [name]: true })),
    error: touched[name] ? errors[name] : undefined,
    valid: Boolean(touched[name] && values[name] && !errors[name]),
  })

  const submit = () => {
    setTouched(Object.fromEntries(fields.map((f) => [f, true])) as Record<F, boolean>)
    return isValid
  }

  return { values, field, submit }
}
