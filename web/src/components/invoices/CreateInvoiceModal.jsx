import React, { useState, useEffect } from 'react';
import Modal from '../ui/Modal';
import { Plus, Trash2 } from 'lucide-react';
import { formatINR, rupeesToPaise } from '../../domain/money';

export default function CreateInvoiceModal({
  isOpen,
  onClose,
  customers = [],
  onCreate,
  getNextInvoiceNumber
}) {
  const [customerId, setCustomerId] = useState('');
  const [invoiceNumber, setInvoiceNumber] = useState('');
  const [notes, setNotes] = useState('');
  const [error, setError] = useState('');
  const [dueDateDays, setDueDateDays] = useState('15');

  const [items, setItems] = useState([
    {
      description: 'Corrugated Box 5-Ply',
      hsnSac: '481910',
      quantity: 100,
      unitPriceRupees: '50.00',
      taxPercent: 18.0
    }
  ]);

  useEffect(() => {
    if (isOpen) {
      if (customers.length > 0 && !customerId) {
        setCustomerId(customers[0].id);
      }
      setInvoiceNumber(getNextInvoiceNumber ? getNextInvoiceNumber() : 'SP/2026-27/011');
      setNotes('');
      setError('');
      setDueDateDays('15');
      setItems([
        {
          description: 'Corrugated Box 5-Ply (Batch A)',
          hsnSac: '481910',
          quantity: 100,
          unitPriceRupees: '50.00',
          taxPercent: 18.0
        }
      ]);
    }
  }, [isOpen, customers, getNextInvoiceNumber]);

  if (!isOpen) return null;

  const handleAddItem = () => {
    setItems([
      ...items,
      {
        description: '',
        hsnSac: '4819',
        quantity: 10,
        unitPriceRupees: '0.00',
        taxPercent: 18.0
      }
    ]);
  };

  const handleRemoveItem = (index) => {
    if (items.length <= 1) return;
    setItems(items.filter((_, i) => i !== index));
  };

  const handleItemChange = (index, field, value) => {
    const updated = [...items];
    updated[index] = { ...updated[index], [field]: value };
    setItems(updated);
  };

  // Calculations
  const calculatedItems = items.map(item => {
    const qty = Number(item.quantity) || 0;
    const priceRupees = Number(item.unitPriceRupees) || 0;
    const pricePaise = rupeesToPaise(priceRupees);
    const tax = Number(item.taxPercent) || 0;

    const lineAmount = Math.round(qty * pricePaise);
    const lineTax = Math.round(lineAmount * (tax / 100));
    const totalLineAmount = lineAmount + lineTax;

    return {
      lineAmount,
      lineTax,
      totalLineAmount,
      pricePaise
    };
  });

  const subTotal = calculatedItems.reduce((sum, ci) => sum + ci.lineAmount, 0);
  const taxTotal = calculatedItems.reduce((sum, ci) => sum + ci.lineTax, 0);
  const grandTotal = subTotal + taxTotal;

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');

    if (!customerId) {
      setError('Please select a customer.');
      return;
    }
    if (!invoiceNumber.trim()) {
      setError('Invoice number cannot be blank.');
      return;
    }
    if (items.some(i => !i.description.trim() || Number(i.quantity) <= 0)) {
      setError('All items must have a valid description and quantity (>0).');
      return;
    }

    try {
      const now = Date.now();
      const due = now + (Number(dueDateDays) || 15) * 24 * 60 * 60 * 1000;

      const formattedItems = items.map((item, index) => ({
        description: item.description.trim(),
        hsnSac: item.hsnSac?.trim() || null,
        quantity: Number(item.quantity) || 0,
        unitPricePaise: calculatedItems[index].pricePaise,
        taxPercent: Number(item.taxPercent) || 0
      }));

      onCreate({
        customerId,
        invoiceNumber: invoiceNumber.trim(),
        issueDate: now,
        dueDate: due,
        notes: notes.trim() || null,
        items: formattedItems
      });

      onClose();
    } catch (err) {
      setError(err.message || 'Failed to create invoice');
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Create New Invoice" maxWidth="max-w-2xl">
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && (
          <div role="alert" className="p-3 bg-red-50 border border-red-200 text-red-700 text-xs rounded-xl font-medium">
            {error}
          </div>
        )}

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label htmlFor="customer-select" className="block text-xs font-bold text-slate-700 mb-1.5">
              Customer <span className="text-red-600">*</span>
            </label>
            <select
              id="customer-select"
              required
              value={customerId}
              onChange={(e) => setCustomerId(e.target.value)}
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-semibold bg-white"
            >
              {customers.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name} {c.phone ? `(${c.phone})` : ''}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label htmlFor="invoice-number" className="block text-xs font-bold text-slate-700 mb-1.5">
              Invoice Number <span className="text-red-600">*</span>
            </label>
            <input
              id="invoice-number"
              type="text"
              required
              value={invoiceNumber}
              onChange={(e) => setInvoiceNumber(e.target.value)}
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-semibold"
            />
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label htmlFor="payment-terms" className="block text-xs font-bold text-slate-700 mb-1.5">
              Payment Terms (Due in Days)
            </label>
            <select
              id="payment-terms"
              value={dueDateDays}
              onChange={(e) => setDueDateDays(e.target.value)}
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-medium bg-white"
            >
              <option value="7">7 Days Net</option>
              <option value="15">15 Days Net (Standard)</option>
              <option value="30">30 Days Net</option>
              <option value="45">45 Days Net</option>
            </select>
          </div>

          <div>
            <label htmlFor="invoice-notes" className="block text-xs font-bold text-slate-700 mb-1.5">
              Notes / Terms (Optional)
            </label>
            <input
              id="invoice-notes"
              type="text"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="e.g. 50% advance, balance on delivery"
              className="w-full px-3.5 py-2.5 rounded-xl border border-slate-200 focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] text-sm font-medium"
            />
          </div>
        </div>

        {/* Line Items Section */}
        <div className="pt-2">
          <div className="flex items-center justify-between mb-2">
            <h3 className="text-xs font-bold text-slate-700 uppercase tracking-wider">
              Line Items ({items.length})
            </h3>
            <button
              type="button"
              onClick={handleAddItem}
              className="flex items-center gap-1 text-xs font-bold text-[#1B4D3E] hover:text-[#12382C] focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] rounded px-1"
            >
              <Plus className="w-3.5 h-3.5" aria-hidden="true" />
              <span>Add Item</span>
            </button>
          </div>

          <div className="space-y-3">
            {items.map((item, idx) => (
              <div
                key={idx}
                className="p-3.5 bg-slate-50 border border-slate-200/80 rounded-xl space-y-2.5"
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-slate-700">Item #{idx + 1}</span>
                  {items.length > 1 && (
                    <button
                      type="button"
                      aria-label={`Remove item #${idx + 1}`}
                      onClick={() => handleRemoveItem(idx)}
                      className="text-red-500 hover:text-red-700 p-1 rounded focus:outline-none focus:ring-2 focus:ring-red-500"
                    >
                      <Trash2 className="w-3.5 h-3.5" aria-hidden="true" />
                    </button>
                  )}
                </div>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                  <div>
                    <label htmlFor={`item-desc-${idx}`} className="sr-only">
                      Item {idx + 1} Description
                    </label>
                    <input
                      id={`item-desc-${idx}`}
                      type="text"
                      required
                      placeholder="Description (e.g. Carton Boxes)"
                      value={item.description}
                      onChange={(e) => handleItemChange(idx, 'description', e.target.value)}
                      className="w-full px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-medium focus:ring-1 focus:ring-[#1B4D3E]"
                    />
                  </div>
                  <div>
                    <label htmlFor={`item-hsn-${idx}`} className="sr-only">
                      Item {idx + 1} HSN or SAC
                    </label>
                    <input
                      id={`item-hsn-${idx}`}
                      type="text"
                      placeholder="HSN/SAC (e.g. 4819)"
                      value={item.hsnSac}
                      onChange={(e) => handleItemChange(idx, 'hsnSac', e.target.value)}
                      className="w-full px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-medium focus:ring-1 focus:ring-[#1B4D3E]"
                    />
                  </div>
                </div>

                <div className="grid grid-cols-3 gap-2">
                  <div>
                    <label htmlFor={`item-qty-${idx}`} className="block text-xs font-semibold text-slate-700 mb-0.5">
                      Quantity
                    </label>
                    <input
                      id={`item-qty-${idx}`}
                      type="number"
                      min="1"
                      required
                      value={item.quantity}
                      onChange={(e) => handleItemChange(idx, 'quantity', e.target.value)}
                      className="w-full px-2.5 py-1.5 rounded-lg border border-slate-200 text-xs font-medium focus:ring-1 focus:ring-[#1B4D3E]"
                    />
                  </div>
                  <div>
                    <label htmlFor={`item-price-${idx}`} className="block text-xs font-semibold text-slate-700 mb-0.5">
                      Price (₹)
                    </label>
                    <input
                      id={`item-price-${idx}`}
                      type="number"
                      step="0.01"
                      min="0"
                      required
                      value={item.unitPriceRupees}
                      onChange={(e) => handleItemChange(idx, 'unitPriceRupees', e.target.value)}
                      className="w-full px-2.5 py-1.5 rounded-lg border border-slate-200 text-xs font-medium focus:ring-1 focus:ring-[#1B4D3E]"
                    />
                  </div>
                  <div>
                    <label htmlFor={`item-gst-${idx}`} className="block text-xs font-semibold text-slate-700 mb-0.5">
                      GST %
                    </label>
                    <select
                      id={`item-gst-${idx}`}
                      value={item.taxPercent}
                      onChange={(e) => handleItemChange(idx, 'taxPercent', e.target.value)}
                      className="w-full px-2.5 py-1.5 rounded-lg border border-slate-200 text-xs font-medium focus:ring-1 focus:ring-[#1B4D3E] bg-white"
                    >
                      <option value="0">0%</option>
                      <option value="5">5%</option>
                      <option value="12">12%</option>
                      <option value="18">18% (Standard)</option>
                      <option value="28">28%</option>
                    </select>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Calculation Summary */}
        <div className="p-4 bg-slate-100/70 border border-slate-200 rounded-xl space-y-2 text-xs font-medium">
          <div className="flex justify-between text-slate-700">
            <span>Taxable Subtotal:</span>
            <span>{formatINR(subTotal)}</span>
          </div>
          <div className="flex justify-between text-slate-700">
            <span>GST Amount:</span>
            <span>{formatINR(taxTotal)}</span>
          </div>
          <div className="flex justify-between text-sm font-bold text-[#111C16] pt-1 border-t border-slate-200">
            <span>Grand Total:</span>
            <span className="text-[#1B4D3E]">{formatINR(grandTotal)}</span>
          </div>
        </div>

        <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-100 rounded-xl transition-colors focus:outline-none focus:ring-2 focus:ring-slate-400"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="px-5 py-2 text-sm font-bold text-white bg-[#1B4D3E] hover:bg-[#153f33] rounded-xl shadow-sm transition-all focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
          >
            Save & Issue Invoice
          </button>
        </div>
      </form>
    </Modal>
  );
}
