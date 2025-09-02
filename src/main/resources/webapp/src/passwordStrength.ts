export const PasswordStrength ={
  TooShort : 0,
  Weak: 1,
  Medium: 2,
  Strong: 3,
  VeryStrong: 4
} as const

export type PasswordStrength = (typeof PasswordStrength)[keyof typeof PasswordStrength]

export function getPasswordStrength(password: string): PasswordStrength {
  if (password.length < 8) return PasswordStrength.TooShort

  let score = 0
  if (/[A-Z]/.test(password)) score++

  if (/[^A-Za-z0-9]/.test(password)) score++
    
  if (/[0-9]/.test(password)) score++
  
  switch (score) {
    case 0:
      return PasswordStrength.Weak
    case 1:
      return PasswordStrength.Medium
    case 2:
      return PasswordStrength.Strong
    case 3:
      return PasswordStrength.VeryStrong
    default:
      return PasswordStrength.Weak
  }
}