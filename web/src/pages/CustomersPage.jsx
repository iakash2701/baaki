import React, { useState } from 'react';
import Header from '../components/layout/Header';
import AddCustomerModal from '../components/customers/AddCustomerModal';
import { useData } from '../data/DataContext';
import { formatINR } from '../domain/money';
import { Plus, Users, Phone, MapPin, Building, ArrowUpRight, Search } from 'lucide-react';

export default function CustomersPage() {
  const { customerInsights, addCustomer } = useData();
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [search, setSearch] = useState('');

  const filteredCustomers = customerInsights.filter(c =>
    c.customer.name.toLowerCase().includes(search.toLowerCase()) ||
    (c.customer.phone && c.customer.phone.includes(search)) ||
    (c.customer.gstin && c.customer.gstin.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="pb-24 md:pb-12">
      <Header
        title="Customers"
        subtitle="Customer ledgers, contact directory & outstanding receivables"
        actionButton={
          <button
            onClick={() => setIsAddOpen(true)}
            className="flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold text-white bg-[#1B4D3E] hover:bg-[#153f33] rounded-xl shadow-xs transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Add Customer</span>
          </button>
        }
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-7xl mx-auto">
        {/* Search */}
        <div className="bg-white p-3.5 rounded-2xl border border-slate-200 shadow-xs flex items-center gap-3">
          <Search className="w-4 h-4 text-slate-400 ml-1" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search customers by name, phone, or GSTIN..."
            className="w-full text-xs sm:text-sm font-medium focus:outline-none bg-transparent"
          />
        </div>

        {/* Customer Cards Grid */}
        {filteredCustomers.length === 0 ? (
          <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center space-y-3">
            <div className="w-12 h-12 rounded-2xl bg-slate-100 text-slate-400 flex items-center justify-center mx-auto">
              <Users className="w-6 h-6" />
            </div>
            <h4 className="text-base font-bold text-slate-700">No Customers Found</h4>
            <p className="text-xs text-slate-500 max-w-sm mx-auto">
              Add your first customer to start tracking credit sales and collecting payments.
            </p>
            <button
              onClick={() => setIsAddOpen(true)}
              className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-[#1B4D3E] text-white font-bold text-xs shadow-xs"
            >
              <Plus className="w-4 h-4" />
              <span>Add First Customer</span>
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filteredCustomers.map(({ customer, invoiceCount, outstandingPaise, totalInvoicedPaise, totalPaidPaise }) => (
              <div
                key={customer.id}
                className="bg-white border border-slate-200 rounded-2xl p-5 hover:border-slate-300 hover:shadow-sm transition-all space-y-4"
              >
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <h3 className="font-extrabold text-base text-slate-900 tracking-tight">
                      {customer.name}
                    </h3>
                    <div className="flex items-center gap-1.5 text-xs text-slate-500 font-medium mt-1">
                      <Phone className="w-3.5 h-3.5 text-slate-400" />
                      <span>{customer.phone}</span>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">
                      Balance Due
                    </span>
                    <span className={`text-sm font-extrabold ${outstandingPaise > 0 ? 'text-rose-600' : 'text-emerald-700'}`}>
                      {formatINR(outstandingPaise)}
                    </span>
                  </div>
                </div>

                <div className="text-xs text-slate-500 space-y-1">
                  {customer.gstin && (
                    <div className="font-mono text-[11px] bg-slate-50 px-2 py-0.5 rounded-md inline-block">
                      GSTIN: {customer.gstin}
                    </div>
                  )}
                  {customer.address && (
                    <div className="flex items-center gap-1.5 text-slate-500 truncate">
                      <MapPin className="w-3 h-3 text-slate-400 flex-shrink-0" />
                      <span className="truncate">{customer.address}</span>
                    </div>
                  )}
                </div>

                <div className="pt-3 border-t border-slate-100 grid grid-cols-3 gap-2 text-center text-xs">
                  <div className="p-2 rounded-xl bg-slate-50">
                    <span className="text-[10px] text-slate-400 block font-semibold">Invoices</span>
                    <span className="font-bold text-slate-800">{invoiceCount}</span>
                  </div>
                  <div className="p-2 rounded-xl bg-slate-50">
                    <span className="text-[10px] text-slate-400 block font-semibold">Billed</span>
                    <span className="font-bold text-slate-800 truncate block">{formatINR(totalInvoicedPaise)}</span>
                  </div>
                  <div className="p-2 rounded-xl bg-emerald-50/60">
                    <span className="text-[10px] text-emerald-700 block font-semibold">Paid</span>
                    <span className="font-bold text-emerald-800 truncate block">{formatINR(totalPaidPaise)}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <AddCustomerModal
        isOpen={isAddOpen}
        onClose={() => setIsAddOpen(false)}
        onAdd={addCustomer}
      />
    </div>
  );
}
