import React from 'react';
import { AppSidebar } from '@/components/shared/layout/app-sidebar';
import { Breadcrumb, BreadcrumbList, BreadcrumbItem, BreadcrumbLink, BreadcrumbSeparator, BreadcrumbPage } from '@/components/ui/breadcrumb';
import { SidebarProvider, SidebarInset, SidebarTrigger } from '@/components/ui/sidebar';
import { Separator } from '@/components/ui/separator';
import { SiteHeader } from './site-header';

interface LayoutProps {
  children: React.ReactNode;
  userName?: string;
}

// const Layout: React.FC<LayoutProps> = ({ children, userName }) => {
//   return (
//     <div className="min-h-screen flex flex-col bg-background text-foreground">
//       <Navbar userName={userName} />
//       <main className="flex-1 p-6">
//         <div className="container mx-auto">
//           {children}
//         </div>
//       </main>
//       <footer className="py-4 px-6 border-t border-border">
//         <div className="container mx-auto text-center text-sm text-muted-foreground">
//           &copy; {new Date().getFullYear()} BUSS - Cloud Admin Console. All rights reserved.
//         </div>
//       </footer>
//     </div>
//   );
// };


const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
  <div className="[--header-height:calc(theme(spacing.14))]">
      <SidebarProvider className="flex flex-col">
        <SiteHeader />
        <div className="flex flex-1" >
        <AppSidebar />
        <SidebarInset>
          <header className="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-[[data-collapsible=icon]]/sidebar-wrapper:h-12">
            <div className="flex items-center gap-2 px-4">
              <SidebarTrigger className="-ml-1" />
              <Separator orientation="vertical" className="mr-2 h-4" />
              <Breadcrumb>
                <BreadcrumbList>
                  <BreadcrumbItem className="hidden md:block">
                    <BreadcrumbLink href="#">
                      Building Your Application
                    </BreadcrumbLink>
                  </BreadcrumbItem>
                  <BreadcrumbSeparator className="hidden md:block" />
                  <BreadcrumbItem>
                    <BreadcrumbPage>Data Fetching</BreadcrumbPage>
                  </BreadcrumbItem>
                </BreadcrumbList>
              </Breadcrumb>
            </div>

          </header>
          <main className="flex-1 p-6">
          <div className="container mx-auto">
            {children}
          </div>
          </main>

        </SidebarInset>
        </div>
      </SidebarProvider>
    </div> 
  );
};


export default Layout;
