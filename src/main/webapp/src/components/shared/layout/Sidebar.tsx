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
  Settings
} from 'lucide-react';

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

interface SidebarProps {
  userName?: string;
}

const Sidebar: React.FC<SidebarProps> = ({ userName }) => {
  const location = useLocation();
  const currentPath = location.pathname;
  const { t } = useTranslation();

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
        {userName ? (
          <div className="flex flex-col space-y-2">
            <Link to="/profile" className="flex items-center px-4 py-2 rounded-lg hover:bg-muted">
              <User size={20} className="mr-3" />
              <span>{userName}</span>
            </Link>
            <Link to="/logout" className="flex items-center px-4 py-2 rounded-lg hover:bg-muted text-red-500">
              <LogOut size={20} className="mr-3" />
              <span>{t('sidebar.auth.logout')}</span>
            </Link>
          </div>
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
