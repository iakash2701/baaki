import React, { useState, useRef, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { LogOut, User as UserIcon, ShieldCheck } from 'lucide-react';

export default function UserProfileMenu() {
  const { user, displayName, avatarUrl, initials, signOut, isSupabaseConfigured } = useAuth();
  const [open, setOpen] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(e) {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  if (!user) return null;

  return (
    <div className="relative" ref={menuRef}>
      <button
        onClick={() => setOpen(!open)}
        aria-expanded={open}
        aria-haspopup="true"
        aria-label="User profile and settings"
        className="flex items-center gap-2 p-1.5 rounded-xl hover:bg-slate-100 dark:hover:bg-[#1C1C1C] transition-colors focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
      >
        {avatarUrl ? (
          <img src={avatarUrl} alt="" className="w-8 h-8 rounded-lg object-cover border border-slate-200 dark:border-[#2D2D2D]" />
        ) : (
          <div className="w-8 h-8 rounded-lg bg-[#1B4D3E] text-white flex items-center justify-center font-bold text-xs shadow-xs">
            {initials || <UserIcon className="w-4 h-4" />}
          </div>
        )}
      </button>

      {open && (
        <div
          role="menu"
          className="absolute left-0 bottom-full mb-2 w-64 bg-white dark:bg-[#141414] rounded-2xl shadow-xl border border-[#E2E8F0] dark:border-[#2D2D2D] p-3 z-50 animate-in fade-in zoom-in-95 duration-100 text-left"
        >
          <div className="flex items-center gap-3 p-2 border-b border-slate-100 dark:border-[#2D2D2D] mb-2">
            {avatarUrl ? (
              <img src={avatarUrl} alt="" className="w-10 h-10 rounded-xl object-cover border border-slate-200 dark:border-[#2D2D2D]" />
            ) : (
              <div className="w-10 h-10 rounded-xl bg-[#1B4D3E] text-white flex items-center justify-center font-bold text-sm shadow-xs">
                {initials || <UserIcon className="w-5 h-5" />}
              </div>
            )}
            <div className="min-w-0 flex-1">
              <p className="text-sm font-bold text-[#111C16] dark:text-[#F5F5F5] truncate">{displayName}</p>
              <p className="text-xs text-slate-500 dark:text-[#A3A3A3] truncate">{user.email || 'Demo Workspace'}</p>
            </div>
          </div>

          <div className="px-2 py-1.5 mb-2 bg-slate-50 dark:bg-[#1C1C1C] rounded-xl flex items-center gap-2 text-xs text-slate-600 dark:text-[#A3A3A3]">
            <ShieldCheck className="w-4 h-4 text-emerald-600 dark:text-emerald-400 flex-shrink-0" />
            <span className="truncate">
              {isSupabaseConfigured ? 'Isolated Cloud Auth' : 'Sandbox Isolated Session'}
            </span>
          </div>

          <button
            role="menuitem"
            onClick={() => {
              setOpen(false);
              signOut();
            }}
            className="w-full flex items-center gap-2.5 px-3 py-2 text-sm font-semibold text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-950/30 rounded-xl transition-colors focus:outline-none focus:ring-2 focus:ring-red-500"
          >
            <LogOut className="w-4 h-4" />
            <span>Sign Out</span>
          </button>
        </div>
      )}
    </div>
  );
}
