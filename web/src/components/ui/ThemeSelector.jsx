import React from 'react';
import { Sun, Moon, Monitor } from 'lucide-react';
import { useTheme } from '../../context/ThemeContext';

export default function ThemeSelector({ className = '' }) {
  const { theme, setTheme } = useTheme();

  const options = [
    { id: 'light', label: 'Light', icon: Sun },
    { id: 'dark', label: 'Dark', icon: Moon },
    { id: 'system', label: 'System', icon: Monitor }
  ];

  return (
    <div
      role="group"
      aria-label="Theme selector"
      className={`inline-flex items-center p-0.5 rounded-xl border border-slate-200 dark:border-[#2D2D2D] bg-slate-100/80 dark:bg-[#141414] shadow-2xs ${className}`}
    >
      {options.map((opt) => {
        const Icon = opt.icon;
        const isSelected = theme === opt.id;

        return (
          <button
            key={opt.id}
            type="button"
            onClick={() => setTheme(opt.id)}
            aria-pressed={isSelected}
            aria-label={`${opt.label} mode`}
            title={`${opt.label} mode`}
            className={`flex items-center gap-1.5 px-2.5 py-1 text-xs font-semibold rounded-lg transition-all focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] dark:focus:ring-emerald-400 ${
              isSelected
                ? 'bg-white dark:bg-[#1C1C1C] text-slate-900 dark:text-[#F5F5F5] shadow-xs'
                : 'text-slate-600 dark:text-[#A3A3A3] hover:text-slate-900 dark:hover:text-[#F5F5F5]'
            }`}
          >
            <Icon className="w-3.5 h-3.5" aria-hidden="true" />
            <span className="hidden sm:inline">{opt.label}</span>
          </button>
        );
      })}
    </div>
  );
}
