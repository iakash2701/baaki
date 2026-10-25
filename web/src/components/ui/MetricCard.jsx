import React from 'react';

export default function MetricCard({
  title,
  amount,
  subtitle,
  icon: Icon,
  variant = 'default', // 'default', 'primary', 'secondary', 'error', 'amber'
  className = ''
}) {
  const iconColorStyles = {
    default: 'bg-slate-100 dark:bg-[#1C1C1C] text-slate-700 dark:text-[#F5F5F5]',
    primary: 'bg-emerald-50 dark:bg-emerald-950/60 text-[#1B4D3E] dark:text-emerald-400',
    secondary: 'bg-teal-50 dark:bg-teal-950/60 text-[#00796B] dark:text-teal-400',
    error: 'bg-rose-50 dark:bg-rose-950/60 text-rose-700 dark:text-rose-400',
    amber: 'bg-amber-50 dark:bg-amber-950/60 text-[#B45309] dark:text-amber-400'
  };

  const amountColorStyles = {
    default: 'text-slate-900 dark:text-[#F5F5F5]',
    primary: 'text-[#1B4D3E] dark:text-emerald-400',
    secondary: 'text-[#00796B] dark:text-teal-400',
    error: 'text-rose-700 dark:text-rose-400',
    amber: 'text-[#B45309] dark:text-amber-400'
  };

  return (
    <div
      className={`rounded-2xl border border-slate-200 dark:border-[#2D2D2D] bg-white dark:bg-[#141414] p-4 sm:p-5 transition-all shadow-xs hover:shadow-sm hover:border-slate-300 dark:hover:border-[#404040] ${className}`}
    >
      <div className="flex items-center justify-between mb-2">
        <h3 className="text-xs sm:text-sm font-semibold text-slate-700 dark:text-[#A3A3A3]">
          {title}
        </h3>
        {Icon && (
          <div className={`p-2 rounded-xl ${iconColorStyles[variant] || iconColorStyles.default}`} aria-hidden="true">
            <Icon className="w-4 h-4 sm:w-5 sm:h-5" />
          </div>
        )}
      </div>
      <p role="text" className={`text-xl sm:text-2xl font-extrabold tracking-tight ${amountColorStyles[variant] || amountColorStyles.default}`}>
        {amount}
      </p>
      {subtitle && (
        <div className="mt-1 text-xs text-slate-600 dark:text-[#A3A3A3] font-medium">
          {subtitle}
        </div>
      )}
    </div>
  );
}
