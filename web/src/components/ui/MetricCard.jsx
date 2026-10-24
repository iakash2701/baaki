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
    default: 'bg-slate-100 text-slate-700',
    primary: 'bg-emerald-50 text-[#1B4D3E]',
    secondary: 'bg-teal-50 text-[#00796B]',
    error: 'bg-rose-50 text-rose-700',
    amber: 'bg-amber-50 text-[#B45309]'
  };

  const amountColorStyles = {
    default: 'text-slate-900',
    primary: 'text-[#1B4D3E]',
    secondary: 'text-[#00796B]',
    error: 'text-rose-700',
    amber: 'text-[#B45309]'
  };

  return (
    <div
      className={`rounded-2xl border border-slate-200 bg-white p-4 sm:p-5 transition-all shadow-xs hover:shadow-sm hover:border-slate-300 ${className}`}
    >
      <div className="flex items-center justify-between mb-2">
        <h3 className="text-xs sm:text-sm font-semibold text-slate-700">
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
        <div className="mt-1 text-xs text-slate-600 font-medium">
          {subtitle}
        </div>
      )}
    </div>
  );
}
