'use client';

import { ModeToggle } from '@/components/mode-toogle';

export function SiteHeader() {
  return (
    <header className="flex sticky top-0 z-50 w-full items-center border-b bg-background">
      <div className="flex h-(--header-height) w-full items-center gap-2 px-4">
        <div className="flex items-center space-x-2">
          <div className="w-10 h-10 flex items-center justify-center">
            <img src="./logo.svg" alt="Logo" className=" text-white" />
          </div>
          <span className="text-xl font-bold">rapidobackup</span>
        </div>

        <div className="w-full sm:ml-auto sm:w-auto">
          <ModeToggle />
        </div>
      </div>
    </header>
  );
}
