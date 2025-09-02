import React, { useState } from 'react';
import { useTranslation } from 'node_modules/react-i18next';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { PasswordInput } from '@/components/ui/password-input';
import { Link } from 'react-router-dom';


const LoginPage: React.FC = () => {
  const { t } = useTranslation();
  const [password, setPassword] = useState("")

  const handleLogin = (event: React.FormEvent) => {
    event.preventDefault();
    // Mock login logic
    console.log('Login attempt');
    // In a real app, you would set some auth state here
    // For now, we'll just log and imagine navigation or state change
    alert(t('loginPage.loginAttemptMessage')); 
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-background p-4"> {/* Added flex-col and padding */}
      {/* Placeholder for SVG Logo */}
      <div className="mb-8">
        {/* TODO: Insert SVG Logo here. Example: <img src="/logo.svg" alt="Logo" className="h-16 w-auto" /> */}
        {/* Or direct SVG: */}
        {/* 
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 20" className="h-10 w-auto text-primary">
          <text x="50" y="15" fontSize="12" textAnchor="middle" fill="currentColor">YOUR LOGO</text>
        </svg>
        */}
        <img src="/logo.svg" alt="Logo" className="h-32 w-auto" />
      </div>
      <Card className="w-full max-w-sm">
        <CardHeader>
          <CardTitle className="text-2xl text-center">{t('loginPage.title')}</CardTitle> {/* Centered title */}
          <CardDescription>
            {t('loginPage.description')}
          </CardDescription>
        </CardHeader>
        <form onSubmit={handleLogin}>
          <CardContent className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="email">{t('loginPage.emailLabel')}</Label>
              <Input id="email" type="email" autoComplete="email" placeholder={t('loginPage.emailPlaceholder')} required />
            </div>
            <div className="grid gap-2">
              <div className="flex justify-between items-center">
                <Label htmlFor="password">{t('loginPage.passwordLabel')}</Label>
                <Link to="/forgot-password" className="text-sm font-medium text-primary underline-offset-4 hover:underline">
                  {t("loginPage.forgotPassword")}
                </Link>
              </div>
              <PasswordInput id="password" peekOnly={true} autoComplete="current-password" placeholder="********" required onChange={(e) => setPassword(e.target.value)} value={password} />
            </div>
          </CardContent>
          <CardFooter className="flex flex-col gap-4 mt-8">
            <Button type="submit" className="w-full">{t('loginPage.signInButton')}</Button>
            <div className="text-center text-sm text-muted-foreground">
              {t("login.noAccount")}{" "}
              <Link to="/signup" className="font-medium text-primary underline-offset-4 hover:underline">
                {t("login.signUpLink")}
              </Link>
            </div>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
};

export default LoginPage;
