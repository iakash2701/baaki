import React from 'react';
import { Link } from 'react-router-dom';
import Header from '../components/layout/Header';
import {
  Star,
  TrendingUp,
  Receipt,
  Users,
  Wallet,
  CheckCircle2,
  ArrowRight,
  ShieldCheck,
  Cpu
} from 'lucide-react';

export default function JudgesShowcasePage() {
  const showcaseFeatures = [
    {
      title: 'Smart Recovery Intelligence',
      description: 'Explainable priority scoring (0–100) for overdue invoices with recommended actions, grounded reasons, and direct reminder sharing.',
      icon: TrendingUp,
      to: '/recovery'
    },
    {
      title: 'Cash Flow Intelligence & Breakdown',
      description: 'Track confirmed inflow collections, operational expenditures, net cash position, and chronological transaction history.',
      icon: Wallet,
      to: '/cashflow'
    },
    {
      title: 'Customer Credit Intelligence',
      description: 'Analyze customer payment reliability, historical collections, and average payment delay days past invoice due dates.',
      icon: Users,
      to: '/intelligence'
    },
    {
      title: 'GST Invoices & Instant Sharing',
      description: 'Create multi-item GST invoices, record partial or full payments, and share WhatsApp-ready summaries or print clean invoices.',
      icon: Receipt,
      to: '/invoices'
    }
  ];

  return (
    <div className="pb-24 md:pb-12">
      <Header
        title="Judges Showcase"
        subtitle="Baaki Hackathon Demo Tour • Built for Indian MSME suppliers 🇮🇳"
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-4xl mx-auto">
        {/* Hero Card */}
        <div className="p-6 sm:p-8 rounded-3xl bg-gradient-to-r from-[#1B4D3E] to-[#0A2920] text-white shadow-md space-y-3">
          <div className="flex items-center gap-2 text-emerald-300 text-xs font-bold uppercase tracking-wider">
            <Star className="w-4 h-4 fill-emerald-300" />
            <span>Hackathon Evaluation Hub</span>
          </div>
          <h2 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            Baaki • Smart Invoice Collection
          </h2>
          <p className="text-xs sm:text-sm text-emerald-100 max-w-2xl leading-relaxed">
            Small Indian B2B manufacturers sell goods on 15–30 day credit and frequently suffer severe liquidity crunches due to delayed payments. Baaki gives business owners complete control over credit receivables, payment chasing, and cash-flow visibility.
          </p>
        </div>

        {/* Feature Hub Cards (Mirrors Android JudgesShowcaseScreen ShowcaseCard) */}
        <div>
          <h3 className="text-base font-bold text-slate-800 mb-3">
            Core Feature Modules
          </h3>
          <div className="space-y-3">
            {showcaseFeatures.map((feat) => {
              const Icon = feat.icon;
              return (
                <Link
                  key={feat.title}
                  to={feat.to}
                  className="flex items-center justify-between p-5 bg-white border border-slate-200 hover:border-[#1B4D3E] hover:shadow-sm rounded-2xl transition-all group"
                >
                  <div className="flex items-start gap-4">
                    <div className="p-3 rounded-xl bg-slate-100 text-[#1B4D3E] group-hover:bg-[#A3E4D7]/40 transition-colors">
                      <Icon className="w-5 h-5" />
                    </div>
                    <div className="space-y-1">
                      <h4 className="font-extrabold text-sm sm:text-base text-slate-900 group-hover:text-[#1B4D3E]">
                        {feat.title}
                      </h4>
                      <p className="text-xs text-slate-500 max-w-xl leading-relaxed">
                        {feat.description}
                      </p>
                    </div>
                  </div>
                  <ArrowRight className="w-4 h-4 text-slate-400 group-hover:text-[#1B4D3E] transition-colors ml-2 flex-shrink-0" />
                </Link>
              );
            })}
          </div>
        </div>

        {/* Hackathon Demo Walkthrough (Mirrors Android JudgesShowcaseScreen Demo Guide) */}
        <div className="bg-slate-50 border border-slate-200 rounded-2xl p-6 space-y-3 shadow-xs">
          <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-[#1B4D3E]" />
            <span>Recommended Judging Walkthrough Steps</span>
          </h3>
          <ol className="list-decimal list-inside text-xs sm:text-sm text-slate-600 space-y-2 font-medium leading-relaxed pl-1">
            <li>
              <strong className="text-slate-900 font-bold">Open Smart Recovery:</strong> Review urgent overdue accounts scored 0–100 with clear data-grounded reasons.
            </li>
            <li>
              <strong className="text-slate-900 font-bold">Share Payment Reminder:</strong> Click &quot;Share Reminder&quot; on any invoice to view the WhatsApp/SMS formatted template with Sharma Packaging branding.
            </li>
            <li>
              <strong className="text-slate-900 font-bold">Record a Partial Payment:</strong> Open an invoice (e.g. SP/2025-26/001), tap &quot;Record Payment&quot;, enter ₹5,000, and watch the status update from OVERDUE to PARTIAL and real-time dashboard updates.
            </li>
            <li>
              <strong className="text-slate-900 font-bold">Inspect Customer Intelligence:</strong> Check average delay analysis and customer credit reliability.
            </li>
            <li>
              <strong className="text-slate-900 font-bold">View Cash Flow:</strong> Review confirmed collections, raw material expenses, and net cash balance.
            </li>
          </ol>
        </div>
      </div>
    </div>
  );
}
