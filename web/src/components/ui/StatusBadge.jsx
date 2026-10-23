import React from 'react';

export default function StatusBadge({ status, className = '' }) {
  const normalized = (status || '').toUpperCase();

  const config = {
    PAID: {
      label: 'PAID',
      bg: 'bg-emerald-50 text-emerald-700 border-emerald-200'
    },
    PARTIALLY_PAID: {
      label: 'PARTIAL',
      bg: 'bg-amber-50 text-amber-700 border-amber-200'
    },
    OVERDUE: {
      label: 'OVERDUE',
      bg: 'bg-rose-50 text-rose-700 border-rose-200'
    },
    SENT: {
      label: 'PENDING',
      bg: 'bg-sky-50 text-sky-700 border-sky-200'
    },
    DRAFT: {
      label: 'DRAFT',
      bg: 'bg-slate-100 text-slate-700 border-slate-200'
    },
    HIGH: {
      label: 'HIGH PRIORITY',
      bg: 'bg-rose-100 text-rose-800 border-rose-300'
    },
    MEDIUM: {
      label: 'MEDIUM',
      bg: 'bg-amber-100 text-amber-800 border-amber-300'
    },
    LOW: {
      label: 'LOW',
      bg: 'bg-slate-100 text-slate-800 border-slate-300'
    }
  };

  const item = config[normalized] || {
    label: normalized || 'UNKNOWN',
    bg: 'bg-slate-100 text-slate-700 border-slate-200'
  };

  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border ${item.bg} ${className}`}
    >
      {item.label}
    </span>
  );
}
