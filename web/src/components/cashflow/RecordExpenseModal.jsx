import React, { useState } from 'react';
import Modal from '../ui/Modal';
import { rupeesToPaise } from '../../domain/money';

export default function RecordExpenseModal({ isOpen, onClose, onRecord }) {
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('SUPPLIES');
  const [amountRupees, setAmountRupees] = useState('');
  const [notes, setNotes] = useState('');
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');

    if (!title.trim()) {
      setError('Expense title is required.');
      return;
    }

    const paise = rupeesToPaise(amountRupees);
    if (paise <= 0) {
      setError('Amount must be greater than zero.');
      return;
    }

    try {
      onRecord({
        title: title.trim(),
        category,
        amountPaise: paise,
        expenseDate: Date.now(),
        notes: notes.trim() || null
      });
      onClose();
    } catch (err) {
      setError(err.message || 'Failed to record expense');
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Record Business Expense">
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && (
          <div className="p-3 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl font-medium">
            {error}
          </div>
        )}

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Expense Description <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            required
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="e.g. Warehouse Electricity Bill"
            className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-semibold"
          />
        </div>

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Category <span className="text-red-500">*</span>
          </label>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-medium bg-white"
          >
            <option value="SUPPLIES">Supplies & Raw Materials</option>
            <option value="RENT">Rent & Godown</option>
            <option value="SALARY">Salaries & Wages</option>
            <option value="UTILITIES">Utilities & Power</option>
            <option value="LOGISTICS">Transport & Freight</option>
            <option value="OTHER">Other Operational Expense</option>
          </select>
        </div>

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Amount (₹) <span className="text-red-500">*</span>
          </label>
          <input
            type="number"
            step="0.01"
            min="0.01"
            required
            value={amountRupees}
            onChange={(e) => setAmountRupees(e.target.value)}
            placeholder="e.g. 15000"
            className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-semibold"
          />
        </div>

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Notes / Vendor Details (Optional)
          </label>
          <input
            type="text"
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            placeholder="e.g. Paid via company debit card"
            className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-medium"
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
            className="px-5 py-2 text-sm font-bold text-white bg-rose-600 hover:bg-rose-700 rounded-xl shadow-sm transition-all"
          >
            Record Outflow
          </button>
        </div>
      </form>
    </Modal>
  );
}
