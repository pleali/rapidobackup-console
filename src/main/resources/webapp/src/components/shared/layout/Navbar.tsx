import React from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'node_modules/react-i18next';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { UserCircle, LogOut, Settings } from 'lucide-react'; // Icons

interface NavItemProps {
  href: string;
  label: string;
  active?: boolean;
}

const NavItem: React.FC<NavItemProps> = ({ href, label, active }) => {
  return (
    <li className="mr-2">
      <a
        href={href}
        className={`inline-block px-4 py-2 rounded-lg ${
          active
            ? 'bg-primary text-primary-foreground'
            : 'hover:bg-muted hover:text-foreground'
        }`}
      >
        {label}
      </a>
    </li>
  );
};

interface NavbarProps {
  userName?: string;
}

const Navbar: React.FC<NavbarProps> = ({ userName }) => {
  const { t } = useTranslation();
  // Mock logout function
  const handleLogout = () => {
    console.log('Logout clicked');
    // In a real app, this would clear auth state and redirect
    alert(t('navbar.logoutAttemptMessage'));
  };

  return (
    <nav className="bg-background border-b border-border px-6 py-3">
      <div className="flex items-center justify-between">
        <div className="flex items-center">
          <Link to="/dashboard" className="text-xl font-bold mr-10 hover:text-primary">
            {t('navbar.title')}
          </Link>
          {userName && ( // Only show nav items if user is logged in
            <ul className="flex">
              <NavItem href="/dashboard" label={t('navbar.dashboard')} active />
              <NavItem href="/agents" label={t('navbar.agents')} />
              <NavItem href="/backups" label={t('navbar.backups')} />
              <NavItem href="/office365" label={t('navbar.office365')} />
              <NavItem href="/remote" label={t('navbar.remoteMaintenance')} />
              <NavItem href="/deployments" label={t('navbar.deployments')} />
              <NavItem href="/scripts" label={t('navbar.scripts')} />
            </ul>
          )}
        </div>
        <div className="flex items-center">
          {userName ? (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                  <Avatar className="h-8 w-8">
                    <AvatarImage src="/placeholder-user.jpg" alt={userName} /> {/* Placeholder image */}
                    <AvatarFallback>
                      {userName.substring(0, 2).toUpperCase()}
                    </AvatarFallback>
                  </Avatar>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent className="w-56" align="end" forceMount>
                <DropdownMenuLabel className="font-normal">
                  <div className="flex flex-col space-y-1">
                    <p className="text-sm font-medium leading-none">{userName}</p>
                    <p className="text-xs leading-none text-muted-foreground">
                      {/* Placeholder for user email or role */}
                      demo@example.com 
                    </p>
                  </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                <DropdownMenuItem asChild>
                  <Link to="/profile" className="flex items-center">
                    <Settings className="mr-2 h-4 w-4" />
                    <span>{t('navbar.editProfile')}</span>
                  </Link>
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={handleLogout} className="flex items-center text-destructive hover:!text-destructive-foreground hover:!bg-destructive">
                  <LogOut className="mr-2 h-4 w-4" />
                  <span>{t('navbar.logout')}</span>
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <Button asChild size="sm">
              <Link to="/login">{t('navbar.login')}</Link>
            </Button>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
