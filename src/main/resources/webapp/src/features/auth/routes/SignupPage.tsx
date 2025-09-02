'use client';

import { useState, type FormEvent } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { signupUser } from '@/lib/api';
import { useTranslation } from 'node_modules/react-i18next';
import { getPasswordStrength } from '../../../passwordStrength';
import { PasswordInput } from '@/components/ui/password-input';
import { Link, useNavigate } from 'react-router-dom';

export function SignupPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const passwordStrength = getPasswordStrength(password);
  const strengthLabels = [
    t('signup.tooShort') || 'Too short',
    t('signup.weak') || 'Weak',
    t('signup.medium') || 'Medium',
    t('signup.strong') || 'Strong',
    t('signup.veryStrong') || 'Very strong',
  ];
  const strengthColors = ['bg-red-500', 'bg-red-500', 'bg-yellow-500', 'bg-green-500', 'bg-blue-600'];

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);

    if (!email || !password || !confirmPassword) {
      setError(t('common.requiredField'));
      return;
    }

    if (password !== confirmPassword) {
      setError(t('signup.passwordMismatch'));
      return;
    }

    setIsLoading(true);
    try {
      await signupUser(email, password);
      navigate({ to: '/login', search: { signedUp: true } });
    } catch (err) {
      setError(err instanceof Error ? err.message : t('signup.genericError'));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-background p-4">
      <div className="mb-8">
        <img src="/logo.svg" alt="Logo" className="h-32 w-auto" />
      </div>
      <Card className="w-full max-w-sm">
        <form onSubmit={handleSubmit}>
          <CardHeader>
            <CardTitle className="text-3xl text-center">{t('signup.title')}</CardTitle>
            <CardDescription className="py-4">{t('signup.description')}</CardDescription>
          </CardHeader>
          <CardContent className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="email">{t('signup.emailLabel')}</Label>
              <Input
                id="email"
                type="email"
                autoComplete="new-email"
                placeholder={t('signup.emailPlaceholder')}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="password">{t('signup.passwordLabel')}</Label>
              <PasswordInput
                id="password"
                autoComplete="new-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              {/* Password strength meter */}
              {password && (
                <div className="mt-0">
                  <div className="h-2 w-full rounded bg-muted">
                    <div
                      className={`h-2 rounded transition-all ${strengthColors[passwordStrength] || 'bg-gray-300'}`}
                      style={{ width: `${((passwordStrength + 1) / 5) * 100}%` }}
                    />
                  </div>
                  <div className="text-xs text-muted-foreground text-right">
                    {strengthLabels[passwordStrength] || t('signup.tooShort') || 'Too short'}
                  </div>
                </div>
              )}
            </div>
            <div className="grid gap-2">
              <Label htmlFor="confirm-password">{t('signup.confirmPasswordLabel')}</Label>
              <PasswordInput
                id="confirm-password"
                autoComplete="new-password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>
            {error && <p className="text-sm text-destructive">{error}</p>}
          </CardContent>
          <CardFooter className="flex flex-col gap-4 mt-8">
            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? t('signup.signingUp') : t('signup.signUpButton')}
            </Button>
            <div className="text-center text-sm text-muted-foreground">
              {t('signup.hasAccount')}{' '}
              <Link to="/login" className="font-medium text-primary underline-offset-4 hover:underline">
                {t('signup.signInLink')}
              </Link>
            </div>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
}
