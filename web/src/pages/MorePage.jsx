import React from 'react';
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
  Receipt
} from 'lucide-react';

export default function MorePage() {
  const { activities, resetDemo } = useData();

  const handleReset = () => {
    if (window.confirm('Reset all demo data back to initial Sharma Packaging state?')) {
      resetDemo();
    }
  };

  const navLinks = [
    { title: 'Judges Showcase', to: '/showcase', icon: Star, desc: 'Guided hackathon product tour' },
    { title: 'Cash Flow Intelligence', to: '/cashflow', icon: Wallet, desc: 'Collections, expenses, & net flow' },
    { title: 'Customer Intelligence', to: '/intelligence', icon: Users, desc: 'Payment habits & average delays' },
    { title: 'Invoices Hub', to: '/invoices', icon: Receipt, desc: 'Create & manage invoices' }
  ];

  return (
    <div className="pb-24 md:pb-12">
      <Header
        title="More"
        subtitle="System settings, demo tools & activity audit trail"
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-4xl mx-auto">
        {/* Business Profile Card */}
        <div className="p-6 bg-white border border-slate-200 rounded-2xl shadow-xs space-y-2">
          <div className="flex items-center gap-2 text-xs font-bold text-slate-400 uppercase tracking-wider">
            <Building className="w-4 h-4 text-[#1B4D3E]" />
            <span>Registered Business Profile</span>
          </div>
          <h3 className="text-lg font-extrabold text-slate-900">
            Sharma Packaging 🇮🇳
          </h3>
          <p className="text-xs text-slate-500">
            Carton & Corrugated Box Manufacturing • Industrial Area, Phase II, New Delhi
          </p>
          <div className="flex flex-wrap gap-2 pt-1 text-xs text-slate-600 font-mono">
            <span className="bg-slate-100 px-2.5 py-1 rounded-md">GSTIN: 07AABCS1429B1Z6</span>
            <span className="bg-slate-100 px-2.5 py-1 rounded-md">State: 07-Delhi</span>
            <span className="bg-slate-100 px-2.5 py-1 rounded-md">Default Terms: 15 Days</span>
          </div>
        </div>

        {/* Quick Nav Links */}
        <div className="space-y-2">
          <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider px-1">
            Explore Modules
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {navLinks.map((item) => {
              const Icon = item.icon;
              return (
                <Link
                  key={item.title}
                  to={item.to}
                  className="flex items-center gap-3.5 p-4 bg-white border border-slate-200 hover:border-[#1B4D3E] hover:shadow-sm rounded-2xl transition-all"
                >
                  <div className="p-2.5 rounded-xl bg-slate-100 text-[#1B4D3E]">
                    <Icon className="w-5 h-5" />
                  </div>
                  <div>
                    <h4 className="text-sm font-bold text-slate-900">{item.title}</h4>
                    <p className="text-xs text-slate-400">{item.desc}</p>
                  </div>
                </Link>
              );
            })}
          </div>
        </div>

        {/* Demo Management */}
        <div className="p-5 bg-white border border-slate-200 rounded-2xl shadow-xs space-y-3">
          <h3 className="text-sm font-bold text-slate-800">Demo Data Management</h3>
          <p className="text-xs text-slate-500 leading-relaxed">
            All data is persisted in browser LocalStorage. You can reset back to the 6 customers and 10 invoices demo scenario at any time.
          </p>
          <button
            onClick={handleReset}
            className="flex items-center gap-2 px-4 py-2 text-xs font-bold text-slate-700 bg-slate-100 hover:bg-slate-200 border border-slate-300 rounded-xl transition-all"
          >
            <RefreshCw className="w-3.5 h-3.5" />
            <span>Reset Demo Data</span>
          </button>
        </div>

        {/* Activity Audit Trail */}
        <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-xs">
          <div className="p-5 border-b border-slate-100 flex items-center justify-between">
            <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
              <Activity className="w-4 h-4 text-[#1B4D3E]" />
              <span>Activity Audit Trail ({activities.length})</span>
            </h3>
            <span className="text-xs text-slate-400 font-medium">Real-time log</span>
          </div>

          <div className="divide-y divide-slate-100 max-h-96 overflow-y-auto">
            {activities.length === 0 ? (
              <div className="p-8 text-center text-xs text-slate-400">
                No activity recorded yet.
              </div>
            ) : (
              activities.map((act) => (
                <div key={act.id} className="p-4 hover:bg-slate-50 transition-colors">
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-xs font-bold text-slate-800">{act.title}</span>
                    <span className="text-[10px] text-slate-400 font-medium">
                      {formatDateTime(act.timestamp)}
                    </span>
                  </div>
                  <p className="text-xs text-slate-600 leading-relaxed">{act.description}</p>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
