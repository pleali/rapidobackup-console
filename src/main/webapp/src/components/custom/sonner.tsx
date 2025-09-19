import { Toaster as Sonner } from "sonner"

type ToasterProps = React.ComponentProps<typeof Sonner>

/**
 * Custom Toaster component for React applications without Next.js
 *
 * This is a modified version of the ShadCN UI Sonner component that removes
 * the dependency on next-themes which is not needed in standard React apps.
 *
 * Original ShadCN component used useTheme() from next-themes for dynamic theming,
 * but we've simplified it to use a static "system" theme since we don't need
 * Next.js specific theme switching.
 */
const Toaster = ({ ...props }: ToasterProps) => {
  return (
    <Sonner
      theme="system"
      position="top-center"
      duration={3000}
      richColors
      expand
      visibleToasts={5}
      className="toaster group"
      style={{ zIndex: 99999 }}
      toastOptions={{
        classNames: {
          toast:
            "group toast group-[.toaster]:bg-background group-[.toaster]:text-foreground group-[.toaster]:border-border group-[.toaster]:shadow-lg",
          description: "group-[.toast]:text-muted-foreground",
          actionButton:
            "group-[.toast]:bg-primary group-[.toast]:text-primary-foreground",
          cancelButton:
            "group-[.toast]:bg-muted group-[.toast]:text-muted-foreground",
        },
      }}
      {...props}
    />
  )
}

export { Toaster }