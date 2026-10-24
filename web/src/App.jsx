import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { DataProvider } from './data/DataContext';
import { ThemeProvider } from './context/ThemeContext';
import Navbar from './components/layout/Navbar';

import DashboardPage from './pages/DashboardPage';
import InvoicesPage from './pages/InvoicesPage';
import InvoiceDetailPage from './pages/InvoiceDetailPage';
import CustomersPage from './pages/CustomersPage';
import SmartRecoveryPage from './pages/SmartRecoveryPage';
import CashFlowPage from './pages/CashFlowPage';
import CustomerIntelligencePage from './pages/CustomerIntelligencePage';
import FinancialReportsPage from './pages/FinancialReportsPage';
import JudgesShowcasePage from './pages/JudgesShowcasePage';
import MorePage from './pages/MorePage';
import NotFoundPage from './pages/NotFoundPage';

export default function App() {
  return (
    <ThemeProvider>
      <DataProvider>
        <BrowserRouter>
          <div className="min-h-screen bg-[#F8FAF9] dark:bg-[#0A0A0A] text-[#111C16] dark:text-[#F5F5F5] flex flex-col transition-colors duration-200">
            {/* Skip link for keyboard users (WCAG 2.4.1) */}
            <a
              href="#main-content"
              className="sr-only focus:not-sr-only focus:fixed focus:top-4 focus:left-4 z-50 px-4 py-2 bg-[#1B4D3E] text-white rounded-xl shadow-lg font-bold text-xs focus:outline-none focus:ring-2 focus:ring-emerald-400"
            >
              Skip to main content
            </a>

            <Navbar />
            <main id="main-content" tabIndex={-1} className="flex-1 md:pl-64 transition-all w-full focus:outline-none">
              <Routes>
                <Route path="/" element={<DashboardPage />} />
                <Route path="/invoices" element={<InvoicesPage />} />
                <Route path="/invoices/:id" element={<InvoiceDetailPage />} />
                <Route path="/customers" element={<CustomersPage />} />
                <Route path="/recovery" element={<SmartRecoveryPage />} />
                <Route path="/cashflow" element={<CashFlowPage />} />
                <Route path="/intelligence" element={<CustomerIntelligencePage />} />
                <Route path="/reports" element={<FinancialReportsPage />} />
                <Route path="/showcase" element={<JudgesShowcasePage />} />
                <Route path="/more" element={<MorePage />} />
                <Route path="*" element={<NotFoundPage />} />
              </Routes>
            </main>
          </div>
        </BrowserRouter>
      </DataProvider>
    </ThemeProvider>
  );
}
