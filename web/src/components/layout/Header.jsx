import React from 'react';
import { Link } from 'react-router-dom';
import { Star, RefreshCw } from 'lucide-react';
import { useData } from '../../data/DataContext';

export default function Header({ title, subtitle, actionButton }) {
  const { resetDemo } = useData();

  const handleReset = () => {
    if (window.confirm('Reset all demo data back to Sharma Packaging initial hackathon showcase state?')) {
      resetDemo();
    }
  };

  return (
    <header className="sticky top-0 z-20 bg-[#F8FAF9]/90 backdrop-blur-md border-b border-[#E2E8F0] px-4 sm:px-8 py-3.5 transition-all">
      <div className="flex items-center justify-between gap-4">
        <div>
          <h1 className="text-xl sm:text-2xl font-extrabold text-[#111C16] tracking-tight">
            {title}
          </h1>
          {subtitle && (
            <p className="text-xs sm:text-sm text-slate-500 font-medium mt-0.5">
              {subtitle}
            </p>
          )}
        </div>

        <div className="flex items-center gap-2 sm:gap-3">
          {actionButton}

          <button
            onClick={handleReset}
            title="Reset Demo Data"
            className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-slate-600 bg-white hover:bg-slate-50 border border-slate-200 rounded-xl transition-all shadow-xs"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">Reset Demo</span>
          </button>

          <Link
            to="/showcase"
            title="Judges Showcase"
            className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-bold text-[#0A2920] bg-[#A3E4D7] hover:bg-[#8ee0d0] rounded-xl transition-all shadow-xs"
          >
            <Star className="w-3.5 h-3.5 fill-[#0A2920]" />
            <span className="hidden sm:inline">Showcase</span>
          </Link>
        </div>
      </div>
    </header>
  );
}
