/**
 * calculations.test.js — Unit verification of financial rules, GST, and Smart Recovery scoring
 */

import { formatINR, formatCompact, rupeesToPaise, paiseToRupees } from '../domain/money.js';
import { calculateLineItem, calculateInvoiceTotals } from '../domain/gst.js';
import { scoreInvoices, PriorityLevel } from '../domain/recoveryScorer.js';

// Quick assertion runner
let passed = 0;
let failed = 0;

function assert(condition, message) {
  if (!condition) {
    console.error(`❌ FAIL: ${message}`);
    failed++;
  } else {
    console.log(`✅ PASS: ${message}`);
    passed++;
  }
}

console.log('--- Testing Money Calculations ---');
assert(formatINR(150000) === '₹1,500', `formatINR(150000) expected ₹1,500, got ${formatINR(150000)}`);
assert(formatINR(15000000) === '₹1,50,000', `formatINR(15000000) expected ₹1,50,000, got ${formatINR(15000000)}`);
assert(formatINR(15000050) === '₹1,50,000.50', `formatINR(15000050) expected ₹1,50,000.50, got ${formatINR(15000050)}`);
assert(formatCompact(15000000) === '₹1.5L', `formatCompact(15000000) expected ₹1.5L, got ${formatCompact(15000000)}`);
assert(formatCompact(2500000000) === '₹2.5Cr', `formatCompact(2500000000) expected ₹2.5Cr, got ${formatCompact(2500000000)}`);
assert(rupeesToPaise('150.50') === 15050, `rupeesToPaise('150.50') expected 15050, got ${rupeesToPaise('150.50')}`);
assert(paiseToRupees(15050) === 150.5, `paiseToRupees(15050) expected 150.5, got ${paiseToRupees(15050)}`);

console.log('\n--- Testing GST Calculations ---');
const line = calculateLineItem({ quantity: 100, unitPricePaise: 5000, taxPercent: 18 });
assert(line.lineAmount === 500000, `lineAmount expected 500000 (₹5000), got ${line.lineAmount}`);
assert(line.lineTax === 90000, `lineTax expected 90000 (₹900), got ${line.lineTax}`);
assert(line.totalLineAmount === 590000, `totalLineAmount expected 590000 (₹5900), got ${line.totalLineAmount}`);

const totals = calculateInvoiceTotals([
  { quantity: 100, unitPrice: 5000, taxPercent: 18 },
  { quantity: 50, unitPrice: 2000, taxPercent: 12 }
]);
assert(totals.subTotal === 600000, `subTotal expected 600000, got ${totals.subTotal}`);
assert(totals.taxTotal === 102000, `taxTotal expected 102000, got ${totals.taxTotal}`);
assert(totals.totalAmount === 702000, `totalAmount expected 702000, got ${totals.totalAmount}`);

console.log('\n--- Testing Smart Recovery Scoring ---');
const now = Date.now();
const dayMillis = 24 * 60 * 60 * 1000;

const testCustomers = [
  { id: 'c1', name: 'Alpha Corp', openingBalance: 500000 },
  { id: 'c2', name: 'Beta Ltd', openingBalance: 0 }
];

const testInvoices = [
  // 10 days overdue, 100,000 paise (₹1,000), partially paid 20,000 paise
  { id: 'inv1', customerId: 'c1', invoiceNumber: 'TEST/01', totalAmount: 100000, dueDate: now - 10 * dayMillis, status: 'SENT' },
  // 1 day overdue, 50,000 paise, no payments
  { id: 'inv2', customerId: 'c2', invoiceNumber: 'TEST/02', totalAmount: 50000, dueDate: now - 1 * dayMillis, status: 'SENT' }
];

const testPayments = [
  { id: 'p1', invoiceId: 'inv1', customerId: 'c1', amount: 20000 }
];

const scored = scoreInvoices(testInvoices, testCustomers, testPayments, now);
assert(scored.length === 2, `Expected 2 scored items, got ${scored.length}`);
assert(scored[0].invoice.id === 'inv1', `Expected inv1 to rank first due to higher overdue and partial payment, got ${scored[0].invoice.id}`);
assert(scored[0].priorityScore >= 40, `inv1 score expected >= 40, got ${scored[0].priorityScore}`);
assert(scored[0].reasons.some(r => r.includes('days overdue')), 'Expected overdue reason to be present');
assert(scored[0].reasons.some(r => r.includes('Partially paid')), 'Expected partial payment reason to be present');

console.log(`\n========================================`);
console.log(`Results: ${passed} passed, ${failed} failed`);
console.log(`========================================`);

if (failed > 0) {
  process.exit(1);
}
