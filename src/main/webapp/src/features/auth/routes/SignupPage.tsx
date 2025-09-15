'use client';

import { useState, type FormEvent } from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useTranslation } from 'react-i18next';
import { PasswordInput } from '@/components/ui/password-input';
import { PasswordWithStrength } from '@/components/custom/password-with-strength';
import { Link, useNavigate } from 'react-router-dom';
import { useSignup } from '@/hooks/useAuth';

export function SignupPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [localError, setLocalError] = useState<string | null>(null);

  const signupMutation = useSignup();


  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setLocalError(null);

    // Prevent multiple submissions
    if (signupMutation.isPending) {
      return;
    }

    if (!email || !password || !confirmPassword) {
      setLocalError(t('common.requiredField'));
      return;
    }

    if (password !== confirmPassword) {
      setLocalError(t('signup.passwordMismatch'));
      return;
    }

    signupMutation.mutate(
      { email, password },
      {
        onSuccess: () => {
          navigate('/login?signedUp=true');
        },
      }
    );
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
              <PasswordWithStrength
                id="password"
                autoComplete="new-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
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
            {(localError || signupMutation.error) && (
              <p className="text-sm text-destructive">
                {localError || signupMutation.error?.message || t('signup.genericError')}
              </p>
            )}
          </CardContent>
          <CardFooter className="flex flex-col gap-4 mt-8">
            <Button type="submit" className="w-full" disabled={signupMutation.isPending}>
              {signupMutation.isPending ? t('signup.signingUp') : t('signup.signUpButton')}
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
