import React from 'react';
import { useTranslation } from 'react-i18next';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { PasswordInput } from '@/components/ui/password-input'; // Assuming this component exists

const EditProfilePage: React.FC = () => {
  const { t } = useTranslation();

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    // Mock profile update logic
    console.log('Profile update attempt');
    alert(t('editProfilePage.updateAttemptMessage'));
  };

  return (
    <div className="container mx-auto py-8">
      <Card className="max-w-2xl mx-auto">
        <CardHeader>
          <CardTitle>{t('editProfilePage.title')}</CardTitle>
          <CardDescription>{t('editProfilePage.description')}</CardDescription>
        </CardHeader>
        <form onSubmit={handleSubmit}>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="name">{t('editProfilePage.nameLabel')}</Label>
              <Input id="name" defaultValue="Demo User" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">{t('editProfilePage.emailLabel')}</Label>
              <Input id="email" type="email" defaultValue="demo@example.com" />
            </div>
            <hr className="my-4" />
            <div className="space-y-2">
              <Label htmlFor="currentPassword">{t('editProfilePage.currentPasswordLabel')}</Label>
              <PasswordInput id="currentPassword" placeholder="********" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="newPassword">{t('editProfilePage.newPasswordLabel')}</Label>
              <PasswordInput id="newPassword" placeholder={t('editProfilePage.newPasswordPlaceholder')} />
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirmPassword">{t('editProfilePage.confirmPasswordLabel')}</Label>
              <PasswordInput id="confirmPassword" placeholder={t('editProfilePage.confirmPasswordPlaceholder')} />
            </div>
          </CardContent>
          <CardFooter>
            <Button type="submit">{t('editProfilePage.saveChangesButton')}</Button>
          </CardFooter>
        </form>
      </Card>
    </div>
  );
};

export default EditProfilePage;
