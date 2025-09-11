import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Label } from '@/components/ui/label';
import { PasswordInput } from '@/components/ui/password-input';
import { useNavigate } from 'react-router-dom';
import { useChangePassword, useCurrentUser } from '@/hooks/useAuth';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { AlertCircle, CheckCircle2 } from 'lucide-react';

const ChangePasswordPage: React.FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { data: user } = useCurrentUser();
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [validationError, setValidationError] = useState("");
  
  const changePasswordMutation = useChangePassword();

  const validatePasswords = () => {
    if (newPassword.length < 4) {
      setValidationError(t('changePasswordPage.passwordTooShort'));
      return false;
    }
    if (newPassword !== confirmPassword) {
      setValidationError(t('changePasswordPage.passwordsDoNotMatch'));
      return false;
    }
    if (newPassword === currentPassword) {
      setValidationError(t('changePasswordPage.newPasswordSameAsCurrent'));
      return false;
    }
    setValidationError("");
    return true;
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    if (!currentPassword || !newPassword || !confirmPassword) {
      setValidationError(t('changePasswordPage.allFieldsRequired'));
      return;
    }

    if (!validatePasswords()) {
      return;
    }

    changePasswordMutation.mutate(
      { currentPassword, newPassword },
      {
        onSuccess: () => {
          // Redirect to dashboard after successful password change
          navigate('/dashboard');
        },
      }
    );
  };

  const isRequired = user?.passwordChangeRequired;

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-background p-4">
      <div className="mb-8">
        <img src="/logo.svg" alt="Logo" className="h-32 w-auto" />
      </div>
      
      {isRequired && (
        <Alert className="mb-6 max-w-md">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>
            {t('changePasswordPage.passwordChangeRequired')}
          </AlertDescription>
        </Alert>
      )}

      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="text-2xl text-center">
            {isRequired ? t('changePasswordPage.requiredTitle') : t('changePasswordPage.title')}
          </CardTitle>
          <CardDescription>
            {isRequired 
              ? t('changePasswordPage.requiredDescription') 
              : t('changePasswordPage.description')
            }
          </CardDescription>
        </CardHeader>
        <form onSubmit={handleSubmit}>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="currentPassword">{t('changePasswordPage.currentPasswordLabel')}</Label>
              <PasswordInput 
                id="currentPassword" 
                placeholder="********" 
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="newPassword">{t('changePasswordPage.newPasswordLabel')}</Label>
              <PasswordInput 
                id="newPassword" 
                placeholder={t('changePasswordPage.newPasswordPlaceholder')}
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">{t('changePasswordPage.confirmPasswordLabel')}</Label>
              <PasswordInput 
                id="confirmPassword" 
                placeholder={t('changePasswordPage.confirmPasswordPlaceholder')}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>

            {validationError && (
              <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertDescription>{validationError}</AlertDescription>
              </Alert>
            )}

            {changePasswordMutation.error && (
              <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertDescription>
                  {changePasswordMutation.error.message || t('changePasswordPage.genericError')}
                </AlertDescription>
              </Alert>
            )}

            {changePasswordMutation.isSuccess && (
              <Alert>
                <CheckCircle2 className="h-4 w-4" />
                <AlertDescription>
                  {t('changePasswordPage.successMessage')}
                </AlertDescription>
              </Alert>
            )}
          </CardContent>
          <CardFooter className="flex flex-col gap-4">
            <Button 
              type="submit" 
              className="w-full" 
              disabled={changePasswordMutation.isPending}
            >
              {changePasswordMutation.isPending 
                ? t('changePasswordPage.changingPassword') 
                : t('changePasswordPage.changePasswordButton')
              }
            </Button>
            {!isRequired && (
              <Button 
                type="button" 
                variant="outline" 
                className="w-full"
                onClick={() => navigate('/dashboard')}
              >
                {t('changePasswordPage.cancelButton')}
              </Button>
            )}
          </CardFooter>
        </form>
      </Card>
    </div>
  );
};

export default ChangePasswordPage;