import React, { useState } from 'react';
import Header from '../components/layout/Header';
import MetricCard from '../components/ui/MetricCard';
import RecordExpenseModal from '../components/cashflow/RecordExpenseModal';
import { useData } from '../data/DataContext';
import { formatINR } from '../domain/money';
import { formatDate } from '../util/invoiceShareFormatter';
import {
  TrendingUp,
  TrendingDown,
  Wallet,
  Plus,
  ArrowUpRight,
  ArrowDownLeft,
  Calendar
} from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid
} from 'recharts';

export default function CashFlowPage() {
  const { cashFlowMetrics, recordExpense } = useData();
  const [isExpenseOpen, setIsExpenseOpen] = useState(false);

  const {
    totalInflowPaise,
    totalOutflowPaise,
    netCashFlowPaise,
    payments,
    expenses
  } = cashFlowMetrics;

  // Chart data: compare Inflow vs Outflow
  const chartData = [
    {
      name: 'Confirmed Inflow',
      amount: totalInflowPaise / 100,
      fill: '#1B4D3E'
    },
    {
      name: 'Total Expenses',
      amount: totalOutflowPaise / 100,
      fill: '#DC2626'
    },
    {
      name: 'Net Cash Flow',
      amount: Math.max(0, netCashFlowPaise / 100),
      fill: '#00796B'
    }
  ];

  // Merge transactions into chronological timeline
  const transactions = [
    ...payments.map(p => ({
      id: p.id,
      title: `Payment Received (${p.paymentMethod})`,
      category: p.referenceNumber ? `Ref: ${p.referenceNumber}` : 'Customer Payment',
      amountPaise: p.amount,
      date: p.paymentDate,
      type: 'INFLOW'
    })),
    ...expenses.map(e => ({
      id: e.id,
      title: e.title,
      category: `Category: ${e.category}`,
      amountPaise: e.amount,
      date: e.expenseDate,
      type: 'OUTFLOW'
    }))
  ].sort((a, b) => b.date - a.date);

  return (
    <div className="pb-24 md:pb-12">
      <Header
        title="Cash Flow"
        subtitle="Inflows, operational outflows, net cash flow & transaction history"
        actionButton={
          <button
            onClick={() => setIsExpenseOpen(true)}
            className="flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold text-white bg-rose-600 hover:bg-rose-700 rounded-xl shadow-xs transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Record Expense</span>
          </button>
        }
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-6xl mx-auto">
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
            subtitle={netCashFlowPaise >= 0 ? 'Surplus cash flow' : 'Deficit'}
            icon={Wallet}
            variant={netCashFlowPaise >= 0 ? 'secondary' : 'error'}
          />
        </div>

        {/* Financial Flow Chart */}
        <div className="bg-white border border-slate-200 rounded-2xl p-5 sm:p-6 shadow-xs space-y-4">
          <h3 className="text-base font-bold text-slate-800">
            Cash Flow Distribution (₹)
          </h3>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData} margin={{ top: 10, right: 10, left: 10, bottom: 20 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#E2E8F0" />
                <XAxis dataKey="name" tick={{ fontSize: 12, fill: '#64748B' }} />
                <YAxis
                  tickFormatter={(val) => `₹${(val / 1000).toFixed(0)}k`}
                  tick={{ fontSize: 12, fill: '#64748B' }}
                />
                <Tooltip
                  formatter={(val) => [`₹${Number(val).toLocaleString('en-IN')}`, 'Amount']}
                  contentStyle={{ borderRadius: '12px', border: '1px solid #E2E8F0' }}
                />
                <Bar dataKey="amount" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Transactions List */}
        <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-xs">
          <div className="p-5 border-b border-slate-100 flex items-center justify-between">
            <h3 className="text-base font-bold text-slate-800">
              Transaction History ({transactions.length})
            </h3>
            <span className="text-xs text-slate-400 font-medium">
              Chronological order
            </span>
          </div>

          <div className="divide-y divide-slate-100">
            {transactions.length === 0 ? (
              <div className="p-8 text-center text-slate-400 text-xs">
                No financial transactions recorded yet.
              </div>
            ) : (
              transactions.map((tx) => (
                <div
                  key={tx.id}
                  className="p-4 sm:p-5 flex items-center justify-between hover:bg-slate-50 transition-colors"
                >
                  <div className="flex items-center gap-3">
                    <div className={`p-2.5 rounded-xl ${tx.type === 'INFLOW' ? 'bg-emerald-50 text-emerald-700' : 'bg-rose-50 text-rose-700'}`}>
                      {tx.type === 'INFLOW' ? (
                        <ArrowDownLeft className="w-4 h-4" />
                      ) : (
                        <ArrowUpRight className="w-4 h-4" />
                      )}
                    </div>
                    <div>
                      <h4 className="text-xs sm:text-sm font-bold text-slate-800">
                        {tx.title}
                      </h4>
                      <div className="text-[11px] text-slate-500 font-medium">
                        {tx.category} • {formatDate(tx.date)}
                      </div>
                    </div>
                  </div>

                  <div className={`text-sm sm:text-base font-extrabold ${tx.type === 'INFLOW' ? 'text-emerald-700' : 'text-rose-600'}`}>
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
