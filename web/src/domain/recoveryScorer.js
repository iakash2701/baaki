/**
 * recoveryScorer.js — Exact reproduction of Android SmartRecoveryScorer.kt
 * Scores unpaid / overdue invoices on a 0–100 scale with explainable reasons.
 */

export const PriorityLevel = {
  HIGH: 'HIGH',
  MEDIUM: 'MEDIUM',
  LOW: 'LOW'
};

export function scoreInvoices(invoices, customers, payments, currentTimeMillis = Date.now()) {
  const customerMap = new Map((customers || []).map(c => [c.id, c]));
  
  // Group payments by invoiceId
  const paymentsByInvoice = new Map();
  (payments || []).forEach(p => {
    if (p.invoiceId) {
      const list = paymentsByInvoice.get(p.invoiceId) || [];
      list.push(p);
      paymentsByInvoice.set(p.invoiceId, list);
    }
  });

  // Filter unpaid invoices
  const unpaidInvoices = (invoices || []).filter(inv => {
    const invPayments = paymentsByInvoice.get(inv.id) || [];
    const paid = invPayments.reduce((sum, p) => sum + (p.amount || 0), 0);
    const outstanding = Math.max(0, (inv.totalAmount || 0) - paid);
    return outstanding > 0 && inv.status !== 'PAID';
  });

  if (unpaidInvoices.length === 0) return [];

  // Find max outstanding among unpaid invoices
  let maxOutstanding = 1;
  unpaidInvoices.forEach(inv => {
    const invPayments = paymentsByInvoice.get(inv.id) || [];
    const paid = invPayments.reduce((sum, p) => sum + (p.amount || 0), 0);
    const outstanding = Math.max(0, (inv.totalAmount || 0) - paid);
    if (outstanding > maxOutstanding) maxOutstanding = outstanding;
  });

  const dayMillis = 24 * 60 * 60 * 1000;

  const recoveryItems = unpaidInvoices.map(inv => {
    const customer = customerMap.get(inv.customerId) || {
      id: inv.customerId,
      name: 'Unknown Customer',
      phone: '',
      email: null,
      gstin: null,
      address: null,
      openingBalance: 0,
      creditLimit: 0,
      createdAt: currentTimeMillis
    };

    const invPayments = paymentsByInvoice.get(inv.id) || [];
    const paid = invPayments.reduce((sum, p) => sum + (p.amount || 0), 0);
    const outstanding = Math.max(0, (inv.totalAmount || 0) - paid);

    const diffMillis = currentTimeMillis - inv.dueDate;
    const daysOverdue = diffMillis > 0 ? Math.floor(diffMillis / dayMillis) : 0;

    const reasons = [];
    let score = 10; // Base score

    // 1. Overdue contribution (up to 50 points)
    if (daysOverdue > 0) {
      const overduePoints = Math.min(50, daysOverdue * 2);
      score += overduePoints;
      reasons.push(`${daysOverdue} days overdue`);
    } else {
      const daysUntilDue = Math.floor((-diffMillis) / dayMillis);
      reasons.push(`Due in ${daysUntilDue} days`);
    }

    // 2. Outstanding balance relative contribution (up to 20 points)
    const balanceRatio = outstanding / maxOutstanding;
    const balancePoints = Math.floor(balanceRatio * 20);
    score += balancePoints;
    reasons.push(`₹${(outstanding / 100).toFixed(2)} outstanding`);

    // 3. Partial payment status boost (10 points)
    if (paid > 0) {
      score += 10;
      reasons.push('Partially paid; balance remains');
    }

    // 4. Customer prior ledger opening balance check (10 points)
    if ((customer.openingBalance || 0) > 0) {
      score += 10;
      reasons.push('Has prior ledger opening balance');
    }

    // Coerce score into 0..100
    const finalScore = Math.max(0, Math.min(100, score));

    let level;
    if (finalScore >= 70) {
      level = PriorityLevel.HIGH;
    } else if (finalScore >= 40) {
      level = PriorityLevel.MEDIUM;
    } else {
      level = PriorityLevel.LOW;
    }

    let recommendedAction;
    switch (level) {
      case PriorityLevel.HIGH:
        recommendedAction = 'Urgent follow-up & share reminder';
        break;
      case PriorityLevel.MEDIUM:
        recommendedAction = 'Send payment reminder';
        break;
      case PriorityLevel.LOW:
      default:
        recommendedAction = 'Monitor / Friendly follow-up';
        break;
    }

    return {
      invoice: inv,
      customer,
      outstandingPaise: outstanding,
      totalPaidPaise: paid,
      daysOverdue,
      priorityScore: finalScore,
      priorityLevel: level,
      reasons,
      recommendedAction
    };
  });

  // Sort descending by priorityScore
  return recoveryItems.sort((a, b) => b.priorityScore - a.priorityScore);
}
