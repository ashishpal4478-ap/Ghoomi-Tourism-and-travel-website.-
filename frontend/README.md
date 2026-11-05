# GHOOMI — Authentication Frontend

This is production-oriented frontend code adapted from the uploaded `ghoomi-authentication-design.zip`, with the approved GHOOMI logo.
No fake accounts, hardcoded passwords, mock tokens, or success simulations are included.

## Install & run

From the repository root:

```powershell
cd frontend
npm install
npm run dev
```

Visit `http://localhost:3000/login` or `/signup`. The other pages are `/forgot-password`, `/verify-otp`, and `/reset-password`.

## Using with an existing frontend folder

Copy the contents of the `frontend` folder from this package into your project's existing `frontend` folder, preserving relative paths.
For conflicting files, back up your originals first. This bundle includes `src/app/globals.css`, `package.json`, `tsconfig.json`, `postcss.config.mjs`, and `next.config.mjs`. If your existing app has important customizations, merge those instead of overwriting them blindly.
This bundle does not touch your root documentation, SQL schema, or backend.

## Why authentication says not configured

The real backend hasn't been implemented. This app intentionally refuses to claim that login, signup, Google OAuth, or password reset succeeded without a verified API response. To connect your real backend, set `NEXT_PUBLIC_AUTH_API_BASE_URL` in `frontend/.env.local` and restart the dev server.

## API contract that backend must implement

All POST requests send JSON and use `credentials: 'include'`. On success return an HTTP 2xx JSON response with `{"success":true}`. On failures use a non-2xx response with a safe, user-facing `{"message":"..."}`. Configure allowed CORS origins and credentialed cookies correctly, as appropriate. Protect state-changing requests against CSRF.

| POST path | JSON request | Backend requirement |
| --- | --- | --- |
| `/auth/login` | `{identifier,password,rememberMe}` | Verify credentials; set secure HttpOnly session cookie |
| `/auth/register` | `{name,email,password,accountType,businessCategories?}` | Create real account; `partner_application` must never grant admin role |
| `/auth/password/forgot` | `{email}` | Safely send email OTP, generic public response, rate limits; may return `retryAfterSeconds` |
| `/auth/password/verify-otp` | `{email,otp}` | Verify OTP server-side; issue short-lived HttpOnly reset authorization cookie |
| `/auth/password/reset` | `{newPassword}` | Require verified reset cookie; update password, invalidate OTP/grant/sessions as needed |
| `/auth/oauth/google/start` | `{intent:'sign_in'}` | Validate requested flow, return `{success:true,authorizationUrl:'https://...'}` for real OAuth |

The actual backend might use different routes or different response shapes. If so, adapt ONLY `src/services/auth-api.ts`, and do not fabricate responses to make integration appear functional.

## Security details

The browser temporarily stores **only the email address** in `sessionStorage` between password recovery pages. It does **not** store a password, OTP, reset token, login token, or privileged role. All OTP verification and reset authorization must occur server-side. The Admin/Franchisee selection is a public **partner application** request and cannot grant privileges.

## Assets

- `public/brand/ghoomi-logo-original.png` — exact selected logo as supplied.
- `public/brand/ghoomi-logo-navy.png` — cropped transparent navy version.
- `public/brand/ghoomi-logo-white.png` — white version for scenic/dark panels.
- `public/images/` — original scenic images from your UI ZIP.

## Testing note

The source has been prepared for Next.js + TypeScript, but dependencies could not be installed in the creation environment due to unavailable network access. Please run `npm install`, `npm run typecheck` and `npm run build` on your machine. If your existing Next.js scaffold has more recent compatible package versions, you can keep those instead of downgrading to the ZIP's versions.
