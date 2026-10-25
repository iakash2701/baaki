import React, { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import { formatINR, rupeesToPaise } from '../../domain/money';

export default function RecordPaymentModal({
  isOpen,
  onClose,
  invoice,
  outstandingPaise,
  onRecord
}) {
  const [amountRupees, setAmountRupees] = useState('');
  const [method, setMethod] = useState('UPI');
  const [reference, setReference] = useState('');
  const [notes, setNotes] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (isOpen && outstandingPaise != null) {
      setAmountRupees((outstandingPaise / 100).toString());
      setMethod('UPI');
      setReference('');
      setNotes('');
      setError('');
    }
  }, [isOpen, outstandingPaise]);

  if (!isOpen || !invoice) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');

    const paise = rupeesToPaise(amountRupees);
    if (paise <= 0) {
      setError('Payment amount must be greater than zero.');
      return;
    }
    if (paise > outstandingPaise) {
      setError(`Payment cannot exceed outstanding balance of ${formatINR(outstandingPaise)}.`);
      return;
    }

    try {
      onRecord({
        invoiceId: invoice.id,
        amountPaise: paise,
        paymentMethod: method,
        referenceNumber: reference.trim() || null,
        notes: notes.trim() || null
      });
      onClose();
    } catch (err) {
      setError(err.message || 'Failed to record payment');
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`Record Payment: ${invoice.invoiceNumber}`}>
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && (
          <div className="p-3 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl font-medium">
            {error}
          </div>
        )}

        <div className="p-3.5 bg-slate-50 border border-slate-200/80 rounded-xl flex items-center justify-between">
          <span className="text-xs font-semibold text-slate-600">Current Outstanding:</span>
          <span className="text-sm font-bold text-red-600">{formatINR(outstandingPaise)}</span>
        </div>

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Payment Amount (₹) <span className="text-red-500">*</span>
          </label>
          <input
            type="number"
            step="0.01"
            min="0.01"
            max={outstandingPaise / 100}
            required
            value={amountRupees}
            onChange={(e) => setAmountRupees(e.target.value)}
            className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-semibold"
            placeholder="e.g. 5000"
          />
        </div>

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Payment Method <span className="text-red-500">*</span>
          </label>
          <select
            value={method}
            onChange={(e) => setMethod(e.target.value)}
            className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-medium bg-white"
          >
            <option value="UPI">UPI</option>
            <option value="BANK_TRANSFER">Bank Transfer / NEFT / RTGS</option>
            <option value="CASH">Cash</option>
            <option value="CHEQUE">Cheque</option>
          </select>
        </div>

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Transaction Reference / UTR Number
          </label>
          <input
            type="text"
            value={reference}
            onChange={(e) => setReference(e.target.value)}
            className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-medium"
            placeholder="e.g. UPI/REF101 or NEFT204"
          />
        </div>

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Notes (Optional)
          </label>
          <input
            type="text"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-medium"
            placeholder="e.g. Paid in full / advance settlement"
          />
        </div>

        <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100 rounded-xl transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="px-5 py-2 text-sm font-bold text-white bg-[#1B4D3E] hover:bg-[#153f33] rounded-xl shadow-sm transition-all"
          >
            Confirm Payment
          </button>
        </div>
      </form>
    </Modal>
  );
}
