/**
 * invoiceShareFormatter.js — Exact reproduction of Android InvoiceShareFormatter.kt
 * Generates WhatsApp/SMS shareable summaries for invoices and reminders.
 */

import { formatINR } from '../domain/money';

export function formatDate(timestamp) {
  if (!timestamp) return '';
  const d = new Date(timestamp);
  return d.toLocaleDateString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric'
  });
}

export function formatDateTime(timestamp) {
  if (!timestamp) return '';
  const d = new Date(timestamp);
  return d.toLocaleDateString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

export function generateSummary(invoice, customer, lineItems = [], totalPaid = 0, outstanding = 0, status = 'SENT') {
  const issueStr = formatDate(invoice.issueDate);
  const dueStr = formatDate(invoice.dueDate);

  const lines = [];
  lines.push('*SHARMA PACKAGING* 🇮🇳');
  lines.push('------------------------------------');
  lines.push(`INVOICE: ${invoice.invoiceNumber}`);
  lines.push(`Issue Date: ${issueStr}`);
  lines.push(`Due Date: ${dueStr}`);
  lines.push(`Status: ${status}\n`);

  lines.push('*Customer Details:*');
  lines.push(`Name: ${customer?.name || 'Unknown'}`);
  if (customer?.phone) lines.push(`Phone: ${customer.phone}`);
  if (customer?.gstin) lines.push(`GSTIN: ${customer.gstin}`);
  lines.push('');

  if (lineItems && lineItems.length > 0) {
    lines.push('*Line Items:*');
    lineItems.forEach(item => {
      lines.push(`• ${item.description} (Qty: ${item.quantity}) @ ${formatINR(item.unitPrice)} = ${formatINR(item.amount)}`);
    });
    lines.push('');
  }

  lines.push(`Subtotal: ${formatINR(invoice.subTotal)}`);
  lines.push(`GST Total: ${formatINR(invoice.taxTotal)}`);
  lines.push(`*Grand Total: ${formatINR(invoice.totalAmount)}*`);
  lines.push(`Paid Received: ${formatINR(totalPaid)}`);
  lines.push(`*Outstanding Due: ${formatINR(outstanding)}*`);
  lines.push('------------------------------------');
  lines.push('Thank you for your business! Sent via Baaki.');

  return lines.join('\n');
}

export function generatePaymentReminder(recoveryItem) {
  const { invoice, customer, outstandingPaise, daysOverdue, recommendedAction } = recoveryItem;
  const dueDateStr = formatDate(invoice.dueDate);

  const lines = [];
  lines.push(`*PAYMENT REMINDER • SHARMA PACKAGING* 🇮🇳`);
  lines.push('------------------------------------');
  lines.push(`Dear ${customer?.name || 'Valued Customer'},`);
  lines.push(`This is a gentle follow-up regarding outstanding invoice *${invoice.invoiceNumber}*.`);
  lines.push('');
  lines.push(`Invoice Total: ${formatINR(invoice.totalAmount)}`);
  lines.push(`Due Date: ${dueDateStr} (${daysOverdue > 0 ? `${daysOverdue} days overdue` : 'Due soon'})`);
  lines.push(`*Outstanding Balance: ${formatINR(outstandingPaise)}*`);
  lines.push('');
  lines.push(`Recommended Action: ${recommendedAction}`);
  lines.push('');
  lines.push(`Please arrange the payment via UPI or NEFT at your earliest convenience.`);
  lines.push('UPI ID: sharmapackaging@icici (Mock Demo)');
  lines.push('------------------------------------');
  lines.push('Thank you for your partnership. Sent via Baaki Smart Recovery.');

  return lines.join('\n');
}
