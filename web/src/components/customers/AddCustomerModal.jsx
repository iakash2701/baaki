import React, { useState } from 'react';
import Modal from '../ui/Modal';
import { rupeesToPaise } from '../../domain/money';

export default function AddCustomerModal({ isOpen, onClose, onAdd }) {
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [gstin, setGstin] = useState('');
  const [address, setAddress] = useState('');
  const [openingBalanceRupees, setOpeningBalanceRupees] = useState('');
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');

    if (!name.trim() || !phone.trim()) {
      setError('Business name and phone number are required.');
      return;
    }

    try {
      const openingPaise = rupeesToPaise(openingBalanceRupees || 0);
      onAdd({
        name: name.trim(),
        phone: phone.trim(),
        email: email.trim() || null,
        gstin: gstin.trim().toUpperCase() || null,
        address: address.trim() || null,
        openingBalancePaise: openingPaise,
        creditLimitPaise: 10000000
      });
      onClose();
    } catch (err) {
      setError(err.message || 'Failed to add customer');
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Add New Customer">
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && (
          <div className="p-3 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl font-medium">
            {error}
          </div>
        )}

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Business Name <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="e.g. Apex Logistics"
            className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-semibold"
          />
        </div>

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Phone Number <span className="text-red-500">*</span>
          </label>
          <input
            type="tel"
            required
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            placeholder="e.g. 9876543210"
            className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-semibold"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              Email (Optional)
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="billing@company.com"
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-medium"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1.5">
              GSTIN (Optional)
            </label>
            <input
              type="text"
              value={gstin}
              onChange={(e) => setGstin(e.target.value)}
              placeholder="e.g. 27AABCS1429B1Z6"
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-medium uppercase"
            />
          </div>
        </div>

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Business Address (Optional)
          </label>
          <input
            type="text"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            placeholder="e.g. Andheri East, Mumbai"
            className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-medium"
          />
        </div>

        <div>
          <label className="block text-xs font-bold text-slate-700 mb-1.5">
            Opening Ledger Balance (₹) (Optional)
          </label>
          <input
            type="number"
            step="0.01"
            min="0"
            value={openingBalanceRupees}
            onChange={(e) => setOpeningBalanceRupees(e.target.value)}
            placeholder="e.g. 5000 (if previous balance exists)"
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
            className="px-5 py-2 text-sm font-bold text-white bg-[#1B4D3E] hover:bg-[#153f33] rounded-xl shadow-sm transition-all"
          >
            Save Customer
          </button>
        </div>
      </form>
    </Modal>
  );
}
