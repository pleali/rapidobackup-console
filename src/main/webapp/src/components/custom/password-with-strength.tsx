'use client'

import * as React from 'react'
import { PasswordInput } from '../ui/password-input'
import { cn } from '@/lib/utils'
import { useTranslation } from 'react-i18next'

interface PasswordStrength {
  score: number
  label: string
  color: string
}

const calculatePasswordStrength = (password: string, t: (key: string) => string): PasswordStrength => {
  if (!password) {
    return { score: 0, label: '', color: '' }
  }

  if (password.length < 8) {
    return { score: 10, label: t('passwordStrength.tooShort'), color: 'red-500' }
  }

  let score = 20 // Base score for minimum length
  const checks = {
    lowercase: /[a-z]/.test(password),
    uppercase: /[A-Z]/.test(password),
    numbers: /\d/.test(password),
    symbols: /[^A-Za-z0-9]/.test(password)
  }

  if (checks.lowercase) score += 20
  if (checks.uppercase) score += 20
  if (checks.numbers) score += 20
  if (checks.symbols) score += 20

  // Bonus for longer passwords
  if (password.length >= 12) score += 10
  if (password.length >= 16) score += 10

  // Cap at 100
  score = Math.min(100, score)

  let label = ''
  let color = ''

  if (score < 40) {
    label = t('passwordStrength.weak')
    color = 'red-500'
  } else if (score < 70) {
    label = t('passwordStrength.medium')
    color = 'yellow-500'
  } else if (score < 90) {
    label = t('passwordStrength.strong')
    color = 'green-500'
  } else {
    label = t('passwordStrength.veryStrong')
    color = 'blue-600'
  }

  return { score, label, color }
}

type PasswordWithStrengthProps = React.ComponentProps<typeof PasswordInput> & {
  showStrength?: boolean
}

const PasswordWithStrength = React.forwardRef<HTMLInputElement, PasswordWithStrengthProps>(
  ({ className, showStrength = true, value, ...props }, ref) => {
    const { t } = useTranslation()
    
    const strength = React.useMemo(() => {
      return calculatePasswordStrength(value as string || '', t)
    }, [value, t])

    return (
      <div className="space-y-2">
        <PasswordInput
          className={className}
          value={value}
          ref={ref}
          {...props}
        />
        {showStrength && value && (
          <div className="space-y-1">
              <div className="flex justify-between text-xs">
                     <span className="text-muted-foreground">{t('passwordStrength.label')}</span>
                     <span className={cn("h-full transition-all duration-300", "text-" + strength.color)}>
                       {strength.label}
                     </span>
                   </div>
            <div className="h-2 bg-muted rounded-full overflow-hidden">
              <div
                className={cn("h-full transition-all duration-300", "bg-" + strength.color)}
                style={{ width: `${strength.score}%` }}
              />
            </div>
            {/* <div className="text-xs text-muted-foreground text-right">
              {strength.label}
            </div> */}
          </div>
        )}
      </div>
    )
  }
)
PasswordWithStrength.displayName = 'PasswordWithStrength'

export { PasswordWithStrength }