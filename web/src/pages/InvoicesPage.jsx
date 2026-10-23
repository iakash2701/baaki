import React, { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/layout/Header';
import StatusBadge from '../components/ui/StatusBadge';
import CreateInvoiceModal from '../components/invoices/CreateInvoiceModal';
import { useData } from '../data/DataContext';
import { formatINR } from '../domain/money';
import { formatDate } from '../util/invoiceShareFormatter';
import { Plus, Search, Filter, Receipt, Clock, AlertCircle } from 'lucide-react';

export default function InvoicesPage() {
  const navigate = useNavigate();
  const {
    invoicesWithCustomer,
    customers,
    createInvoice,
    generateNextInvoiceNumber
  } = useData();

  const [search, setSearch] = useState('');
  const [filterStatus, setFilterStatus] = useState('ALL'); // ALL, PENDING, OVERDUE, PAID
  const [isCreateOpen, setIsCreateOpen] = useState(false);

  // Filtered invoices
  const filteredInvoices = useMemo(() => {
    return invoicesWithCustomer.filter(item => {
      const matchSearch =
        item.invoice.invoiceNumber.toLowerCase().includes(search.toLowerCase()) ||
        item.customerName.toLowerCase().includes(search.toLowerCase());

      if (!matchSearch) return false;

      if (filterStatus === 'ALL') return true;
      if (filterStatus === 'PAID') return item.invoice.status === 'PAID';
      if (filterStatus === 'OVERDUE') return item.invoice.status === 'OVERDUE';
      if (filterStatus === 'PENDING') return item.invoice.status === 'SENT' || item.invoice.status === 'PARTIALLY_PAID';

      return true;
    });
  }, [invoicesWithCustomer, search, filterStatus]);

  return (
    <div className="pb-24 md:pb-12">
      <Header
        title="Invoices"
        subtitle="Manage GST invoices, monitor outstanding balances & record payments"
        actionButton={
          <button
            onClick={() => setIsCreateOpen(true)}
            className="flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold text-white bg-[#1B4D3E] hover:bg-[#153f33] rounded-xl shadow-xs transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Create Invoice</span>
          </button>
        }
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-7xl mx-auto">
        {/* Search & Filter Bar */}
        <div className="flex flex-col sm:flex-row items-center justify-between gap-3 bg-white p-3.5 rounded-2xl border border-slate-200 shadow-xs">
          <div className="relative w-full sm:w-80">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by invoice # or customer..."
              className="w-full pl-9 pr-4 py-2 text-xs sm:text-sm font-medium rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] bg-slate-50/50"
            />
          </div>

          <div className="flex items-center gap-1.5 w-full sm:w-auto overflow-x-auto pb-1 sm:pb-0">
            {['ALL', 'PENDING', 'OVERDUE', 'PAID'].map((status) => (
              <button
                key={status}
                onClick={() => setFilterStatus(status)}
                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all whitespace-nowrap ${
                  filterStatus === status
                    ? 'bg-[#1B4D3E] text-white shadow-xs'
                    : 'text-slate-600 hover:bg-slate-100'
                }`}
              >
                {status}
              </button>
            ))}
          </div>
        </div>

        {/* Invoice List */}
        <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-xs">
          {filteredInvoices.length === 0 ? (
            <div className="p-12 text-center space-y-3">
              <div className="w-12 h-12 rounded-2xl bg-slate-100 text-slate-400 flex items-center justify-center mx-auto">
                <Receipt className="w-6 h-6" />
              </div>
              <h4 className="text-base font-bold text-slate-700">No Invoices Found</h4>
              <p className="text-xs text-slate-500 max-w-sm mx-auto">
                {search || filterStatus !== 'ALL'
                  ? 'No invoices match your current search and filter criteria.'
                  : 'Start issuing professional invoices for your customers.'}
              </p>
              {customers.length > 0 && (
                <button
                  onClick={() => setIsCreateOpen(true)}
                  className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-[#1B4D3E] text-white font-bold text-xs shadow-xs"
                >
                  <Plus className="w-4 h-4" />
                  <span>Create First Invoice</span>
                </button>
              )}
            </div>
          ) : (
            <div className="divide-y divide-slate-100">
              {filteredInvoices.map((item) => (
                <div
                  key={item.invoice.id}
                  onClick={() => navigate(`/invoices/${item.invoice.id}`)}
                  className="p-4 sm:p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-3 hover:bg-slate-50/80 cursor-pointer transition-colors"
                >
                  <div className="space-y-1.5">
                    <div className="flex items-center gap-2.5">
                      <span className="font-extrabold text-sm sm:text-base text-slate-900 tracking-tight">
                        {item.invoice.invoiceNumber}
                      </span>
                      <StatusBadge status={item.invoice.status} />
                    </div>
                    <div className="text-xs sm:text-sm font-semibold text-slate-700">
                      {item.customerName}
                    </div>
                    <div className="text-xs text-slate-400 flex flex-wrap items-center gap-2 sm:gap-4">
                      <span>Issued: {formatDate(item.invoice.issueDate)}</span>
                      <span>•</span>
                      <span className={item.invoice.dueDate < Date.now() && item.outstandingPaise > 0 ? 'text-rose-600 font-semibold' : ''}>
                        Due: {formatDate(item.invoice.dueDate)}
                      </span>
                      {item.invoice.notes && (
                        <>
                          <span>•</span>
                          <span className="truncate max-w-xs">{item.invoice.notes}</span>
                        </>
                      )}
                    </div>
                  </div>

                  <div className="flex items-center justify-between sm:flex-col sm:items-end gap-1 pt-2 sm:pt-0 border-t sm:border-t-0 border-slate-100">
                    <div className="text-sm sm:text-base font-extrabold text-slate-900">
                      {formatINR(item.invoice.totalAmount)}
                    </div>
                    {item.outstandingPaise > 0 ? (
                      <div className="text-xs font-bold text-rose-600">
                        Due: {formatINR(item.outstandingPaise)}
                      </div>
                    ) : (
                      <div className="text-xs font-bold text-emerald-600">
                        Settled in full
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <CreateInvoiceModal
        isOpen={isCreateOpen}
        onClose={() => setIsCreateOpen(false)}
        customers={customers}
        onCreate={createInvoice}
        getNextInvoiceNumber={generateNextInvoiceNumber}
      />
    </div>
  );
}
