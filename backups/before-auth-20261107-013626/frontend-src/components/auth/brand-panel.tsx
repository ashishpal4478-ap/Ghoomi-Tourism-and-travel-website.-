
import { Compass, Plane } from 'lucide-react'
import Image from 'next/image'
import type { CSSProperties } from 'react'
import { cn } from '@/lib/utils'
import type { AuthMode } from './auth-card'

const JOURNEY = ['Plan', 'Travel', 'Explore', 'Return']

// Different backgrounds for GHOOMI authentication pages
function getBackgroundImage(
  mode: AuthMode,
  recovery: boolean
) {
  if (recovery) {
    return '/images/ghoomi-journey.png'
  }

  if (mode === 'signup') {
    return '/images/ghoomi-signup-beach.webp'
  }

  return '/images/ghoomi-traveller.png'
}

function Wordmark({
  className,
}: {
  className?: string
}) {
  return (
    <div className={cn('flex items-center', className)}>
      <Image
        src="/brand/ghoomi-logo-white.png"
        alt="GHOOMI — Explore More"
        width={220}
        height={113}
        className="brand-logo object-contain object-left"
        priority
      />
    </div>
  )
}

function JourneyIndicator() {
  return (
    <ol
      aria-label="Your journey with Ghoomi"
      className="flex items-center gap-1.5 text-[10px] font-medium tracking-[0.16em] text-white/85 uppercase sm:gap-2 sm:tracking-[0.22em]"
    >
      {JOURNEY.map((step, index) => (
        <li key={step} className="flex items-center gap-2">
          <span className="flex items-center gap-1.5">
            <span
              aria-hidden="true"
              className={cn(
                'size-1.5 rounded-full',
                index === 0
                  ? 'bg-accent'
                  : 'bg-white/45'
              )}
            />
            {step}
          </span>

          {index < JOURNEY.length - 1 && (
            <span className="h-px w-2.5 bg-gradient-to-r from-white/60 to-white/15 sm:w-5" />
          )}
        </li>
      ))}
    </ol>
  )
}

function RouteDecor({
  align,
}: {
  align: 'left' | 'right'
}) {
  const pathId = `ghoomi-route-${align}`

  return (
    <div
      aria-hidden="true"
      className="pointer-events-none absolute inset-0"
    >
      <svg
        viewBox="0 0 300 220"
        className={cn(
          'h-full w-full overflow-visible',
          align === 'right' && '-scale-x-100'
        )}
      >
        <path
          id={pathId}
          d="M8 206 C 70 206, 70 150, 128 140 S 214 104, 226 60 S 268 18, 292 14"
          fill="none"
          stroke="white"
          strokeOpacity="0.55"
          strokeWidth="1.5"
          strokeLinecap="round"
          strokeDasharray="1 7"
        />

        <circle
          cx="8"
          cy="206"
          r="3.5"
          fill="white"
          fillOpacity="0.85"
        />

        <circle
          cx="292"
          cy="14"
          r="4"
          fill="none"
          stroke="white"
          strokeOpacity="0.85"
          strokeWidth="1.5"
        />

        <g className="route-pin">
          <circle r="11" className="fill-accent/25">
            <animate
              attributeName="r"
              values="7;13;7"
              dur="2.4s"
              repeatCount="indefinite"
            />
          </circle>

          <circle
            r="4.5"
            className="fill-accent"
            stroke="white"
            strokeWidth="1.5"
          />

          <animateMotion
            dur="10s"
            repeatCount="indefinite"
            keyPoints="0;1"
            keyTimes="0;1"
            calcMode="spline"
            keySplines="0.45 0 0.55 1"
          >
            <mpath href={`#${pathId}`} />
          </animateMotion>
        </g>
      </svg>

      <span
        className={cn(
          'float-soft absolute top-0 flex size-9 items-center justify-center rounded-full border border-white/25 bg-white/10 text-white backdrop-blur-sm',
          align === 'left' ? 'right-2' : 'left-2'
        )}
        style={
          {
            ['--float-delay' as string]: '0s',
          } as CSSProperties
        }
      >
        <Plane
          className={cn(
            'size-4',
            align === 'right' && '-scale-x-100'
          )}
        />
      </span>

      <span
        className={cn(
          'float-soft absolute bottom-6 flex size-8 items-center justify-center rounded-full border border-white/25 bg-white/10 text-white backdrop-blur-sm',
          align === 'left' ? 'right-10' : 'left-10'
        )}
        style={
          {
            ['--float-delay' as string]: '-3s',
          } as CSSProperties
        }
      >
        <Compass className="size-4" />
      </span>
    </div>
  )
}

