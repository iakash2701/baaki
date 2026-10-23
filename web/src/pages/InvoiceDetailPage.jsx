import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Header from '../components/layout/Header';
import StatusBadge from '../components/ui/StatusBadge';
import RecordPaymentModal from '../components/invoices/RecordPaymentModal';
import InvoiceShareModal from '../components/invoices/InvoiceShareModal';
import { useData } from '../data/DataContext';
import { formatINR } from '../domain/money';
import { formatDate, formatDateTime } from '../util/invoiceShareFormatter';
import {
  ArrowLeft,
  Share2,
  DollarSign,
  Receipt,
  Building,
  Phone,
  Calendar,
  FileText,
  CreditCard,
  Printer
} from 'lucide-react';

export default function InvoiceDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { getInvoiceDetails, recordPayment } = useData();

  const [isPaymentOpen, setIsPaymentOpen] = useState(false);
  const [isShareOpen, setIsShareOpen] = useState(false);

  const details = getInvoiceDetails(id);

  if (!details || !details.invoice) {
    return (
      <div className="pb-24 px-4 sm:px-8 py-12 max-w-4xl mx-auto text-center space-y-4">
        <h3 className="text-xl font-bold text-slate-800">Invoice Not Found</h3>
        <p className="text-sm text-slate-500">The invoice you requested could not be located in your local records.</p>
        <button
          onClick={() => navigate('/invoices')}
          className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-[#1B4D3E] text-white text-xs font-bold"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Invoices</span>
        </button>
      </div>
    );
  }

  const { invoice, customer, lineItems, payments, totalPaidPaise, outstandingPaise, status } = details;

  return (
    <div className="pb-24 md:pb-12">
      <Header
        title={invoice.invoiceNumber}
        subtitle={`Invoice Details • Sharma Packaging 🇮🇳`}
        actionButton={
          <div className="flex items-center gap-2">
            <button
              onClick={() => setIsShareOpen(true)}
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-bold text-slate-700 bg-white hover:bg-slate-100 border border-slate-200 rounded-xl transition-all shadow-xs"
            >
              <Share2 className="w-3.5 h-3.5" />
              <span>Share</span>
            </button>
            {outstandingPaise > 0 && (
              <button
                onClick={() => setIsPaymentOpen(true)}
                className="flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold text-white bg-[#1B4D3E] hover:bg-[#153f33] rounded-xl shadow-xs transition-all"
              >
                <DollarSign className="w-4 h-4" />
                <span>Record Payment</span>
              </button>
            )}
          </div>
        }
      />

      <div className="px-4 sm:px-8 py-6 space-y-6 max-w-4xl mx-auto">
        <button
          onClick={() => navigate('/invoices')}
          className="inline-flex items-center gap-1.5 text-xs font-bold text-slate-600 hover:text-slate-900 mb-2"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Invoices</span>
        </button>

        {/* Invoice Summary Banner (Mirrors Android InvoiceDetailScreen) */}
        <div className={`p-6 rounded-3xl border shadow-xs ${
          status === 'PAID'
            ? 'bg-emerald-50/70 border-emerald-200'
            : status === 'OVERDUE'
            ? 'bg-rose-50/70 border-rose-200'
            : 'bg-white border-slate-200'
        }`}>
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <div className="flex items-center gap-3 mb-1">
                <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">
                  {invoice.invoiceNumber}
                </h2>
                <StatusBadge status={status} />
              </div>
              <p className="text-sm font-semibold text-slate-700">
                Customer: {customer?.name || 'Unknown'} {customer?.phone ? `(${customer.phone})` : ''}
              </p>
              {customer?.gstin && (
                <p className="text-xs text-slate-500 font-mono mt-0.5">
                  GSTIN: {customer.gstin}
                </p>
              )}
            </div>

            <div className="sm:text-right space-y-1 bg-white/80 p-3 sm:p-0 rounded-xl sm:bg-transparent">
              <div className="text-xs text-slate-500 font-medium">
                Issue Date: <strong className="text-slate-800">{formatDate(invoice.issueDate)}</strong>
              </div>
              <div className="text-xs text-slate-500 font-medium">
                Due Date: <strong className={status === 'OVERDUE' ? 'text-rose-600' : 'text-slate-800'}>{formatDate(invoice.dueDate)}</strong>
              </div>
            </div>
          </div>
        </div>

        {/* Financial Breakdown Card (Mirrors Android InvoiceDetailScreen Financial Summary) */}
        <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-xs space-y-4">
          <h3 className="text-base font-bold text-slate-800 border-b border-slate-100 pb-3">
            Financial Breakdown
          </h3>
          <div className="space-y-2.5 text-sm">
            <div className="flex justify-between text-slate-600">
              <span>Taxable Subtotal</span>
              <span className="font-semibold">{formatINR(invoice.subTotal)}</span>
            </div>
            <div className="flex justify-between text-slate-600">
              <span>GST Total (CGST + SGST / IGST)</span>
              <span className="font-semibold">{formatINR(invoice.taxTotal)}</span>
            </div>
            <div className="flex justify-between text-base font-extrabold text-slate-900 pt-2 border-t border-slate-100">
              <span>Grand Total</span>
              <span>{formatINR(invoice.totalAmount)}</span>
            </div>
            <div className="flex justify-between text-sm font-semibold text-emerald-700">
              <span>Confirmed Paid</span>
              <span>{formatINR(totalPaidPaise)}</span>
            </div>
            <div className="flex justify-between text-base font-extrabold pt-2 border-t border-slate-100">
              <span className={outstandingPaise > 0 ? 'text-rose-600' : 'text-emerald-700'}>
                Outstanding Balance
              </span>
              <span className={outstandingPaise > 0 ? 'text-rose-600' : 'text-emerald-700'}>
                {formatINR(outstandingPaise)}
              </span>
            </div>
          </div>
        </div>

        {/* Line Items Table */}
        <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-xs">
          <div className="p-5 border-b border-slate-100">
            <h3 className="text-base font-bold text-slate-800">
              Line Items ({lineItems.length})
            </h3>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs sm:text-sm">
              <thead className="bg-[#F8FAF9] text-slate-500 font-semibold border-b border-slate-100">
                <tr>
                  <th className="px-5 py-3">Description</th>
                  <th className="px-4 py-3">HSN/SAC</th>
                  <th className="px-4 py-3 text-right">Qty</th>
                  <th className="px-4 py-3 text-right">Unit Price</th>
                  <th className="px-4 py-3 text-right">GST %</th>
                  <th className="px-5 py-3 text-right">Total (₹)</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {lineItems.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-50/50">
                    <td className="px-5 py-3.5 font-semibold text-slate-800">
                      {item.description}
                    </td>
                    <td className="px-4 py-3.5 text-slate-500 font-mono text-xs">
                      {item.hsnSac || '—'}
                    </td>
                    <td className="px-4 py-3.5 text-right font-medium">
                      {item.quantity}
                    </td>
                    <td className="px-4 py-3.5 text-right font-medium">
                      {formatINR(item.unitPrice)}
                    </td>
                    <td className="px-4 py-3.5 text-right text-slate-600">
                      {item.taxPercent}%
                    </td>
                    <td className="px-5 py-3.5 text-right font-bold text-slate-900">
                      {formatINR(item.amount)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Confirmed Payments History */}
        <div className="bg-white border border-slate-200 rounded-2xl p-6 shadow-xs space-y-4">
          <div className="flex items-center justify-between border-b border-slate-100 pb-3">
            <h3 className="text-base font-bold text-slate-800">
              Payment Transactions ({payments.length})
            </h3>
            {outstandingPaise > 0 && (
              <button
                onClick={() => setIsPaymentOpen(true)}
                className="text-xs font-bold text-[#1B4D3E] hover:underline"
              >
                + Record Payment
              </button>
            )}
          </div>

          {payments.length === 0 ? (
            <p className="text-xs text-slate-400 py-3 text-center">
              No payments recorded for this invoice yet.
            </p>
          ) : (
            <div className="divide-y divide-slate-100">
              {payments.map((pay) => (
                <div key={pay.id} className="py-3 flex items-center justify-between text-xs sm:text-sm">
                  <div>
                    <div className="font-semibold text-slate-800">
                      Paid via {pay.paymentMethod}
                    </div>
                    {pay.referenceNumber && (
                      <div className="text-xs text-slate-500 font-mono">
                        Ref: {pay.referenceNumber}
                      </div>
                    )}
                    <div className="text-[11px] text-slate-400">
                      {formatDateTime(pay.paymentDate)}
                    </div>
                  </div>
                  <div className="font-bold text-emerald-700 text-sm sm:text-base">
                    +{formatINR(pay.amount)}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <RecordPaymentModal
        isOpen={isPaymentOpen}
        onClose={() => setIsPaymentOpen(false)}
        invoice={invoice}
        outstandingPaise={outstandingPaise}
        onRecord={recordPayment}
      />

      <InvoiceShareModal
        isOpen={isShareOpen}
        onClose={() => setIsShareOpen(false)}
        invoice={invoice}
        customer={customer}
        lineItems={lineItems}
        totalPaid={totalPaidPaise}
        outstanding={outstandingPaise}
        status={status}
      />
    </div>
  );
}
