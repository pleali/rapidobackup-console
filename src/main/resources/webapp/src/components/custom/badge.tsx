import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "@/lib/utils"

const badgeVariants = cva(
  "inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2",
  {
    variants: {
      variant: {
        default:
          "border-transparent bg-primary text-primary-foreground hover:bg-primary/80",
        secondary:
          "border-transparent bg-secondary text-secondary-foreground hover:bg-secondary/80",
        destructive:
          "border-transparent bg-destructive text-destructive-foreground hover:bg-destructive/80",
        outline: "text-foreground border-border",
        "outline-success": "border-green-500 text-green-600 hover:bg-green-50 dark:text-green-400 dark:hover:bg-green-950/50",
        "outline-destructive": "border-red-500 text-red-600 hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-950/50",
        "outline-warning": "border-yellow-500 text-yellow-600 hover:bg-yellow-50 dark:text-yellow-400 dark:hover:bg-yellow-950/50",
        "outline-info": "border-blue-500 text-blue-600 hover:bg-blue-50 dark:text-blue-400 dark:hover:bg-blue-950/50",
        success:
          "border-transparent bg-green-500 text-white hover:bg-green-500/80",
        warning:
          "border-transparent bg-yellow-500 text-white hover:bg-yellow-500/80",
        info:
          "border-transparent bg-blue-500 text-white hover:bg-blue-500/80",
        ghost:
          "border-transparent bg-transparent text-foreground hover:bg-accent hover:text-accent-foreground",
      },
      size: {
        default: "px-2.5 py-0.5 text-xs",
        sm: "px-2 py-0.5 text-xs",
        lg: "px-3 py-1 text-sm",
        xl: "px-4 py-1.5 text-base",
      },
      shape: {
        default: "rounded-full",
        square: "rounded-md",
        pill: "rounded-full",
        none: "rounded-none",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
      shape: "default",
    },
  }
)

export interface BadgeProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof badgeVariants> {
  children: React.ReactNode
  dot?: boolean
  outline?: boolean
  responsive?: boolean
}

function Badge({ 
  className, 
  variant, 
  size, 
  shape, 
  dot, 
  outline, 
  responsive,
  children,
  ...props 
}: BadgeProps) {
  // Gestion du variant outline avec couleurs spécifiques
  let finalVariant = variant
  if (outline) {
    switch (variant) {
      case 'success':
        finalVariant = 'outline-success'
        break
      case 'destructive':
        finalVariant = 'outline-destructive'
        break
      case 'warning':
        finalVariant = 'outline-warning'
        break
      case 'info':
        finalVariant = 'outline-info'
        break
      default:
        finalVariant = 'outline'
    }
  }

  return (
    <div 
      className={cn(
        badgeVariants({ variant: finalVariant, size, shape }),
        // Ajout du point indicateur
        dot && "relative pl-6",
        // Badge responsive
        responsive && "text-xs sm:text-sm md:text-base",
        className
      )} 
      {...props}
    >
      {dot && (
        <span className="absolute left-2 top-1/2 h-2 w-2 -translate-y-1/2 rounded-full bg-current opacity-75" />
      )}
      {children}
    </div>
  )
}

// Composants spécialisés comme dans DaisyUI
export const BadgeSuccess = ({ children, ...props }: Omit<BadgeProps, 'variant'>) => (
  <Badge variant="success" {...props}>{children}</Badge>
)

export const BadgeError = ({ children, ...props }: Omit<BadgeProps, 'variant'>) => (
  <Badge variant="destructive" {...props}>{children}</Badge>
)

export const BadgeWarning = ({ children, ...props }: Omit<BadgeProps, 'variant'>) => (
  <Badge variant="warning" {...props}>{children}</Badge>
)

export const BadgeInfo = ({ children, ...props }: Omit<BadgeProps, 'variant'>) => (
  <Badge variant="info" {...props}>{children}</Badge>
)

export const BadgeNeutral = ({ children, ...props }: Omit<BadgeProps, 'variant'>) => (
  <Badge variant="secondary" {...props}>{children}</Badge>
)

export const BadgeGhost = ({ children, ...props }: Omit<BadgeProps, 'variant'>) => (
  <Badge variant="ghost" {...props}>{children}</Badge>
)

// Badge avec icône
export interface BadgeWithIconProps extends BadgeProps {
  icon?: React.ReactNode
  iconPosition?: 'left' | 'right'
}

export const BadgeWithIcon = ({ 
  icon, 
  iconPosition = 'left', 
  children, 
  className,
  ...props 
}: BadgeWithIconProps) => (
  <Badge 
    className={cn("flex items-center justify-center", className)} 
    {...props}
  >
    {icon && iconPosition === 'left' && (
      <span className="mr-1 flex h-3 w-3 items-center justify-center [&>svg]:h-3 [&>svg]:w-3">
        {icon}
      </span>
    )}
    <span className="flex items-center">{children}</span>
    {icon && iconPosition === 'right' && (
      <span className="ml-1 flex h-3 w-3 items-center justify-center [&>svg]:h-3 [&>svg]:w-3">
        {icon}
      </span>
    )}
  </Badge>
)

// Badge avec compteur
export interface CounterBadgeProps extends Omit<BadgeProps, 'children'> {
  count: number
  max?: number
  showZero?: boolean
}

export const CounterBadge = ({ 
  count, 
  max = 99, 
  showZero = false,
  ...props 
}: CounterBadgeProps) => {
  if (count === 0 && !showZero) return null
  
  const displayCount = count > max ? `${max}+` : count.toString()
  
  return (
    <Badge {...props}>
      {displayCount}
    </Badge>
  )
}

// Hook pour les badges animés
export const useAnimatedBadge = (trigger: boolean) => {
  const [animate, setAnimate] = React.useState(false)
  
  React.useEffect(() => {
    if (trigger) {
      setAnimate(true)
      const timer = setTimeout(() => setAnimate(false), 300)
      return () => clearTimeout(timer)
    }
  }, [trigger])
  
  return animate ? "animate-pulse" : ""
}

export { Badge, badgeVariants }