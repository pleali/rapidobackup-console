import * as React from "react"
import {
  LayoutDashboard,
  Server,
  Database,
  Mail,
  Monitor,
  Package,
  FileCode,
  Settings2,
  GalleryVerticalEnd,
} from "lucide-react"

import { NavMain } from "./nav-main"
import { NavUser } from "./nav-user"
import { useCurrentUser, useIsAuthenticated } from "@/hooks/useAuth"
//
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from "@/components/ui/sidebar"

// Navigation data for RapidoBackup
const getNavData = () => ({
  navMain: [
    {
      title: "Dashboard",
      url: "/dashboard",
      icon: LayoutDashboard,
      isActive: false,
    },
    {
      title: "Agents",
      url: "/agents",
      icon: Server,
      isActive: false,
    },
    {
      title: "Backups",
      url: "/backups",
      icon: Database,
      isActive: false,
    },
    {
      title: "Office 365",
      url: "/office365",
      icon: Mail,
      isActive: false,
    },
    {
      title: "Remote Maintenance",
      url: "/remote",
      icon: Monitor,
      isActive: false,
    },
    {
      title: "Deployments",
      url: "/deployments",
      icon: Package,
      isActive: false,
    },
    {
      title: "Scripts",
      url: "/scripts",
      icon: FileCode,
      isActive: false,
    },
    {
      title: "Settings",
      url: "/settings",
      icon: Settings2,
      isActive: false,
    },
  ],
})

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  const user = useCurrentUser()
  const isAuthenticated = useIsAuthenticated()
  const navData = getNavData()

  // Transform user data for NavUser component
  const userData = React.useMemo(() => {
    if (!user) return null

    return {
      name: `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.login || '',
      email: user.email || '',
      avatar: user.imageUrl || "",
      role: user.role || '',
      passwordChangeRequired: user.passwordChangeRequired,
    }
  }, [user])

  if (!isAuthenticated || !userData) {
    return null
  }

  return (
    <Sidebar
      collapsible="icon"
      className="top-(--header-height) !h-[calc(100svh-var(--header-height))]"
      {...props}
    >
      {/* not useful, already have logo and product name in header 
      <SidebarHeader>
        <div className="flex items-center gap-2 px-4 py-2">
          <GalleryVerticalEnd className="h-6 w-6" />
          <span className="text-lg font-semibold">RapidoBackup</span>
        </div>
      </SidebarHeader>
      */}

      <SidebarContent>
        <NavMain items={navData.navMain} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={userData} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
}