function BrandCopy({
  align,
  active,
}: {
  align: 'left' | 'right'
  active: boolean
}) {
  const dir = align === 'left' ? -1 : 1

  const item = (i: number): CSSProperties => ({
    ['--i' as string]: i,
    ['--dir' as string]: dir,
  })

  return (
    <div
      data-active={active}
      aria-hidden={!active}
      className={cn(
        'stagger-group absolute inset-y-0 flex w-[40%] flex-col p-12',
        align === 'left'
          ? 'left-0 items-start text-left'
          : 'right-0 items-end text-right'
      )}
    >
      <div className="stagger-item" style={item(0)}>
        <Wordmark />
      </div>

      <div
        className={cn(
          'mt-10 flex flex-col gap-4',
          align === 'right' && 'items-end'
        )}
      >
        <p
          className="stagger-item text-xs font-semibold tracking-[0.3em] text-accent uppercase"
          style={item(1)}
        >
          {align === 'left'
            ? 'Welcome traveller'
            : 'New journey'}
        </p>

        <h2
          className="stagger-item font-display text-4xl leading-[1.05] font-bold text-balance text-white lg:text-5xl"
          style={item(2)}
        >
          Your journey starts here.
        </h2>

        <p
          className="stagger-item max-w-xs text-sm leading-relaxed text-pretty text-white/80"
          style={item(3)}
        >
          Plan, explore, book and manage your entire
          journey with Ghoomi.
        </p>
      </div>

      <div
        className="stagger-item relative my-6 min-h-24 w-full flex-1"
        style={item(4)}
      >
        <RouteDecor align={align} />
      </div>

      <div className="stagger-item" style={item(5)}>
        <JourneyIndicator />
      </div>
    </div>
  )
}

export function BrandPanel({
  mode,
  animate,
  recovery = false,
}: {
  mode: AuthMode
  animate: boolean
  recovery?: boolean
}) {
  const animation = animate
    ? mode === 'signup'
      ? 'to-signup'
      : 'to-login'
    : undefined

  const backgroundImage = getBackgroundImage(
    mode,
    recovery
  )

  return (
    <div className="absolute inset-0 hidden md:block">
      <div
        data-mode={mode}
        data-animate={animation}
        style={{ ['--e' as string]: '3px' }}
        className="brand-clip brand-edge absolute inset-0 bg-primary"
      />

      <div
        data-mode={mode}
        data-animate={animation}
        className="brand-clip absolute inset-0 overflow-hidden bg-ink"
      >
        <div className="brand-intro absolute inset-0">
          <div
            data-mode={mode}
            className="brand-media absolute inset-y-0 left-[-25%] w-[150%]"
          >
            <Image
              src={backgroundImage}
              alt={
                recovery
                  ? 'Scenic mountain lake and winding roads'
                  : mode === 'signup'
                    ? 'Tropical beach with turquoise water and palm trees'
                    : 'Beautiful alpine mountain landscape and winding road'
              }
              fill
              priority
              sizes="1560px"
              className={cn(
                'object-cover',
                mode === 'signup' && !recovery
                  ? 'object-center'
                  : 'object-center'
              )}
            />
          </div>

          {/* Preserve existing dark overlays */}
          <div className="absolute inset-0 bg-gradient-to-br from-ink/75 via-ink/20 to-primary/25" />

          <div className="absolute inset-0 bg-gradient-to-t from-ink/70 via-transparent to-ink/30" />
        </div>
      </div>

      <BrandCopy
        align="left"
        active={mode === 'login'}
      />

      <BrandCopy
        align="right"
        active={mode === 'signup'}
      />
    </div>
  )
}

// Responsive mobile branding
export function MobileBrandHeader({
  recovery = false,
  mode = 'login',
}: {
  recovery?: boolean
  mode?: AuthMode
}) {
  const backgroundImage = getBackgroundImage(
    mode,
    recovery
  )

  return (
    <div className="brand-intro relative h-56 overflow-hidden bg-ink md:hidden">
      <Image
        src={backgroundImage}
        alt="GHOOMI travel landscape"
        fill
        priority
        sizes="100vw"
        className="object-cover object-center"
      />

      <div className="absolute inset-0 bg-gradient-to-t from-ink/85 via-ink/30 to-ink/50" />

      <div className="relative flex h-full flex-col justify-between p-6">
        <Wordmark />

        <div className="flex flex-col gap-3">
          <p className="font-display text-2xl leading-tight font-bold text-balance text-white">
            Your journey starts here.
          </p>

          <JourneyIndicator />
        </div>
      </div>
    </div>
  )
}
