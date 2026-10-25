import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Header from '../components/layout/Header';
import MetricCard from '../components/ui/MetricCard';
import StatusBadge from '../components/ui/StatusBadge';
import { useData } from '../data/DataContext';
import { formatINR } from '../domain/money';
import { formatDate } from '../util/invoiceShareFormatter';
import {
  TrendingUp,
  Receipt,
  Users,
  Wallet,
  Star,
  ArrowRight,
  ShieldAlert,
  CheckCircle2,
  Clock,
  LineChart,
  ChevronRight,
  BarChart3
} from 'lucide-react';

export default function DashboardPage() {
  const navigate = useNavigate();
  const { dashboardMetrics } = useData();

  useEffect(() => {
    document.title = 'Dashboard — Baaki Cash Flow Intelligence';
  }, []);

  const {
    totalOutstandingPaise,
    overdueBalancePaise,
    overdueCount,
    totalCollectionsPaise,
    invoiceCount,
    customerCount,
    recentInvoices
  } = dashboardMetrics;

  const quickActions = [
    { title: 'Invoices', to: '/invoices', icon: Receipt, desc: 'Issue & track' },
    { title: 'Customers', to: '/customers', icon: Users, desc: 'Manage ledgers' },
    { title: 'Reports', to: '/reports', icon: BarChart3, desc: 'Sales & audits' },
    { title: 'Cash Flow', to: '/cashflow', icon: Wallet, desc: 'Track flows' },
    { title: 'Intelligence', to: '/intelligence', icon: LineChart, desc: 'Payment habits' },
    { title: 'Smart Recovery', to: '/recovery', icon: TrendingUp, desc: 'Priority chase' }
  ];

  return (
    <div className="pb-24 md:pb-12">
      <Header
        title="Dashboard"
        subtitle="Sharma Packaging 🇮🇳 • Know who owes you, chase with intelligence."
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-7xl mx-auto">
        {/* Business Hero Banner */}
        <div className="rounded-3xl bg-[#1B4D3E] bg-gradient-to-r from-[#1B4D3E] via-[#145530] to-[#0A2920] text-white p-6 sm:p-8 shadow-md relative overflow-hidden">
          <div className="relative z-10 max-w-2xl">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/10 backdrop-blur-md text-emerald-200 text-xs font-semibold mb-3 border border-emerald-400/20">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" aria-hidden="true" />
              <span>Sharma Packaging • Credit Collection Command</span>
            </div>
            <h2 className="text-2xl sm:text-3xl font-extrabold tracking-tight mb-2 text-white">
              Keep your cash flow protected.
            </h2>
            <p className="text-sm text-emerald-50 leading-relaxed mb-5">
              Real-time Indian GST invoice tracking, 0–100 Smart Recovery scoring, customer payment habit intelligence, and cash-flow monitoring.
            </p>
            <div className="flex flex-wrap items-center gap-3">
              <Link
                to="/recovery"
                className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#A3E4D7] text-[#0A2920] font-bold text-xs sm:text-sm hover:bg-emerald-300 transition-all shadow-sm focus:outline-none focus:ring-2 focus:ring-emerald-400"
              >
                <TrendingUp className="w-4 h-4" aria-hidden="true" />
                <span>Open Smart Recovery Queue</span>
              </Link>
              <Link
                to="/invoices"
                className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-white text-[#1B4D3E] hover:bg-emerald-50 font-bold text-xs sm:text-sm transition-all shadow-sm focus:outline-none focus:ring-2 focus:ring-white"
              >
                <span>Create New Invoice</span>
              </Link>
            </div>
          </div>
          <div className="absolute right-0 bottom-0 opacity-10 pointer-events-none translate-x-8 translate-y-8" aria-hidden="true">
            <Receipt className="w-80 h-80 text-white" />
          </div>
        </div>

        {/* Financial Metrics Cards (Mirrors Android HomeScreen Financial Overview) */}
        <div>
          <h2 className="text-base font-bold text-slate-800 mb-3 flex items-center gap-2">
            <span>Financial Overview</span>
          </h2>
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4">
            <MetricCard
              title="Total Outstanding"
              amount={formatINR(totalOutstandingPaise)}
              subtitle={`${invoiceCount} Total Invoices`}
              icon={Receipt}
              variant="default"
            />
            <MetricCard
              title="Collections"
              amount={formatINR(totalCollectionsPaise)}
              subtitle="Confirmed received"
              icon={CheckCircle2}
              variant="secondary"
            />
            <MetricCard
              title="Overdue Receivables"
              amount={formatINR(overdueBalancePaise)}
              subtitle={`${overdueCount} Invoices Overdue`}
              icon={ShieldAlert}
              variant="error"
            />
            <MetricCard
              title="Active Customers"
              amount={customerCount.toString()}
              subtitle="Ledger accounts"
              icon={Users}
              variant="default"
            />
          </div>
        </div>

        {/* Smart Recovery Spotlight (Mirrors Android HomeScreen Spotlight Card) */}
        <div className="p-5 sm:p-6 bg-gradient-to-r from-emerald-50/90 to-teal-50/90 border border-emerald-200/90 rounded-2xl flex flex-col sm:flex-row sm:items-center justify-between gap-4 shadow-xs">
          <div className="flex items-start gap-3.5">
            <div className="p-3 bg-[#1B4D3E] text-[#A3E4D7] rounded-xl shadow-xs mt-0.5" aria-hidden="true">
              <TrendingUp className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2.5 flex-wrap">
                <h3 className="text-base font-bold text-slate-900">
                  Smart Recovery Queue
                </h3>
                <span className="px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800 border border-emerald-200">
                  {overdueCount} needs attention
                </span>
              </div>
              <p className="text-xs sm:text-sm text-slate-700 mt-1 leading-relaxed">
                Prioritized using Android's explainable 0–100 algorithm. Potential recovery balance: <strong className="font-bold text-slate-900">{formatINR(overdueBalancePaise)}</strong>
              </p>
            </div>
          </div>
          <Link
            to="/recovery"
            className="inline-flex items-center justify-center gap-2 px-5 py-2.5 rounded-xl bg-[#1B4D3E] hover:bg-[#143D31] text-white font-bold text-xs sm:text-sm shadow-xs transition-all whitespace-nowrap self-start sm:self-auto focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
          >
            <span>View Recovery Queue</span>
            <ArrowRight className="w-4 h-4" aria-hidden="true" />
          </Link>
        </div>

        {/* Quick Actions Grid (Mirrors Android HomeScreen Quick Actions) */}
        <div>
          <h2 className="text-base font-bold text-slate-800 mb-3">
            Quick Actions
          </h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
            {quickActions.map((action) => {
              const Icon = action.icon;
              return (
                <Link
                  key={action.title}
                  to={action.to}
                  className="flex flex-col items-center justify-center py-5 px-3 min-h-[114px] rounded-2xl bg-white border border-slate-200 hover:border-[#1B4D3E] hover:shadow-md transition-all text-center group focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
                >
                  <div className="p-2.5 rounded-xl bg-slate-100 group-hover:bg-[#A3E4D7]/40 text-[#1B4D3E] transition-colors mb-2" aria-hidden="true">
                    <Icon className="w-5 h-5" />
                  </div>
                  <span className="text-xs font-bold text-slate-800 group-hover:text-[#1B4D3E]">
                    {action.title}
                  </span>
                  <span className="text-xs text-slate-600 font-medium mt-1.5">
                    {action.desc}
                  </span>
                </Link>
              );
            })}
          </div>
        </div>

        {/* Cash Flow Snapshot banner */}
        <div className="p-5 bg-white border border-slate-200 rounded-2xl flex items-center justify-between shadow-xs">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-xl bg-emerald-50 text-emerald-700" aria-hidden="true">
              <Wallet className="w-5 h-5" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-slate-800">Cash Flow Snapshot</h3>
              <p className="text-xs text-slate-600">
                Confirmed collections: <strong className="text-emerald-700 font-semibold">{formatINR(totalCollectionsPaise)}</strong>
              </p>
            </div>
          </div>
          <Link
            to="/cashflow"
            aria-label="View Cash Flow details"
            className="px-4 py-2 rounded-xl border border-slate-200 bg-slate-50 hover:bg-slate-100 font-bold text-xs text-[#1B4D3E] flex items-center gap-1.5 transition-colors shadow-2xs focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
          >
            <span>View Details</span>
            <ArrowRight className="w-3.5 h-3.5" aria-hidden="true" />
          </Link>
        </div>

        {/* Recent Invoices List (Mirrors Android HomeScreen Recent Invoices) */}
        <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-xs">
          <div className="p-5 border-b border-slate-100 flex items-center justify-between">
            <div>
              <h3 className="text-base font-bold text-slate-800">
                Recent Invoices ({invoiceCount} Total)
              </h3>
              <p className="text-xs text-slate-600 font-medium">
                Latest billing activities across all customer accounts
              </p>
            </div>
            <Link
              to="/invoices"
              aria-label="View all invoices"
              className="text-xs font-bold text-[#1B4D3E] hover:underline flex items-center gap-1 py-1 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] rounded"
            >
              <span>View All</span>
              <ArrowRight className="w-3.5 h-3.5" aria-hidden="true" />
            </Link>
          </div>

          <div className="divide-y divide-slate-100">
            {recentInvoices.length === 0 ? (
              <div className="p-8 text-center text-slate-500 text-sm">
                No invoices recorded yet.
              </div>
            ) : (
              recentInvoices.map((item) => {
                const now = Date.now();
                const isOverdue = item.invoice.dueDate < now && item.outstandingPaise > 0;

                return (
                  <div
                    key={item.invoice.id}
                    role="button"
                    tabIndex={0}
                    aria-label={`View invoice ${item.invoice.invoiceNumber} for ${item.customerName}`}
                    onClick={() => navigate(`/invoices/${item.invoice.id}`)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        navigate(`/invoices/${item.invoice.id}`);
                      }
                    }}
                    className="p-4 sm:p-5 flex items-center justify-between hover:bg-slate-50 cursor-pointer transition-colors group focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] focus:ring-inset"
                  >
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-bold text-sm text-[#1B4D3E] group-hover:underline">
                          {item.invoice.invoiceNumber}
                        </span>
                        <StatusBadge status={item.invoice.status} />
                      </div>
                      <div className="text-xs font-medium text-slate-700">
                        {item.customerName}
                      </div>
                      <div className="text-xs text-slate-600 flex items-center gap-1">
                        <Clock className="w-3.5 h-3.5" aria-hidden="true" />
                        <span>Issued: {formatDate(item.invoice.issueDate)}</span>
                        <span aria-hidden="true">•</span>
                        <span className={isOverdue ? 'text-rose-700 font-bold' : ''}>
                          Due: {formatDate(item.invoice.dueDate)}
                        </span>
                      </div>
                    </div>

                    <div className="flex items-center gap-4">
                      <div className="text-right space-y-1">
                        <div className="text-sm font-bold text-slate-900">
                          {formatINR(item.invoice.totalAmount)}
                        </div>
                        {item.outstandingPaise > 0 ? (
                          isOverdue ? (
                            <div className="text-xs font-bold text-rose-700">
                              Overdue: {formatINR(item.outstandingPaise)}
                            </div>
                          ) : (
                            <div className="text-xs font-medium text-slate-700">
                              Balance: {formatINR(item.outstandingPaise)}
                            </div>
                          )
                        ) : (
                          <div className="text-xs font-bold text-emerald-700">
                            Settled in full
                          </div>
                        )}
                      </div>
                      <ChevronRight className="w-4 h-4 text-slate-400 group-hover:text-slate-600 transition-colors" aria-hidden="true" />
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
