import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
  LayoutDashboard,
  Server,
  Database,
  Mail,
  Monitor,
  Package,
  FileCode,
  LogOut,
  User,
  Settings,
  ChevronDown
} from 'lucide-react';
import { useCurrentUser, useIsAuthenticated, useLogout } from '@/hooks/useAuth';
import { UserRole } from '@/types/UserRole';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger
} from '@/components/ui/dropdown-menu';

interface SidebarItemProps {
  href: string;
  label: string;
  icon: React.ReactNode;
  active?: boolean;
}

const SidebarItem: React.FC<SidebarItemProps> = ({ href, label, icon, active }) => {
  return (
    <li className="mb-2">
      <Link
        to={href}
        className={`flex items-center px-4 py-3 rounded-lg transition-colors ${
          active
            ? 'bg-primary text-primary-foreground'
            : 'hover:bg-muted hover:text-foreground'
        }`}
      >
        <span className="mr-3">{icon}</span>
        {label}
      </Link>
    </li>
  );
};

const Sidebar: React.FC = () => {
  const location = useLocation();
  const currentPath = location.pathname;
  const { t } = useTranslation();
  const user = useCurrentUser();
  const isAuthenticated = useIsAuthenticated();
  const logoutMutation = useLogout();

  return (
    <div className="w-64 h-screen bg-background border-r border-border flex flex-col">
      <div className="p-6">
        <h1 className="text-xl font-bold">{t('sidebar.title')}</h1>
      </div>
      
      <nav className="flex-1 px-4 pb-4">
        <ul>
          <SidebarItem 
            href="/dashboard" 
            label={t('sidebar.nav.dashboard')}
            icon={<LayoutDashboard size={20} />} 
            active={currentPath === '/dashboard'} 
          />
          <SidebarItem 
            href="/agents" 
            label={t('sidebar.nav.agents')}
            icon={<Server size={20} />} 
            active={currentPath === '/agents'} 
          />
          <SidebarItem 
            href="/backups" 
            label={t('sidebar.nav.backups')}
            icon={<Database size={20} />} 
            active={currentPath === '/backups'} 
          />
          <SidebarItem 
            href="/office365" 
            label={t('sidebar.nav.office365')}
            icon={<Mail size={20} />} 
            active={currentPath === '/office365'} 
          />
          <SidebarItem 
            href="/remote" 
            label={t('sidebar.nav.remoteMaintenance')}
            icon={<Monitor size={20} />} 
            active={currentPath === '/remote'} 
          />
          <SidebarItem 
            href="/deployments" 
            label={t('sidebar.nav.deployments')}
            icon={<Package size={20} />} 
            active={currentPath === '/deployments'} 
          />
          <SidebarItem 
            href="/scripts" 
            label={t('sidebar.nav.scripts')}
            icon={<FileCode size={20} />} 
            active={currentPath === '/scripts'} 
          />
        </ul>
      </nav>
      
      <div className="p-4 border-t border-border">
        {isAuthenticated && user ? (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="w-full justify-start p-2 h-auto">
                <div className="flex items-center space-x-3 w-full">
                  <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                    <User size={20} className="text-primary" />
                  </div>
                  <div className="flex-1 text-left min-w-0">
                    <p className="text-sm font-medium truncate">
                      {`${user.firstName || ''} ${user.lastName || ''}`.trim() || user.login}
                    </p>
                    <div className="flex items-center space-x-2">
                      <p className="text-xs text-muted-foreground truncate">
                        {user.email}
                      </p>
                      <Badge variant="secondary" className="text-xs">
                        {user.role || UserRole.USER}
                      </Badge>
                    </div>
                  </div>
                  <ChevronDown size={16} className="text-muted-foreground" />
                </div>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-56">
              <DropdownMenuItem asChild>
                <Link to="/profile" className="flex items-center">
                  <User className="mr-2 h-4 w-4" />
                  <span>{t('sidebar.user.profile')}</span>
                </Link>
              </DropdownMenuItem>
              <DropdownMenuItem asChild>
                <Link to="/settings" className="flex items-center">
                  <Settings className="mr-2 h-4 w-4" />
                  <span>{t('sidebar.user.settings')}</span>
                </Link>
              </DropdownMenuItem>
              {user.passwordChangeRequired && (
                <DropdownMenuItem asChild>
                  <Link to="/change-password" className="flex items-center text-orange-600">
                    <Settings className="mr-2 h-4 w-4" />
                    <span>{t('sidebar.user.changePassword')}</span>
                  </Link>
                </DropdownMenuItem>
              )}
              <DropdownMenuSeparator />
              <DropdownMenuItem
                onClick={() => logoutMutation.mutate()}
                className="text-red-600 focus:text-red-600"
              >
                <LogOut className="mr-2 h-4 w-4" />
                <span>{t('sidebar.auth.logout')}</span>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        ) : (
          <div className="flex flex-col space-y-2">
            <Link to="/login" className="flex items-center justify-center px-4 py-2 rounded-lg bg-primary text-primary-foreground hover:bg-primary/90">
              {t('sidebar.auth.login')}
            </Link>
            <Link to="/register" className="flex items-center justify-center px-4 py-2 rounded-lg border border-input bg-background hover:bg-accent hover:text-accent-foreground">
              {t('sidebar.auth.register')}
            </Link>
          </div>
        )}
      </div>
    </div>
  );
};

export default Sidebar;
