import React, { useState } from 'react';
import Modal from '../ui/Modal';
import { Copy, Check, Printer, Share2 } from 'lucide-react';
import { generateSummary, generatePaymentReminder } from '../../util/invoiceShareFormatter';

export default function InvoiceShareModal({
  isOpen,
  onClose,
  invoice,
  customer,
  lineItems = [],
  totalPaid = 0,
  outstanding = 0,
  status = 'SENT',
  recoveryItem = null
}) {
  const [copied, setCopied] = useState(false);

  if (!invoice && !recoveryItem) return null;

  const summaryText = recoveryItem
    ? generatePaymentReminder(recoveryItem)
    : generateSummary(invoice, customer, lineItems, totalPaid, outstanding, status);

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(summaryText);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (e) {
      console.error('Failed to copy text:', e);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  const title = recoveryItem
    ? `Share Payment Reminder (${recoveryItem.invoice.invoiceNumber})`
    : `Share Invoice Summary (${invoice?.invoiceNumber})`;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title} maxWidth="max-w-xl">
      <div className="space-y-4">
        <p className="text-xs text-slate-500 font-medium">
          Formatted summary ready to copy or share directly with customer on WhatsApp, SMS, or Email:
        </p>

        {/* Text Area display */}
        <div className="p-4 bg-slate-900 text-slate-100 rounded-xl font-mono text-xs whitespace-pre-wrap leading-relaxed max-h-72 overflow-y-auto border border-slate-800">
          {summaryText}
        </div>

        {/* Action Buttons */}
        <div className="flex flex-wrap items-center justify-end gap-3 pt-2">
          <button
            onClick={handlePrint}
            className="flex items-center gap-2 px-4 py-2 text-sm font-semibold text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-xl transition-all"
          >
            <Printer className="w-4 h-4" />
            <span>Print Invoice</span>
          </button>

          <button
            onClick={handleCopy}
            className={`flex items-center gap-2 px-5 py-2 text-sm font-bold rounded-xl transition-all shadow-sm ${
              copied
                ? 'bg-emerald-600 text-white'
                : 'bg-[#1B4D3E] hover:bg-[#153f33] text-white'
            }`}
          >
            {copied ? (
              <>
                <Check className="w-4 h-4" />
                <span>Copied to Clipboard!</span>
              </>
            ) : (
              <>
                <Copy className="w-4 h-4" />
                <span>Copy to Clipboard</span>
              </>
            )}
          </button>
        </div>
      </div>
    </Modal>
  );
}
