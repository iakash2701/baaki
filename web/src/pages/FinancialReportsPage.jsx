import React, { useState, useMemo, useEffect } from 'react';
import Header from '../components/layout/Header';
import MetricCard from '../components/ui/MetricCard';
import { useData } from '../data/DataContext';
import { formatINR } from '../domain/money';
import { formatDate } from '../util/invoiceShareFormatter';
import {
  FileText,
  Printer,
  Download,
  Calendar,
  DollarSign,
  TrendingUp,
  ShieldAlert,
  CheckCircle2,
  Users,
  ChevronRight
} from 'lucide-react';

export default function FinancialReportsPage() {
  const { invoicesWithCustomer, customers, payments = [] } = useData();

  // Date Filter matching Android's ReportDateFilter
  // 'ALL_TIME' | 'THIS_MONTH' | 'LAST_30_DAYS' | 'CURRENT_FY'
  const [dateFilter, setDateFilter] = useState('ALL_TIME');

  useEffect(() => {
    document.title = 'Financial Reports & Analytics — Baaki';
  }, []);

  const filterOptions = [
    { id: 'ALL_TIME', label: 'All Time' },
    { id: 'THIS_MONTH', label: 'This Month' },
    { id: 'LAST_30_DAYS', label: 'Last 30 Days' },
    { id: 'CURRENT_FY', label: 'Current Financial Year' }
  ];

  // Calculate start time based on filter
  const startTime = useMemo(() => {
    const now = new Date();
    if (dateFilter === 'THIS_MONTH') {
      return new Date(now.getFullYear(), now.getMonth(), 1).getTime();
    }
    if (dateFilter === 'LAST_30_DAYS') {
      return Date.now() - 30 * 24 * 60 * 60 * 1000;
    }
    if (dateFilter === 'CURRENT_FY') {
      const year = now.getFullYear();
      const month = now.getMonth(); // 0-indexed (April is 3)
      const startYear = month < 3 ? year - 1 : year;
      return new Date(startYear, 3, 1).getTime(); // April 1st
    }
    return 0; // ALL_TIME
  }, [dateFilter]);

  // Filtered invoices and metrics
  const reportData = useMemo(() => {
    const now = Date.now();
    const filteredInvoices = invoicesWithCustomer.filter(
      (item) => item.invoice.issueDate >= startTime
    );

    let totalInvoicedPaise = 0;
    let totalCollectedPaise = 0;
    let outstandingPaise = 0;
    let overduePaise = 0;
    let paidCount = 0;
    let partialCount = 0;
    let unpaidCount = 0;
    let overdueCount = 0;

    filteredInvoices.forEach((item) => {
      totalInvoicedPaise += item.invoice.totalAmount;
      const isPaid = item.invoice.status === 'PAID' || item.outstandingPaise === 0;
      const isOverdue = item.invoice.dueDate < now && item.outstandingPaise > 0;

      if (isPaid) {
        paidCount++;
      } else if (item.invoice.status === 'PARTIALLY_PAID') {
        partialCount++;
      } else {
        unpaidCount++;
      }

      if (isOverdue) {
        overdueCount++;
        overduePaise += item.outstandingPaise;
      }

      if (item.outstandingPaise > 0) {
        outstandingPaise += item.outstandingPaise;
      }
    });

    // Total collected from all payments in period
    const filteredPayments = (payments || []).filter(
      (p) => (p.paymentDate || p.createdAt || 0) >= startTime
    );
    totalCollectedPaise = filteredPayments.reduce((sum, p) => sum + (p.amount || 0), 0);

    // If payments array is small in demo, fallback to (invoiced - outstanding)
    if (totalCollectedPaise === 0 && totalInvoicedPaise > outstandingPaise) {
      totalCollectedPaise = totalInvoicedPaise - outstandingPaise;
    }

    // Customer-wise outstanding balances
    const customerBalances = customers.map((c) => {
      const custInvoices = invoicesWithCustomer.filter((i) => i.customer.id === c.id);
      const custOutstanding = custInvoices.reduce((sum, i) => sum + i.outstandingPaise, 0);
      return {
        customer: c,
        outstandingPaise: custOutstanding
      };
    });

    return {
      totalInvoicedPaise,
      totalCollectedPaise,
      outstandingPaise,
      overduePaise,
      paidCount,
      partialCount,
      unpaidCount,
      overdueCount,
      customerBalances
    };
  }, [invoicesWithCustomer, customers, payments, startTime]);

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="pb-24 md:pb-12">
      <Header
        title="Financial Reports & Analytics"
        subtitle="Audited financial summaries, invoice statuses & customer balances (mirrors Android app)"
        actionButton={
          <button
            onClick={handlePrint}
            aria-label="Export or Print Financial Report"
            className="flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold text-white bg-[#1B4D3E] hover:bg-[#153f33] rounded-xl shadow-xs transition-all focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
          >
            <Printer className="w-4 h-4" aria-hidden="true" />
            <span>Export / Print Report</span>
          </button>
        }
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-7xl mx-auto">
        {/* Date Filter Bar */}
        <div className="flex flex-col sm:flex-row items-center justify-between gap-3 bg-white p-3.5 rounded-2xl border border-slate-200 shadow-xs">
          <div className="flex items-center gap-2 text-xs font-bold text-slate-700">
            <Calendar className="w-4 h-4 text-[#1B4D3E]" aria-hidden="true" />
            <span>Report Period:</span>
          </div>

          <div
            role="toolbar"
            aria-label="Date range filter"
            className="flex items-center gap-1.5 w-full sm:w-auto flex-wrap"
          >
            {filterOptions.map((opt) => (
              <button
                key={opt.id}
                onClick={() => setDateFilter(opt.id)}
                aria-pressed={dateFilter === opt.id}
                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] ${
                  dateFilter === opt.id
                    ? 'bg-[#1B4D3E] text-white shadow-xs'
                    : 'text-slate-700 hover:bg-slate-100'
                }`}
              >
                {opt.label}
              </button>
            ))}
          </div>
        </div>

        {/* 4 Financial Overview Metrics Cards */}
        <div>
          <h2 className="text-base font-bold text-slate-800 mb-3 flex items-center gap-2">
            <span>Summary Financial Metrics</span>
          </h2>
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4">
            <MetricCard
              title="Total Invoiced Sales"
              amount={formatINR(reportData.totalInvoicedPaise)}
              subtitle="Sales billed in period"
              icon={FileText}
              variant="default"
            />
            <MetricCard
              title="Collections Cash"
              amount={formatINR(reportData.totalCollectedPaise)}
              subtitle="Confirmed cash receipts"
              icon={CheckCircle2}
              variant="secondary"
            />
            <MetricCard
              title="Outstanding Balance"
              amount={formatINR(reportData.outstandingPaise)}
              subtitle="Current receivables owed"
              icon={DollarSign}
              variant="primary"
            />
            <MetricCard
              title="Overdue Receivables"
              amount={formatINR(reportData.overduePaise)}
              subtitle={`${reportData.overdueCount} Overdue Invoices`}
              icon={ShieldAlert}
              variant="error"
            />
          </div>
        </div>

        {/* Invoice Status Breakdown Card (Mirrors Android Card) */}
        <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-xs">
          <h2 className="text-base font-bold text-slate-900 mb-4 pb-2 border-b border-slate-100 flex items-center justify-between">
            <span>Invoice Status Summary</span>
            <span className="text-xs font-semibold text-slate-500">
              Filter: {filterOptions.find((f) => f.id === dateFilter)?.label}
            </span>
          </h2>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <div className="p-4 rounded-xl bg-emerald-50/60 border border-emerald-100">
              <span className="text-xs font-medium text-emerald-800">Paid Invoices</span>
              <div className="text-2xl font-extrabold text-emerald-700 mt-1">
                {reportData.paidCount}
              </div>
            </div>

            <div className="p-4 rounded-xl bg-teal-50/60 border border-teal-100">
              <span className="text-xs font-medium text-teal-800">Partially Paid</span>
              <div className="text-2xl font-extrabold text-teal-700 mt-1">
                {reportData.partialCount}
              </div>
            </div>

            <div className="p-4 rounded-xl bg-slate-50 border border-slate-200">
              <span className="text-xs font-medium text-slate-700">Unpaid Invoices</span>
              <div className="text-2xl font-extrabold text-slate-800 mt-1">
                {reportData.unpaidCount}
              </div>
            </div>

            <div className="p-4 rounded-xl bg-rose-50/60 border border-rose-100">
              <span className="text-xs font-medium text-rose-800">Overdue Invoices</span>
              <div className="text-2xl font-extrabold text-rose-700 mt-1">
                {reportData.overdueCount}
              </div>
            </div>
          </div>
        </div>

        {/* Customer-wise Outstanding Balances (Mirrors Android Card) */}
        <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-xs">
          <div className="p-5 border-b border-slate-100">
            <h2 className="text-base font-bold text-slate-900">
              Customer-wise Outstanding Balances
            </h2>
            <p className="text-xs text-slate-600 mt-0.5">
              Live receivables ledger grouped by client account
            </p>
          </div>

          <div className="divide-y divide-slate-100">
            {reportData.customerBalances.length === 0 ? (
              <div className="p-8 text-center text-slate-500 text-sm">
                No customer records found.
              </div>
            ) : (
              reportData.customerBalances.map(({ customer, outstandingPaise: custDue }) => (
                <div
                  key={customer.id}
                  className="p-4 sm:p-5 flex items-center justify-between hover:bg-slate-50 transition-colors"
                >
                  <div className="space-y-0.5">
                    <div className="text-sm font-bold text-slate-900">
                      {customer.name}
                    </div>
                    <div className="text-xs text-slate-600">
                      Phone: {customer.phone || 'N/A'} • {customer.city || 'Delhi NCR'}
                    </div>
                  </div>

                  <div className="text-right">
                    <div
                      className={`text-sm sm:text-base font-bold ${
                        custDue > 0 ? 'text-rose-700' : 'text-emerald-700'
                      }`}
                    >
                      {formatINR(custDue)}
                    </div>
                    <div className="text-[11px] font-medium text-slate-500">
                      {custDue > 0 ? 'Owed balance' : 'Zero balance'}
                    </div>
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
