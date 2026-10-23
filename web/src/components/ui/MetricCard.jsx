import React from 'react';

export default function MetricCard({
  title,
  amount,
  subtitle,
  icon: Icon,
  variant = 'default', // 'default', 'primary', 'secondary', 'error', 'amber'
  className = ''
}) {
  const variantStyles = {
    default: 'bg-white border-[#E2E8F0] text-[#111C16]',
    primary: 'bg-[#A3E4D7]/20 border-[#A3E4D7] text-[#0A2920]',
    secondary: 'bg-[#B2DFDB]/30 border-[#B2DFDB] text-[#004D40]',
    error: 'bg-red-50 border-red-200 text-red-900',
    amber: 'bg-amber-50 border-amber-200 text-amber-900'
  };

  const amountColorStyles = {
    default: 'text-[#111C16]',
    primary: 'text-[#1B4D3E]',
    secondary: 'text-[#00796B]',
    error: 'text-red-600',
    amber: 'text-[#D97706]'
  };

  return (
    <div
      className={`rounded-2xl border p-4 sm:p-5 transition-all shadow-sm hover:shadow-md ${variantStyles[variant] || variantStyles.default} ${className}`}
    >
      <div className="flex items-center justify-between mb-2">
        <span className="text-xs sm:text-sm font-medium text-slate-500">
          {title}
        </span>
        {Icon && (
          <div className="p-2 rounded-xl bg-slate-100/80 text-slate-700">
            <Icon className="w-4 h-4 sm:w-5 sm:h-5" />
          </div>
        )}
      </div>
      <div className={`text-xl sm:text-2xl font-bold tracking-tight ${amountColorStyles[variant] || amountColorStyles.default}`}>
        {amount}
      </div>
      {subtitle && (
        <div className="mt-1 text-xs text-slate-500 font-medium">
          {subtitle}
        </div>
      )}
    </div>
  );
}
