import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { PasswordInput } from '@/components/ui/password-input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Separator } from '@/components/ui/separator';
import { useCurrentUser, useChangePassword } from '@/hooks/useAuth';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { AlertCircle, CheckCircle2, User, Shield, Globe } from 'lucide-react';
import { Link } from 'react-router-dom';

const EditProfilePage: React.FC = () => {
  const { t, i18n } = useTranslation();
  const user = useCurrentUser();
  
  // Profile form state
  const [firstName, setFirstName] = useState(user?.firstName || '');
  const [lastName, setLastName] = useState(user?.lastName || '');
  const [email, setEmail] = useState(user?.email || '');
  const [langKey, setLangKey] = useState(user?.langKey || 'en');
  
  // Password change state
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [passwordValidationError, setPasswordValidationError] = useState('');
  
  const changePasswordMutation = useChangePassword();

  const validatePasswords = () => {
    if (newPassword.length < 4) {
      setPasswordValidationError(t('editProfilePage.passwordTooShort'));
      return false;
    }
    if (newPassword !== confirmPassword) {
      setPasswordValidationError(t('editProfilePage.passwordsDoNotMatch'));
      return false;
    }
    if (newPassword === currentPassword) {
      setPasswordValidationError(t('editProfilePage.newPasswordSameAsCurrent'));
      return false;
    }
    setPasswordValidationError('');
    return true;
  };

  const handleProfileSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    // TODO: Implement profile update API call
    console.log('Profile update attempt');
    alert(t('editProfilePage.updateAttemptMessage'));
  };

  const handlePasswordSubmit = (event: React.FormEvent) => {
    event.preventDefault();

    if (!currentPassword || !newPassword || !confirmPassword) {
      setPasswordValidationError(t('editProfilePage.allFieldsRequired'));
      return;
    }

    if (!validatePasswords()) {
      return;
    }

    changePasswordMutation.mutate(
      { currentPassword, newPassword },
      {
        onSuccess: () => {
          setCurrentPassword('');
          setNewPassword('');
          setConfirmPassword('');
        },
      }
    );
  };

  const handleLanguageChange = (newLang: string) => {
    setLangKey(newLang);
    i18n.changeLanguage(newLang);
  };

  return (
    <div className="container mx-auto py-8 space-y-6">
      <div className="flex items-center gap-2 mb-6">
        <User className="h-6 w-6" />
        <h1 className="text-2xl font-bold">{t('editProfilePage.title')}</h1>
      </div>

      {/* Profile Information Card */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <User className="h-5 w-5" />
            <CardTitle>{t('editProfilePage.profileInformation')}</CardTitle>
          </div>
          <CardDescription>{t('editProfilePage.profileDescription')}</CardDescription>
        </CardHeader>
        <form onSubmit={handleProfileSubmit}>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="firstName">{t('editProfilePage.firstNameLabel')}</Label>
                <Input 
                  id="firstName" 
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="lastName">{t('editProfilePage.lastNameLabel')}</Label>
                <Input 
                  id="lastName" 
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">{t('editProfilePage.emailLabel')}</Label>
              <Input 
                id="email" 
                type="email" 
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="login">{t('editProfilePage.loginLabel')}</Label>
              <Input id="login" value={user?.login || ''} disabled />
              <p className="text-sm text-muted-foreground">
                {t('editProfilePage.loginHelp')}
              </p>
            </div>
            <div className="space-y-2">
              <Label htmlFor="role">{t('editProfilePage.roleLabel')}</Label>
              <Input id="role" value={user?.role || ''} disabled />
            </div>
          </CardContent>
          <CardFooter>
            <Button type="submit">{t('editProfilePage.saveProfileButton')}</Button>
          </CardFooter>
        </form>
      </Card>

      {/* Language Settings Card */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <Globe className="h-5 w-5" />
            <CardTitle>{t('editProfilePage.languageSettings')}</CardTitle>
          </div>
          <CardDescription>{t('editProfilePage.languageDescription')}</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-2">
            <Label htmlFor="language">{t('editProfilePage.languageLabel')}</Label>
            <Select value={langKey} onValueChange={handleLanguageChange}>
              <SelectTrigger>
                <SelectValue placeholder={t('editProfilePage.selectLanguage')} />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="en">English</SelectItem>
                <SelectItem value="fr">Français</SelectItem>
                <SelectItem value="es">Español</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Password Change Card */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <Shield className="h-5 w-5" />
            <CardTitle>{t('editProfilePage.passwordSettings')}</CardTitle>
          </div>
          <CardDescription>{t('editProfilePage.passwordDescription')}</CardDescription>
          {user?.passwordChangeRequired && (
            <Alert>
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>
                {t('editProfilePage.passwordChangeRequired')}
                <Link to="/change-password" className="ml-2 underline">
                  {t('editProfilePage.changePasswordLink')}
                </Link>
              </AlertDescription>
            </Alert>
          )}
        </CardHeader>
        <form onSubmit={handlePasswordSubmit}>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="currentPassword">{t('editProfilePage.currentPasswordLabel')}</Label>
              <PasswordInput 
                id="currentPassword" 
                placeholder="********"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="newPassword">{t('editProfilePage.newPasswordLabel')}</Label>
              <PasswordInput 
                id="newPassword" 
                placeholder={t('editProfilePage.newPasswordPlaceholder')}
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">{t('editProfilePage.confirmPasswordLabel')}</Label>
              <PasswordInput 
                id="confirmPassword" 
                placeholder={t('editProfilePage.confirmPasswordPlaceholder')}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
              />
            </div>

            {passwordValidationError && (
              <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertDescription>{passwordValidationError}</AlertDescription>
              </Alert>
            )}

            {changePasswordMutation.error && (
              <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertDescription>
                  {changePasswordMutation.error.message || t('editProfilePage.passwordChangeError')}
                </AlertDescription>
              </Alert>
            )}

            {changePasswordMutation.isSuccess && (
              <Alert>
                <CheckCircle2 className="h-4 w-4" />
                <AlertDescription>
                  {t('editProfilePage.passwordChangeSuccess')}
                </AlertDescription>
              </Alert>
            )}
          </CardContent>
          <CardFooter>
            <Button 
              type="submit" 
              disabled={changePasswordMutation.isPending}
            >
              {changePasswordMutation.isPending 
                ? t('editProfilePage.changingPassword')
                : t('editProfilePage.changePasswordButton')
              }
            </Button>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
};

export default EditProfilePage;
