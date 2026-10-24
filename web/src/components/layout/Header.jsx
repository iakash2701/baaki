import React from 'react';
import { Link } from 'react-router-dom';
import { Star, RefreshCw } from 'lucide-react';
import { useData } from '../../data/DataContext';
import ThemeSelector from '../ui/ThemeSelector';

export default function Header({ title, subtitle, actionButton }) {
  const { resetDemo } = useData();

  const handleReset = () => {
    if (window.confirm('Reset all demo data back to Sharma Packaging initial hackathon showcase state?')) {
      resetDemo();
    }
  };

  return (
    <header className="sticky top-0 z-20 bg-[#F8FAF9]/90 dark:bg-[#0A0A0A]/90 backdrop-blur-md border-b border-[#E2E8F0] dark:border-[#2D2D2D] px-4 sm:px-8 py-3.5 transition-colors duration-200">
      <div className="flex items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-extrabold text-[#111C16] dark:text-[#F5F5F5] tracking-tight">
            {title}
          </h1>
          {subtitle && (
            <p className="text-xs sm:text-sm text-slate-600 dark:text-[#A3A3A3] font-medium mt-0.5">
              {subtitle}
            </p>
          )}
        </div>

        <div className="flex items-center gap-2 sm:gap-3">
          {actionButton}

          <ThemeSelector />

          <button
            onClick={handleReset}
            title="Reset Demo Data"
            aria-label="Reset Demo Data"
            className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-slate-700 dark:text-[#F5F5F5] bg-white dark:bg-[#141414] hover:bg-slate-50 dark:hover:bg-[#1C1C1C] border border-slate-200 dark:border-[#2D2D2D] rounded-xl transition-all shadow-xs focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
          >
            <RefreshCw className="w-3.5 h-3.5" aria-hidden="true" />
            <span className="hidden sm:inline">Reset Demo</span>
          </button>

          <Link
            to="/showcase"
            title="Judges Showcase"
            aria-label="Judges Showcase"
            className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-slate-700 dark:text-[#F5F5F5] hover:text-slate-900 bg-white dark:bg-[#141414] hover:bg-slate-50 dark:hover:bg-[#1C1C1C] border border-slate-200 dark:border-[#2D2D2D] rounded-xl transition-all shadow-xs focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
          >
            <Star className="w-3.5 h-3.5 text-amber-500" aria-hidden="true" />
            <span className="hidden sm:inline">Showcase</span>
          </Link>
        </div>
      </div>
    </header>
  );
}
