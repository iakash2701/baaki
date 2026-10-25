import React, { useState, useEffect } from 'react';
import Header from '../components/layout/Header';
import MetricCard from '../components/ui/MetricCard';
import ThemeSelector from '../components/ui/ThemeSelector';
import RecordExpenseModal from '../components/cashflow/RecordExpenseModal';
import { useData } from '../data/DataContext';
import { useTheme } from '../context/ThemeContext';
import { formatINR } from '../domain/money';
import { formatDate } from '../util/invoiceShareFormatter';
import {
  TrendingUp,
  TrendingDown,
  Wallet,
  Plus,
  ArrowUpRight,
  ArrowDownLeft,
  Calendar,
  Layers,
  ArrowDown
} from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
  Legend
} from 'recharts';

export default function CashFlowPage() {
  const { cashFlowMetrics, recordExpense } = useData();
  const { resolvedTheme } = useTheme();
  const [isExpenseOpen, setIsExpenseOpen] = useState(false);

  useEffect(() => {
    document.title = 'Cash Flow Intelligence — Baaki';
  }, []);

  const {
    totalInflowPaise,
    totalOutflowPaise,
    netCashFlowPaise,
    payments,
    expenses
  } = cashFlowMetrics;

  const isDark = resolvedTheme === 'dark';

  // Chart data: compare Inflow vs Outflow
  const chartData = [
    {
      name: 'Confirmed Inflow',
      amount: totalInflowPaise / 100,
      fill: isDark ? '#34D399' : '#1B4D3E' // Accessible mint in dark, forest green in light
    },
    {
      name: 'Total Expenses',
      amount: totalOutflowPaise / 100,
      fill: isDark ? '#F87171' : '#DC2626' // Accessible soft red in dark
    },
    {
      name: 'Net Cash Flow',
      amount: Math.max(0, netCashFlowPaise / 100),
      fill: isDark ? '#38BDF8' : '#00796B' // Accessible sky blue in dark, deep teal in light
    }
  ];

  // Merge transactions into chronological timeline
  const transactions = [
    ...payments.map((p) => ({
      id: p.id,
      title: `Payment Received (${p.paymentMethod})`,
      category: p.referenceNumber ? `Ref: ${p.referenceNumber}` : 'Customer Payment',
      amountPaise: p.amount,
      date: p.paymentDate,
      type: 'INFLOW'
    })),
    ...expenses.map((e) => ({
      id: e.id,
      title: e.title,
      category: `Category: ${e.category}`,
      amountPaise: e.amount,
      date: e.expenseDate,
      type: 'OUTFLOW'
    }))
  ].sort((a, b) => b.date - a.date);

  return (
    <div className="pb-24 md:pb-12 bg-[#F8FAF9] dark:bg-[#0A0A0A] min-h-screen text-[#111C16] dark:text-[#F5F5F5] transition-colors duration-200">
      <Header
        title="Cash Flow"
        subtitle="Inflows, operational outflows, net cash flow & transaction history"
        actionButton={
          <button
            onClick={() => setIsExpenseOpen(true)}
            aria-label="Record Outflow Expense"
            className="flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold text-white bg-rose-600 hover:bg-rose-700 dark:bg-rose-600 dark:hover:bg-rose-500 rounded-xl shadow-xs transition-all focus:outline-none focus:ring-2 focus:ring-rose-500"
          >
            <Plus className="w-4 h-4" aria-hidden="true" />
            <span>Record Expense</span>
          </button>
        }
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-6xl mx-auto">
        {/* Quick Theme & Period Toolbar */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-white dark:bg-[#141414] p-3.5 rounded-2xl border border-slate-200 dark:border-[#2D2D2D] shadow-xs transition-colors duration-200">
          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-slate-700 dark:text-[#F5F5F5]">
              Theme Mode:
            </span>
            <ThemeSelector />
          </div>

          <div className="text-xs font-medium text-slate-500 dark:text-[#A3A3A3] flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" aria-hidden="true" />
            <span>Real-time Indian Business Ledger</span>
          </div>
        </div>

        {/* Metric Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <MetricCard
            title="Total Inflow (Collections)"
            amount={formatINR(totalInflowPaise)}
            subtitle={`${payments.length} Payments recorded`}
            icon={TrendingUp}
            variant="primary"
          />
          <MetricCard
            title="Total Outflow (Expenses)"
            amount={formatINR(totalOutflowPaise)}
            subtitle={`${expenses.length} Expenses recorded`}
            icon={TrendingDown}
            variant="error"
          />
          <MetricCard
            title="Net Cash Position"
            amount={formatINR(netCashFlowPaise)}
            subtitle={netCashFlowPaise >= 0 ? 'Surplus cash flow' : 'Deficit cash flow'}
            icon={Wallet}
            variant={netCashFlowPaise >= 0 ? 'secondary' : 'error'}
          />
        </div>

        {/* Financial Flow Chart */}
        <div className="bg-white dark:bg-[#141414] border border-slate-200 dark:border-[#2D2D2D] rounded-2xl p-5 sm:p-6 shadow-xs space-y-4 transition-colors duration-200">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-bold text-slate-800 dark:text-[#F5F5F5]">
              Cash Flow Distribution (₹)
            </h2>
            <span className="text-xs font-semibold text-slate-500 dark:text-[#A3A3A3]">
              Confirmed vs Outflow
            </span>
          </div>

          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart
                data={chartData}
                margin={{ top: 10, right: 10, left: 10, bottom: 20 }}
              >
                <CartesianGrid
                  strokeDasharray="3 3"
                  vertical={false}
                  stroke={isDark ? '#2D2D2D' : '#E2E8F0'}
                />
                <XAxis
                  dataKey="name"
                  stroke={isDark ? '#A3A3A3' : '#64748B'}
                  tick={{ fontSize: 12, fill: isDark ? '#A3A3A3' : '#64748B' }}
                />
                <YAxis
                  stroke={isDark ? '#A3A3A3' : '#64748B'}
                  tickFormatter={(val) => `₹${(val / 1000).toFixed(0)}k`}
                  tick={{ fontSize: 12, fill: isDark ? '#A3A3A3' : '#64748B' }}
                />
                <Tooltip
                  formatter={(val) => [`₹${Number(val).toLocaleString('en-IN')}`, 'Amount']}
                  contentStyle={
                    isDark
                      ? {
                          backgroundColor: '#141414',
                          borderColor: '#2D2D2D',
                          color: '#F5F5F5',
                          borderRadius: '12px',
                          boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.5)'
                        }
                      : {
                          backgroundColor: '#FFFFFF',
                          borderColor: '#E2E8F0',
                          color: '#0F172A',
                          borderRadius: '12px'
                        }
                  }
                  itemStyle={{ color: isDark ? '#F5F5F5' : '#0F172A' }}
                  labelStyle={{ color: isDark ? '#A3A3A3' : '#64748B', fontWeight: 600 }}
                />
                <Bar dataKey="amount" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Transactions List */}
        <div className="bg-white dark:bg-[#141414] border border-slate-200 dark:border-[#2D2D2D] rounded-2xl overflow-hidden shadow-xs transition-colors duration-200">
          <div className="p-5 border-b border-slate-100 dark:border-[#2D2D2D] flex items-center justify-between">
            <h2 className="text-base font-bold text-slate-800 dark:text-[#F5F5F5]">
              Transaction History ({transactions.length})
            </h2>
            <span className="text-xs text-slate-500 dark:text-[#A3A3A3] font-medium">
              Chronological order
            </span>
          </div>

          <div className="divide-y divide-slate-100 dark:divide-[#2D2D2D]">
            {transactions.length === 0 ? (
              <div className="p-8 text-center text-slate-500 dark:text-[#A3A3A3] text-xs">
                No financial transactions recorded yet.
              </div>
            ) : (
              transactions.map((tx) => (
                <div
                  key={tx.id}
                  className="p-4 sm:p-5 flex items-center justify-between hover:bg-slate-50 dark:hover:bg-[#1C1C1C] transition-colors"
                >
                  <div className="flex items-center gap-3">
                    <div
                      className={`p-2.5 rounded-xl ${
                        tx.type === 'INFLOW'
                          ? 'bg-emerald-50 dark:bg-emerald-950/60 text-emerald-700 dark:text-emerald-400'
                          : 'bg-rose-50 dark:bg-rose-950/60 text-rose-700 dark:text-rose-400'
                      }`}
                      aria-hidden="true"
                    >
                      {tx.type === 'INFLOW' ? (
                        <ArrowDownLeft className="w-4 h-4" />
                      ) : (
                        <ArrowUpRight className="w-4 h-4" />
                      )}
                    </div>
                    <div>
                      <h3 className="text-xs sm:text-sm font-bold text-slate-800 dark:text-[#F5F5F5]">
                        {tx.title}
                      </h3>
                      <div className="text-[11px] text-slate-600 dark:text-[#A3A3A3] font-medium">
                        {tx.category} • {formatDate(tx.date)}
                      </div>
                    </div>
                  </div>

                  <div
                    className={`text-sm sm:text-base font-extrabold ${
                      tx.type === 'INFLOW'
                        ? 'text-emerald-700 dark:text-emerald-400'
                        : 'text-rose-600 dark:text-rose-400'
                    }`}
                  >
                    {tx.type === 'INFLOW' ? `+${formatINR(tx.amountPaise)}` : `-${formatINR(tx.amountPaise)}`}
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>

      <RecordExpenseModal
        isOpen={isExpenseOpen}
        onClose={() => setIsExpenseOpen(false)}
        onRecord={recordExpense}
      />
    </div>
  );
}
