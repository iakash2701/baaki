/**
 * storage.js — Multi-user LocalStorage database and seed manager
 * Scopes data strictly per authenticated user (or demo session) using a user prefix.
 * Manages customers, invoices, line items, payments, expenses, and activity logs.
 */

import { getFinancialYear } from '../util/financialYear.js';

export function getStorageKeys(userId = 'demo-user') {
  const prefix = `baaki_user_${userId || 'demo'}_`;
  return {
    CUSTOMERS: `${prefix}customers`,
    INVOICES: `${prefix}invoices`,
    LINE_ITEMS: `${prefix}line_items`,
    PAYMENTS: `${prefix}payments`,
    EXPENSES: `${prefix}expenses`,
    ACTIVITIES: `${prefix}activities`,
    SEEDED: `${prefix}seeded_flag`
  };
}

export function loadItem(key, defaultValue) {
  try {
    const raw = localStorage.getItem(key);
    if (!raw) return defaultValue;
    return JSON.parse(raw);
  } catch (e) {
    console.error(`Error loading ${key} from localStorage:`, e);
    return defaultValue;
  }
}

export function saveItem(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (e) {
    console.error(`Error saving ${key} to localStorage:`, e);
  }
}

export function seedDemoDataIfEmpty(userId = 'demo-user') {
  const keys = getStorageKeys(userId);
  const existingCustomers = loadItem(keys.CUSTOMERS, []);
  if (existingCustomers.length > 0 && localStorage.getItem(keys.SEEDED)) {
    return; // Already seeded for this user
  }

  const now = Date.now();
  const dayMillis = 24 * 60 * 60 * 1000;
  const fy = getFinancialYear(now);

  const c1 = 'cust_gupta_traders';
  const c2 = 'cust_apex_logistics';
  const c3 = 'cust_shree_ram';
  const c4 = 'cust_balaji_pkg';
  const c5 = 'cust_national_logistics';
  const c6 = 'cust_perfect_printers';

  const customers = [
    {
      id: c1,
      userId,
      name: 'Gupta Traders',
      phone: '9876543210',
      email: 'gupta.traders@example.com',
      gstin: '07AABCG1234F1Z5',
      address: 'Chandni Chowk, New Delhi',
      openingBalance: 500000, // ₹5,000.00
      creditLimit: 10000000,
      createdAt: now - 20 * dayMillis
    },
    {
      id: c2,
      userId,
      name: 'Apex Logistics',
      phone: '9123456789',
      email: 'contact@apexlogistics.in',
      gstin: '27AADCA5678G1Z2',
      address: 'Andheri East, Mumbai',
      openingBalance: 0,
      creditLimit: 25000000,
      createdAt: now - 15 * dayMillis
    },
    {
      id: c3,
      userId,
      name: 'Shree Ram Enterprises',
      phone: '9988776655',
      email: null,
      gstin: null,
      address: 'Station Road, Jaipur',
      openingBalance: 120000, // ₹1,200.00
      creditLimit: 5000000,
      createdAt: now - 12 * dayMillis
    },
    {
      id: c4,
      userId,
      name: 'Balaji Packaging',
      phone: '9811122233',
      email: 'orders@balajipkg.co.in',
      gstin: '24AABCB9876Q1Z1',
      address: 'Gandhi Nagar, Ahmedabad',
      openingBalance: 0,
      creditLimit: 15000000,
      createdAt: now - 10 * dayMillis
    },
    {
      id: c5,
      userId,
      name: 'National Logistics',
      phone: '9700011122',
      email: null,
      gstin: null,
      address: 'Park Street, Kolkata',
      openingBalance: 0,
      creditLimit: 10000000,
      createdAt: now - 8 * dayMillis
    },
    {
      id: c6,
      userId,
      name: 'Perfect Printers',
      phone: '9655544332',
      email: 'hello@perfectprinters.in',
      gstin: '33AABCP5555R1Z9',
      address: 'Mount Road, Chennai',
      openingBalance: 0,
      creditLimit: 5000000,
      createdAt: now - 2 * dayMillis
    }
  ];

  const invData = [
    { custId: c1, invNo: `SP/${fy}/001`, amount: 2900000 },
    { custId: c1, invNo: `SP/${fy}/002`, amount: 4500000 },
    { custId: c2, invNo: `SP/${fy}/003`, amount: 12000000 },
    { custId: c2, invNo: `SP/${fy}/004`, amount: 8500000 },
    { custId: c3, invNo: `SP/${fy}/005`, amount: 1500000 },
    { custId: c3, invNo: `SP/${fy}/006`, amount: 3200000 },
    { custId: c4, invNo: `SP/${fy}/007`, amount: 6500000 },
    { custId: c4, invNo: `SP/${fy}/008`, amount: 4100000 },
    { custId: c5, invNo: `SP/${fy}/009`, amount: 1800000 },
    { custId: c5, invNo: `SP/${fy}/010`, amount: 2500000 }
  ];

  const invoices = [];
  const lineItems = [];
  const payments = [];

  invData.forEach((item, index) => {
    const invId = `inv_${index + 1}`;
    const sub = Math.round(item.amount * 0.85);
    const tax = item.amount - sub;
    const issue = now - ((15 - index) * dayMillis);
    const due = issue + (10 * dayMillis);

    const isPaid = (index === 2 || index === 8 || index === 9);
    const status = isPaid ? 'PAID' : (due < now ? 'OVERDUE' : 'SENT');

    invoices.push({
      id: invId,
      userId,
      invoiceNumber: item.invNo,
      customerId: item.custId,
      issueDate: issue,
      dueDate: due,
      subTotal: sub,
      taxTotal: tax,
      discountTotal: 0,
      totalAmount: item.amount,
      status: status,
      notes: `Order batch #${index + 1}`,
      createdAt: issue
    });

    lineItems.push({
      id: `li_${index + 1}`,
      userId,
      invoiceId: invId,
      description: `Industrial Corrugated Boxes Batch ${index + 1}`,
      hsnSac: '481910',
      quantity: 200,
      unitPrice: Math.round(sub / 200),
      taxPercent: 18.0,
      amount: item.amount
    });

    if (index === 0) {
      payments.push({
        id: 'pay_1',
        userId,
        invoiceId: invId,
        customerId: item.custId,
        amount: 1000000, // ₹10,000
        paymentDate: issue + 2 * dayMillis,
        paymentMethod: 'UPI',
        referenceNumber: 'UPI/REF101',
        notes: 'Partial payment',
        createdAt: issue + 2 * dayMillis
      });
    } else if (isPaid) {
      payments.push({
        id: `pay_${index + 1}`,
        userId,
        invoiceId: invId,
        customerId: item.custId,
        amount: item.amount,
        paymentDate: issue + 3 * dayMillis,
        paymentMethod: 'BANK_TRANSFER',
        referenceNumber: `NEFT/REF20${index}`,
        notes: 'Full settlement',
        createdAt: issue + 3 * dayMillis
      });
    } else if (index === 4) {
      payments.push({
        id: 'pay_advance_c3',
        userId,
        invoiceId: invId,
        customerId: item.custId,
        amount: 500000, // ₹5,000
        paymentDate: issue + 2 * dayMillis,
        paymentMethod: 'CASH',
        referenceNumber: null,
        notes: 'Advance cash',
        createdAt: issue + 2 * dayMillis
      });
    }
  });

  const expenses = [
    {
      id: 'exp_1',
      userId,
      title: 'Godown Rent',
      category: 'RENT',
      amount: 1500000, // ₹15,000
      expenseDate: now - 8 * dayMillis,
      notes: 'Monthly godown warehouse rent',
      createdAt: now - 8 * dayMillis
    },
    {
      id: 'exp_2',
      userId,
      title: 'Kraft Paper Raw Material',
      category: 'SUPPLIES',
      amount: 4500000, // ₹45,000
      expenseDate: now - 5 * dayMillis,
      notes: 'Paper mill purchase batch #94',
      createdAt: now - 5 * dayMillis
    }
  ];

  const activities = [
    {
      id: 'act_seed',
      userId,
      title: 'Data Initialized',
      description: 'Workspace ready with default ledger and sample invoices.',
      eventType: 'SYSTEM',
      timestamp: now,
      relatedId: null
    }
  ];

  saveItem(keys.CUSTOMERS, customers);
  saveItem(keys.INVOICES, invoices);
  saveItem(keys.LINE_ITEMS, lineItems);
  saveItem(keys.PAYMENTS, payments);
  saveItem(keys.EXPENSES, expenses);
  saveItem(keys.ACTIVITIES, activities);
  localStorage.setItem(keys.SEEDED, 'true');
}

export function resetAllData(userId = 'demo-user') {
  const keys = getStorageKeys(userId);
  localStorage.removeItem(keys.CUSTOMERS);
  localStorage.removeItem(keys.INVOICES);
  localStorage.removeItem(keys.LINE_ITEMS);
  localStorage.removeItem(keys.PAYMENTS);
  localStorage.removeItem(keys.EXPENSES);
  localStorage.removeItem(keys.ACTIVITIES);
  localStorage.removeItem(keys.SEEDED);
  seedDemoDataIfEmpty(userId);
}
