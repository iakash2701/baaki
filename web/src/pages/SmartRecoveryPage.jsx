import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/layout/Header';
import StatusBadge from '../components/ui/StatusBadge';
import InvoiceShareModal from '../components/invoices/InvoiceShareModal';
import { useData } from '../data/DataContext';
import { formatINR } from '../domain/money';
import { formatDate } from '../util/invoiceShareFormatter';
import { PriorityLevel } from '../domain/recoveryScorer';
import {
  TrendingUp,
  Share2,
  AlertTriangle,
  CheckCircle2,
  Clock,
  Sparkles,
  Info
} from 'lucide-react';

export default function SmartRecoveryPage() {
  const navigate = useNavigate();
  const { recoveryItems } = useData();
  const [selectedRecoveryItem, setSelectedRecoveryItem] = useState(null);

  const highPriorityCount = recoveryItems.filter(i => i.priorityLevel === PriorityLevel.HIGH).length;
  const totalRecoverable = recoveryItems.reduce((sum, i) => sum + i.outstandingPaise, 0);

  return (
    <div className="pb-24 md:pb-12">
      <Header
        title="Smart Recovery"
        subtitle="Explainable 0–100 collection scoring & one-click reminder generator"
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-5xl mx-auto">
        {/* Banner summary */}
        <div className="p-6 rounded-3xl bg-gradient-to-r from-amber-500 via-amber-600 to-orange-600 text-white shadow-sm flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="space-y-1">
            <div className="flex items-center gap-2 text-amber-100 text-xs font-bold uppercase tracking-wider">
              <Sparkles className="w-4 h-4" />
              <span>Smart Recovery Algorithm Active</span>
            </div>
            <h2 className="text-xl sm:text-2xl font-extrabold tracking-tight">
              {recoveryItems.length} Invoices Need Follow-up
            </h2>
            <p className="text-xs sm:text-sm text-amber-100">
              {highPriorityCount} high-priority accounts requiring immediate attention. Total recoverable balance:{' '}
              <strong className="underline underline-offset-4">{formatINR(totalRecoverable)}</strong>
            </p>
          </div>

          <div className="p-3 bg-white/10 rounded-2xl border border-white/20 backdrop-blur-sm text-center min-w-[120px]">
            <span className="text-xs text-amber-100 block font-medium">High Urgency</span>
            <span className="text-2xl font-extrabold">{highPriorityCount}</span>
          </div>
        </div>

        {/* Explainable Scoring Methodology Card */}
        <div className="p-4 bg-white border border-slate-200 rounded-2xl text-xs text-slate-600 leading-relaxed shadow-xs flex items-start gap-3">
          <Info className="w-4 h-4 text-[#1B4D3E] flex-shrink-0 mt-0.5" />
          <div>
            <strong className="text-slate-800 font-bold">How Baaki Priority Scores (0–100): </strong>
            Base (10 pts) + Overdue Days (up to 50 pts, 2 pts/day) + Outstanding Balance ratio (up to 20 pts) + Partial Payment received boost (10 pts) + Prior customer ledger balance boost (10 pts).
          </div>
        </div>

        {/* Recovery Queue Items */}
        {recoveryItems.length === 0 ? (
          <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center space-y-3">
            <CheckCircle2 className="w-12 h-12 text-emerald-600 mx-auto" />
            <h4 className="text-base font-bold text-slate-700">No Invoices in Recovery Queue</h4>
            <p className="text-xs text-slate-500 max-w-sm mx-auto">
              All customer invoices are fully settled! Excellent work keeping collections healthy.
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {recoveryItems.map((item) => {
              const { invoice, customer, outstandingPaise, daysOverdue, priorityScore, priorityLevel, reasons, recommendedAction } = item;

              return (
                <div
                  key={invoice.id}
                  className="bg-white border border-slate-200 hover:border-slate-300 rounded-2xl p-5 sm:p-6 shadow-xs transition-all space-y-4"
                >
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-100 pb-3">
                    <div className="space-y-0.5">
                      <div className="flex items-center gap-2">
                        <h3 className="font-extrabold text-base sm:text-lg text-slate-900">
                          {customer.name}
                        </h3>
                        <span className="text-xs text-slate-400 font-mono">
                          ({invoice.invoiceNumber})
                        </span>
                      </div>
                      <div className="text-xs text-slate-500">
                        Due Date: <strong className={daysOverdue > 0 ? 'text-rose-600' : 'text-slate-700'}>{formatDate(invoice.dueDate)}</strong>
                        {daysOverdue > 0 && ` (${daysOverdue} days overdue)`}
                      </div>
                    </div>

                    <div className="flex items-center gap-2.5 self-start sm:self-auto">
                      <div className="flex items-center gap-1.5 px-3 py-1 rounded-xl bg-slate-100 border border-slate-200">
                        <span className="text-[10px] font-bold text-slate-400 uppercase">Score</span>
                        <span className="text-sm font-black text-slate-800">{priorityScore}/100</span>
                      </div>
                      <StatusBadge status={priorityLevel} />
                    </div>
                  </div>

                  {/* Amounts row */}
                  <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 text-xs">
                    <div className="p-3 bg-slate-50 rounded-xl">
                      <span className="text-slate-400 block font-semibold mb-0.5">Invoice Total</span>
                      <span className="text-sm font-bold text-slate-800">{formatINR(invoice.totalAmount)}</span>
                    </div>
                    <div className="p-3 bg-red-50/70 border border-red-100 rounded-xl">
                      <span className="text-red-700 block font-semibold mb-0.5">Outstanding Due</span>
                      <span className="text-sm font-extrabold text-red-600">{formatINR(outstandingPaise)}</span>
                    </div>
                    <div className="p-3 bg-slate-50 rounded-xl col-span-2 sm:col-span-1">
                      <span className="text-slate-400 block font-semibold mb-0.5">Customer Contact</span>
                      <span className="text-xs font-semibold text-slate-700">{customer.phone || 'No phone'}</span>
                    </div>
                  </div>

                  {/* Grounded Reasons List */}
                  <div className="space-y-1.5 pt-1">
                    <span className="text-[11px] font-bold text-[#1B4D3E] uppercase tracking-wider block">
                      Grounding Reasons:
                    </span>
                    <div className="flex flex-wrap gap-2">
                      {reasons.map((r, idx) => (
                        <span
                          key={idx}
                          className="px-2.5 py-1 rounded-lg text-xs font-medium bg-slate-100 text-slate-700 border border-slate-200/80"
                        >
                          • {r}
                        </span>
                      ))}
                    </div>
                  </div>

                  {/* Footer Recommended Action & Share */}
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pt-3 border-t border-slate-100">
                    <div className="text-xs text-slate-600 italic">
                      Recommended: <strong className="text-slate-800 not-italic font-bold">{recommendedAction}</strong>
                    </div>

                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => navigate(`/invoices/${invoice.id}`)}
                        className="px-3 py-1.5 text-xs font-bold text-slate-600 hover:text-slate-900 border border-slate-200 rounded-xl hover:bg-slate-50 transition-colors"
                      >
                        View Invoice
                      </button>

                      <button
                        onClick={() => setSelectedRecoveryItem(item)}
                        className="flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold text-white bg-[#1B4D3E] hover:bg-[#153f33] rounded-xl shadow-xs transition-all"
                      >
                        <Share2 className="w-3.5 h-3.5" />
                        <span>Share Reminder</span>
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {selectedRecoveryItem && (
        <InvoiceShareModal
          isOpen={true}
          onClose={() => setSelectedRecoveryItem(null)}
          recoveryItem={selectedRecoveryItem}
        />
      )}
    </div>
  );
}
