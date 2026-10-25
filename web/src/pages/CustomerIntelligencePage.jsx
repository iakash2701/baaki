import React from 'react';
import Header from '../components/layout/Header';
import { useData } from '../data/DataContext';
import { formatINR } from '../domain/money';
import { Users, AlertCircle, Clock, CheckCircle2, TrendingUp } from 'lucide-react';

export default function CustomerIntelligencePage() {
  const { customerInsights } = useData();

  return (
    <div className="pb-24 md:pb-12">
      <Header
        title="Customer Intelligence"
        subtitle="Analyze customer payment delays, billing history & reliability patterns"
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-5xl mx-auto">
        {/* Intro banner */}
        <div className="p-6 rounded-3xl bg-[#1B4D3E] text-white shadow-xs space-y-2">
          <div className="flex items-center gap-2 text-emerald-200 text-xs font-bold uppercase tracking-wider">
            <TrendingUp className="w-4 h-4" />
            <span>Habit & Reliability Analysis</span>
          </div>
          <h2 className="text-xl sm:text-2xl font-extrabold tracking-tight">
            Customer Credit Intelligence
          </h2>
          <p className="text-xs sm:text-sm text-emerald-100 max-w-2xl leading-relaxed">
            Historical settlement behavior calculated across customer accounts. Identify which clients pay on time and which consistently delay settlement.
          </p>
        </div>

        {/* Intelligence Cards */}
        {customerInsights.length === 0 ? (
          <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center text-slate-400">
            No customer accounts found.
          </div>
        ) : (
          <div className="space-y-4">
            {customerInsights.map((insight) => {
              const {
                customer,
                totalInvoicedPaise,
                totalPaidPaise,
                outstandingPaise,
                unpaidInvoiceCount,
                averagePaymentDelayDays
              } = insight;

              const isHighRisk = averagePaymentDelayDays != null && averagePaymentDelayDays > 5;

              let delayLabel = 'Insufficient payment history for timing analysis';
              let delayClass = 'text-slate-500 bg-slate-100';

              if (averagePaymentDelayDays != null) {
                const days = Math.round(averagePaymentDelayDays);
                if (days <= 0) {
                  delayLabel = `Pays promptly (~${Math.abs(days)} days before/on due date)`;
                  delayClass = 'text-emerald-700 bg-emerald-50 border-emerald-200';
                } else {
                  delayLabel = `Average delay: ~${days} days past invoice due date`;
                  delayClass = days > 5 ? 'text-rose-700 bg-rose-50 border-rose-200' : 'text-amber-700 bg-amber-50 border-amber-200';
                }
              }

              return (
                <div
                  key={customer.id}
                  className="bg-white border border-slate-200 hover:border-slate-300 rounded-2xl p-5 sm:p-6 shadow-xs transition-all space-y-4"
                >
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-100 pb-3">
                    <div>
                      <h3 className="font-extrabold text-base sm:text-lg text-slate-900">
                        {customer.name}
                      </h3>
                      <p className="text-xs text-slate-500">
                        Phone: {customer.phone} {customer.gstin ? `• GSTIN: ${customer.gstin}` : ''}
                      </p>
                    </div>

                    <div className="text-left sm:text-right">
                      <span className="text-[10px] text-slate-400 font-bold uppercase block">Current Balance</span>
                      <span className={`text-base font-extrabold ${outstandingPaise > 0 ? 'text-rose-600' : 'text-emerald-700'}`}>
                        {formatINR(outstandingPaise)}
                      </span>
                    </div>
                  </div>

                  {/* Financial Stats */}
                  <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs">
                    <div className="p-3 bg-slate-50 rounded-xl">
                      <span className="text-slate-400 block font-semibold mb-0.5">Total Billed</span>
                      <span className="text-sm font-bold text-slate-800">{formatINR(totalInvoicedPaise)}</span>
                    </div>
                    <div className="p-3 bg-emerald-50/60 rounded-xl">
                      <span className="text-emerald-700 block font-semibold mb-0.5">Total Collected</span>
                      <span className="text-sm font-bold text-emerald-800">{formatINR(totalPaidPaise)}</span>
                    </div>
                    <div className="p-3 bg-slate-50 rounded-xl">
                      <span className="text-slate-400 block font-semibold mb-0.5">Unpaid Invoices</span>
                      <span className="text-sm font-bold text-slate-800">{unpaidInvoiceCount}</span>
                    </div>
                    <div className="p-3 bg-slate-50 rounded-xl">
                      <span className="text-slate-400 block font-semibold mb-0.5">Prior Ledger Balance</span>
                      <span className="text-sm font-bold text-slate-800">{formatINR(customer.openingBalance || 0)}</span>
                    </div>
                  </div>

                  {/* Behavior Evaluation */}
                  <div className="pt-2">
                    <div className={`p-3 rounded-xl border text-xs font-semibold flex items-center gap-2 ${delayClass}`}>
                      {isHighRisk ? (
                        <AlertCircle className="w-4 h-4 flex-shrink-0" />
                      ) : (
                        <Clock className="w-4 h-4 flex-shrink-0" />
                      )}
                      <span>{delayLabel}</span>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
