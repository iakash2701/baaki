import React, { createContext, useContext, useState, useEffect, useMemo, useCallback } from 'react';
import {
  StorageKeys,
  loadItem,
  saveItem,
  seedDemoDataIfEmpty,
  resetAllData
} from './storage';
import { scoreInvoices } from '../domain/recoveryScorer';
import { getFinancialYear } from '../util/financialYear';

const DataContext = createContext(null);

export function DataProvider({ children }) {
  const [customers, setCustomers] = useState([]);
  const [invoices, setInvoices] = useState([]);
  const [lineItems, setLineItems] = useState([]);
  const [payments, setPayments] = useState([]);
  const [expenses, setExpenses] = useState([]);
  const [activities, setActivities] = useState([]);
  const [isInitialized, setIsInitialized] = useState(false);

  // Initialize and load from LocalStorage
  const reloadData = useCallback(() => {
    seedDemoDataIfEmpty();
    setCustomers(loadItem(StorageKeys.CUSTOMERS, []));
    setInvoices(loadItem(StorageKeys.INVOICES, []));
    setLineItems(loadItem(StorageKeys.LINE_ITEMS, []));
    setPayments(loadItem(StorageKeys.PAYMENTS, []));
    setExpenses(loadItem(StorageKeys.EXPENSES, []));
    setActivities(loadItem(StorageKeys.ACTIVITIES, []));
    setIsInitialized(true);
  }, []);

  useEffect(() => {
    reloadData();
  }, [reloadData]);

  // Save changes to localStorage
  const updateCustomers = useCallback((newCusts) => {
    setCustomers(newCusts);
    saveItem(StorageKeys.CUSTOMERS, newCusts);
  }, []);

  const updateInvoices = useCallback((newInvs) => {
    setInvoices(newInvs);
    saveItem(StorageKeys.INVOICES, newInvs);
  }, []);

  const updateLineItems = useCallback((newItems) => {
    setLineItems(newItems);
    saveItem(StorageKeys.LINE_ITEMS, newItems);
  }, []);

  const updatePayments = useCallback((newPays) => {
    setPayments(newPays);
    saveItem(StorageKeys.PAYMENTS, newPays);
  }, []);

  const updateExpenses = useCallback((newExps) => {
    setExpenses(newExps);
    saveItem(StorageKeys.EXPENSES, newExps);
  }, []);

  const addActivity = useCallback((title, description, eventType, relatedId = null) => {
    const newAct = {
      id: `act_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`,
      title,
      description,
      eventType, // INVOICE_CREATED, PAYMENT_RECEIVED, CUSTOMER_ADDED, EXPENSE_RECORDED, SYSTEM
      timestamp: Date.now(),
      relatedId
    };
    setActivities(prev => {
      const updated = [newAct, ...prev];
      saveItem(StorageKeys.ACTIVITIES, updated);
      return updated;
    });
  }, []);

  // Action: Add customer
  const addCustomer = useCallback(({ name, phone, email, gstin, address, openingBalancePaise = 0, creditLimitPaise = 10000000 }) => {
    if (!name?.trim() || !phone?.trim()) {
      throw new Error('Business name and phone number are required.');
    }
    const id = `cust_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`;
    const newCustomer = {
      id,
      name: name.trim(),
      phone: phone.trim(),
      email: email?.trim() || null,
      gstin: gstin?.trim() || null,
      address: address?.trim() || null,
      openingBalance: Math.round(Number(openingBalancePaise)) || 0,
      creditLimit: Math.round(Number(creditLimitPaise)) || 10000000,
      createdAt: Date.now()
    };

    const updated = [...customers, newCustomer];
    updateCustomers(updated);
    addActivity('Customer Added', `Added customer ${newCustomer.name}`, 'CUSTOMER_ADDED', id);
    return newCustomer;
  }, [customers, updateCustomers, addActivity]);

  // Action: Generate next invoice number
  const generateNextInvoiceNumber = useCallback((issueDate = Date.now()) => {
    const fy = getFinancialYear(issueDate);
    const countForFy = invoices.filter(inv => inv.invoiceNumber && inv.invoiceNumber.includes(fy)).length;
    return `SP/${fy}/${(countForFy + 1).toString().padStart(3, '0')}`;
  }, [invoices]);

  // Action: Create invoice with line items
  const createInvoice = useCallback(({ customerId, invoiceNumber, issueDate, dueDate, notes, items }) => {
    if (!customerId) throw new Error('Please select a customer.');
    if (!items || items.length === 0) throw new Error('Please add at least one line item.');

    const invoiceId = `inv_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`;
    let subTotal = 0;
    let taxTotal = 0;

    const newLineItemEntities = items.map((item, index) => {
      const qty = Number(item.quantity) || 0;
      const unitPrice = Math.round(Number(item.unitPricePaise)) || 0;
      const taxPercent = Number(item.taxPercent) || 0;

      const lineAmount = Math.round(qty * unitPrice);
      const lineTax = Math.round(lineAmount * (taxPercent / 100));
      const totalLineAmount = lineAmount + lineTax;

      subTotal += lineAmount;
      taxTotal += lineTax;

      return {
        id: `li_${invoiceId}_${index + 1}`,
        invoiceId,
        description: (item.description || '').trim(),
        hsnSac: item.hsnSac?.trim() || null,
        quantity: qty,
        unitPrice,
        taxPercent,
        amount: totalLineAmount
      };
    });

    const totalAmount = subTotal + taxTotal;

    const newInvoice = {
      id: invoiceId,
      invoiceNumber: invoiceNumber.trim(),
      customerId,
      issueDate: Number(issueDate) || Date.now(),
      dueDate: Number(dueDate) || (Date.now() + 15 * 24 * 60 * 60 * 1000),
      subTotal,
      taxTotal,
      discountTotal: 0,
      totalAmount,
      status: 'SENT',
      notes: notes?.trim() || null,
      createdAt: Date.now()
    };

    updateInvoices([newInvoice, ...invoices]);
    updateLineItems([...lineItems, ...newLineItemEntities]);
    addActivity('Invoice Created', `Created invoice ${newInvoice.invoiceNumber} for ₹${(totalAmount / 100).toFixed(2)}`, 'INVOICE_CREATED', invoiceId);

    return newInvoice;
  }, [invoices, lineItems, updateInvoices, updateLineItems, addActivity]);

  // Action: Record payment
  const recordPayment = useCallback(({ invoiceId, amountPaise, paymentMethod = 'UPI', referenceNumber = null, notes = null }) => {
    const inv = invoices.find(i => i.id === invoiceId);
    if (!inv) throw new Error('Invoice not found.');

    const invPayments = payments.filter(p => p.invoiceId === invoiceId);
    const alreadyPaid = invPayments.reduce((sum, p) => sum + (p.amount || 0), 0);
    const outstanding = Math.max(0, inv.totalAmount - alreadyPaid);

    const amt = Math.round(Number(amountPaise));
    if (amt <= 0) throw new Error('Payment amount must be greater than zero.');
    if (amt > outstanding) throw new Error(`Payment cannot exceed outstanding balance of ₹${(outstanding / 100).toFixed(2)}.`);

    const paymentId = `pay_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`;
    const newPayment = {
      id: paymentId,
      invoiceId,
      customerId: inv.customerId,
      amount: amt,
      paymentDate: Date.now(),
      paymentMethod,
      referenceNumber: referenceNumber?.trim() || null,
      notes: notes?.trim() || null,
      createdAt: Date.now()
    };

    const newPaidTotal = alreadyPaid + amt;
    const isNowPaid = newPaidTotal >= inv.totalAmount;

    // Update invoice status if needed
    const updatedInvoices = invoices.map(i => {
      if (i.id === invoiceId) {
        return {
          ...i,
          status: isNowPaid ? 'PAID' : 'PARTIALLY_PAID'
        };
      }
      return i;
    });

    updatePayments([newPayment, ...payments]);
    updateInvoices(updatedInvoices);
    addActivity('Payment Received', `Recorded payment of ₹${(amt / 100).toFixed(2)} via ${paymentMethod} for ${inv.invoiceNumber}`, 'PAYMENT_RECEIVED', paymentId);

    return newPayment;
  }, [invoices, payments, updatePayments, updateInvoices, addActivity]);

  // Action: Record expense
  const recordExpense = useCallback(({ title, category = 'OTHER', amountPaise, expenseDate = Date.now(), notes = null }) => {
    if (!title?.trim()) throw new Error('Expense title is required.');
    const amt = Math.round(Number(amountPaise));
    if (amt <= 0) throw new Error('Expense amount must be greater than zero.');

    const expenseId = `exp_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`;
    const newExpense = {
      id: expenseId,
      title: title.trim(),
      category,
      amount: amt,
      expenseDate: Number(expenseDate) || Date.now(),
      notes: notes?.trim() || null,
      createdAt: Date.now()
    };

    updateExpenses([newExpense, ...expenses]);
    addActivity('Expense Recorded', `Recorded expense ${newExpense.title} of ₹${(amt / 100).toFixed(2)} (${category})`, 'EXPENSE_RECORDED', expenseId);
    return newExpense;
  }, [expenses, updateExpenses, addActivity]);

  // Action: Reset demo
  const resetDemo = useCallback(() => {
    resetAllData();
    reloadData();
  }, [reloadData]);

  // Helper: Detailed view for a single invoice
  const getInvoiceDetails = useCallback((invoiceId) => {
    const invoice = invoices.find(i => i.id === invoiceId);
    if (!invoice) return null;

    const customer = customers.find(c => c.id === invoice.customerId) || null;
    const items = lineItems.filter(li => li.invoiceId === invoiceId);
    const invPayments = payments.filter(p => p.invoiceId === invoiceId);
    const totalPaidPaise = invPayments.reduce((sum, p) => sum + (p.amount || 0), 0);
    const outstandingPaise = Math.max(0, invoice.totalAmount - totalPaidPaise);

    const now = Date.now();
    let status = 'SENT';
    if (outstandingPaise === 0) {
      status = 'PAID';
    } else if (totalPaidPaise > 0) {
      status = 'PARTIALLY_PAID';
    } else if (invoice.dueDate < now) {
      status = 'OVERDUE';
    }

    return {
      invoice,
      customer,
      lineItems: items,
      payments: invPayments.sort((a, b) => b.paymentDate - a.paymentDate),
      totalPaidPaise,
      outstandingPaise,
      status
    };
  }, [invoices, customers, lineItems, payments]);

  // Derived: Invoices with customer name & live outstanding
  const invoicesWithCustomer = useMemo(() => {
    const customerMap = new Map(customers.map(c => [c.id, c]));
    const paymentsByInvoice = new Map();
    payments.forEach(p => {
      if (p.invoiceId) {
        paymentsByInvoice.set(p.invoiceId, (paymentsByInvoice.get(p.invoiceId) || 0) + (p.amount || 0));
      }
    });

    const now = Date.now();

    return invoices.map(inv => {
      const paid = paymentsByInvoice.get(inv.id) || 0;
      const outstanding = Math.max(0, inv.totalAmount - paid);

      let derivedStatus = 'SENT';
      if (outstanding === 0) {
        derivedStatus = 'PAID';
      } else if (paid > 0) {
        derivedStatus = 'PARTIALLY_PAID';
      } else if (inv.dueDate < now) {
        derivedStatus = 'OVERDUE';
      }

      return {
        invoice: { ...inv, status: derivedStatus },
        customerName: customerMap.get(inv.customerId)?.name || 'Unknown Customer',
        customer: customerMap.get(inv.customerId) || null,
        outstandingPaise: outstanding,
        totalPaidPaise: paid
      };
    });
  }, [invoices, customers, payments]);

  // Derived: Customer summaries & intelligence
  const customerInsights = useMemo(() => {
    const invoicesByCustomer = new Map();
    invoices.forEach(inv => {
      const list = invoicesByCustomer.get(inv.customerId) || [];
      list.push(inv);
      invoicesByCustomer.set(inv.customerId, list);
    });

    const paymentsByCustomer = new Map();
    payments.forEach(p => {
      const list = paymentsByCustomer.get(p.customerId) || [];
      list.push(p);
      paymentsByCustomer.set(p.customerId, list);
    });

    const paymentsByInvoice = new Map();
    payments.forEach(p => {
      if (p.invoiceId) {
        const list = paymentsByInvoice.get(p.invoiceId) || [];
        list.push(p);
        paymentsByInvoice.set(p.invoiceId, list);
      }
    });

    const dayMillis = 24 * 60 * 60 * 1000;

    return customers.map(cust => {
      const custInvoices = invoicesByCustomer.get(cust.id) || [];
      const custPayments = paymentsByCustomer.get(cust.id) || [];

      const totalInvoiced = custInvoices.reduce((sum, inv) => sum + (inv.totalAmount || 0), 0);
      const totalPaid = custPayments.reduce((sum, p) => sum + (p.amount || 0), 0);
      
      // Outstanding = openingBalance + totalInvoiced - totalPaid
      const outstanding = Math.max(0, (cust.openingBalance || 0) + totalInvoiced - totalPaid);

      const unpaidInvoiceCount = custInvoices.filter(inv => {
        const invPaid = (paymentsByInvoice.get(inv.id) || []).reduce((sum, p) => sum + p.amount, 0);
        return (inv.totalAmount - invPaid) > 0;
      }).length;

      // Average payment delay on settled invoices
      const delays = [];
      custInvoices.forEach(inv => {
        const invPays = paymentsByInvoice.get(inv.id) || [];
        const paidForInv = invPays.reduce((sum, p) => sum + p.amount, 0);
        if (paidForInv >= inv.totalAmount && invPays.length > 0) {
          const lastPaymentDate = Math.max(...invPays.map(p => p.paymentDate));
          const delayDays = (lastPaymentDate - inv.dueDate) / dayMillis;
          delays.push(delayDays);
        }
      });

      const avgDelay = delays.length >= 2 ? (delays.reduce((s, d) => s + d, 0) / delays.length) : null;

      return {
        customer: cust,
        invoiceCount: custInvoices.length,
        totalInvoicedPaise: totalInvoiced,
        totalPaidPaise: totalPaid,
        outstandingPaise: outstanding,
        unpaidInvoiceCount,
        averagePaymentDelayDays: avgDelay
      };
    }).sort((a, b) => b.outstandingPaise - a.outstandingPaise);
  }, [customers, invoices, payments]);

  // Derived: Dashboard metrics
  const dashboardMetrics = useMemo(() => {
    let totalOutstanding = 0;
    let overdueBalance = 0;
    let overdueCount = 0;
    const now = Date.now();

    invoicesWithCustomer.forEach(item => {
      totalOutstanding += item.outstandingPaise;
      const isOverdue = item.invoice.dueDate < now && item.outstandingPaise > 0;
      if (isOverdue) {
        overdueBalance += item.outstandingPaise;
        overdueCount++;
      }
    });

    const totalCollections = payments.reduce((sum, p) => sum + (p.amount || 0), 0);
    const totalBilled = invoices.reduce((sum, i) => sum + (i.totalAmount || 0), 0);
    const collectionRate = totalBilled > 0 ? Math.round((totalCollections / totalBilled) * 100) : 0;

    const recentInvoices = [...invoicesWithCustomer]
      .sort((a, b) => b.invoice.issueDate - a.invoice.issueDate)
      .slice(0, 5);

    return {
      totalOutstandingPaise: totalOutstanding,
      overdueBalancePaise: overdueBalance,
      overdueCount,
      totalCollectionsPaise: totalCollections,
      totalBilledPaise: totalBilled,
      collectionRate,
      invoiceCount: invoices.length,
      customerCount: customers.length,
      recentInvoices
    };
  }, [invoicesWithCustomer, payments, invoices, customers]);

  // Derived: Cash flow metrics
  const cashFlowMetrics = useMemo(() => {
    const totalInflow = payments.reduce((sum, p) => sum + (p.amount || 0), 0);
    const totalOutflow = expenses.reduce((sum, e) => sum + (e.amount || 0), 0);
    const netCashFlow = totalInflow - totalOutflow;

    return {
      totalInflowPaise: totalInflow,
      totalOutflowPaise: totalOutflow,
      netCashFlowPaise: netCashFlow,
      payments: [...payments].sort((a, b) => b.paymentDate - a.paymentDate),
      expenses: [...expenses].sort((a, b) => b.expenseDate - a.expenseDate)
    };
  }, [payments, expenses]);

  // Derived: Smart recovery queue
  const recoveryItems = useMemo(() => {
    return scoreInvoices(invoices, customers, payments);
  }, [invoices, customers, payments]);

  const value = {
    isInitialized,
    customers,
    invoices,
    lineItems,
    payments,
    expenses,
    activities,
    invoicesWithCustomer,
    customerInsights,
    dashboardMetrics,
    cashFlowMetrics,
    recoveryItems,
    getInvoiceDetails,
    addCustomer,
    createInvoice,
    recordPayment,
    recordExpense,
    generateNextInvoiceNumber,
    resetDemo
  };

  return (
    <DataContext.Provider value={value}>
      {children}
    </DataContext.Provider>
  );
}

export function useData() {
  const context = useContext(DataContext);
  if (!context) {
    throw new Error('useData must be used within a DataProvider');
  }
  return context;
}
