import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import Header from '../components/layout/Header';
import { useData } from '../data/DataContext';
import { formatDateTime } from '../util/invoiceShareFormatter';
import {
  Activity,
  Star,
  Users,
  Wallet,
  Building,
  RefreshCw,
  Info,
  CheckCircle2,
  Receipt,
  BarChart3,
  Download,
  Smartphone
} from 'lucide-react';

export default function MorePage() {
  const { activities, resetDemo } = useData();

  useEffect(() => {
    document.title = 'Settings & Tools — Baaki';
  }, []);

  const handleReset = () => {
    if (window.confirm('Reset all demo data back to initial Sharma Packaging state?')) {
      resetDemo();
    }
  };

  const navLinks = [
    { title: 'Judges Showcase', to: '/showcase', icon: Star, desc: 'Guided hackathon product tour' },
    { title: 'Financial Reports & Analytics', to: '/reports', icon: BarChart3, desc: 'Audited summaries & balances' },
    { title: 'Cash Flow Intelligence', to: '/cashflow', icon: Wallet, desc: 'Collections, expenses, & net flow' },
    { title: 'Customer Intelligence', to: '/intelligence', icon: Users, desc: 'Payment habits & average delays' },
    { title: 'Invoices Hub', to: '/invoices', icon: Receipt, desc: 'Create & manage GST invoices' }
  ];

  return (
    <div className="pb-24 md:pb-12">
      <Header
        title="More"
        subtitle="System settings, Android APK download, demo tools & activity audit trail"
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-4xl mx-auto">
        {/* Android App Download Banner (New from user's updated APK) */}
        <div className="p-6 bg-gradient-to-r from-emerald-50 to-teal-50 border border-emerald-200 rounded-3xl shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-start gap-4">
            <div className="p-3 bg-[#1B4D3E] text-[#A3E4D7] rounded-2xl shadow-xs" aria-hidden="true">
              <Smartphone className="w-6 h-6" />
            </div>
            <div className="space-y-1">
              <div className="flex items-center gap-2">
                <h2 className="text-base font-extrabold text-slate-900">
                  Baaki Android App (APK)
                </h2>
                <span className="px-2 py-0.5 rounded-full text-[11px] font-bold bg-emerald-100 text-emerald-800 border border-emerald-200">
                  v1.0.0 Updated
                </span>
              </div>
              <p className="text-xs text-slate-600 leading-relaxed max-w-lg">
                Official debug APK built with Jetpack Compose, Room SQLite database, and offline-first UPI collection engine. Size: ~20.8 MB.
              </p>
            </div>
          </div>
          <a
            href="/baaki.apk"
            download="Baaki.apk"
            className="inline-flex items-center justify-center gap-2 px-5 py-2.5 rounded-xl bg-[#1B4D3E] hover:bg-[#143D31] text-white font-bold text-xs sm:text-sm shadow-sm transition-all whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
          >
            <Download className="w-4 h-4" aria-hidden="true" />
            <span>Download APK (20.8 MB)</span>
          </a>
        </div>

        {/* Business Profile Card */}
        <div className="p-6 bg-white border border-slate-200 rounded-2xl shadow-xs space-y-2">
          <div className="flex items-center gap-2 text-xs font-bold text-slate-600 uppercase tracking-wider">
            <Building className="w-4 h-4 text-[#1B4D3E]" aria-hidden="true" />
            <span>Registered Business Profile</span>
          </div>
          <h2 className="text-lg font-extrabold text-slate-900">
            Sharma Packaging 🇮🇳
          </h2>
          <p className="text-xs text-slate-600">
            Carton & Corrugated Box Manufacturing • Industrial Area, Phase II, New Delhi
          </p>
          <div className="flex flex-wrap gap-2 pt-1 text-xs text-slate-700 font-mono">
            <span className="bg-slate-100 px-2.5 py-1 rounded-md">GSTIN: 07AABCS1429B1Z6</span>
            <span className="bg-slate-100 px-2.5 py-1 rounded-md">State: 07-Delhi</span>
            <span className="bg-slate-100 px-2.5 py-1 rounded-md">Default Terms: 15 Days</span>
          </div>
        </div>

        {/* Quick Nav Links */}
        <div className="space-y-2">
          <h2 className="text-xs font-bold text-slate-600 uppercase tracking-wider px-1">
            Explore Modules
          </h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {navLinks.map((item) => {
              const Icon = item.icon;
              return (
                <Link
                  key={item.title}
                  to={item.to}
                  className="flex items-center gap-3.5 p-4 bg-white border border-slate-200 hover:border-[#1B4D3E] hover:shadow-sm rounded-2xl transition-all focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
                >
                  <div className="p-2.5 rounded-xl bg-slate-100 text-[#1B4D3E]" aria-hidden="true">
                    <Icon className="w-5 h-5" />
                  </div>
                  <div>
                    <h3 className="text-sm font-bold text-slate-900">{item.title}</h3>
                    <p className="text-xs text-slate-600">{item.desc}</p>
                  </div>
                </Link>
              );
            })}
          </div>
        </div>

        {/* Demo Management */}
        <div className="p-5 bg-white border border-slate-200 rounded-2xl shadow-xs space-y-3">
          <h2 className="text-sm font-bold text-slate-800">Demo Data Management</h2>
          <p className="text-xs text-slate-600 leading-relaxed">
            All data is persisted in browser LocalStorage. You can reset back to the 6 customers and 10 invoices demo scenario at any time.
          </p>
          <button
            onClick={handleReset}
            className="flex items-center gap-2 px-4 py-2 text-xs font-bold text-slate-700 bg-slate-100 hover:bg-slate-200 border border-slate-300 rounded-xl transition-all focus:outline-none focus:ring-2 focus:ring-slate-400"
          >
            <RefreshCw className="w-3.5 h-3.5" aria-hidden="true" />
            <span>Reset Demo Data</span>
          </button>
        </div>

        {/* Recent Activity Log */}
        <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-xs">
          <div className="p-5 border-b border-slate-100 flex items-center justify-between">
            <div>
              <h2 className="text-base font-bold text-slate-900">Recent Activity Log</h2>
              <p className="text-xs text-slate-600">Audit trail of actions taken in the current session</p>
            </div>
            <span className="text-xs font-bold text-[#1B4D3E] bg-emerald-50 px-2.5 py-1 rounded-full border border-emerald-100">
              {activities.length} Recorded
            </span>
          </div>

          <div className="divide-y divide-slate-100 max-h-80 overflow-y-auto">
            {activities.length === 0 ? (
              <div className="p-8 text-center text-slate-500 text-xs">
                No activities recorded in this session.
              </div>
            ) : (
              activities.map((act) => (
                <div key={act.id} className="p-4 flex items-start gap-3 text-xs">
                  <div className="p-1.5 rounded-lg bg-emerald-50 text-emerald-700 mt-0.5" aria-hidden="true">
                    <CheckCircle2 className="w-4 h-4" />
                  </div>
                  <div className="space-y-0.5 flex-1">
                    <p className="font-semibold text-slate-800">{act.description}</p>
                    <p className="text-[11px] text-slate-500">{formatDateTime(act.timestamp)}</p>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
