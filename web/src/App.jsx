import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { DataProvider } from './data/DataContext';
import Navbar from './components/layout/Navbar';

import DashboardPage from './pages/DashboardPage';
import InvoicesPage from './pages/InvoicesPage';
import InvoiceDetailPage from './pages/InvoiceDetailPage';
import CustomersPage from './pages/CustomersPage';
import SmartRecoveryPage from './pages/SmartRecoveryPage';
import CashFlowPage from './pages/CashFlowPage';
import CustomerIntelligencePage from './pages/CustomerIntelligencePage';
import JudgesShowcasePage from './pages/JudgesShowcasePage';
import MorePage from './pages/MorePage';
import NotFoundPage from './pages/NotFoundPage';

export default function App() {
  return (
    <DataProvider>
      <BrowserRouter>
        <div className="min-h-screen bg-[#F8FAF9] text-[#111C16] flex flex-col">
          <Navbar />
          <main className="flex-1 md:pl-64 transition-all w-full">
            <Routes>
              <Route path="/" element={<DashboardPage />} />
              <Route path="/invoices" element={<InvoicesPage />} />
              <Route path="/invoices/:id" element={<InvoiceDetailPage />} />
              <Route path="/customers" element={<CustomersPage />} />
              <Route path="/recovery" element={<SmartRecoveryPage />} />
              <Route path="/cashflow" element={<CashFlowPage />} />
              <Route path="/intelligence" element={<CustomerIntelligencePage />} />
              <Route path="/showcase" element={<JudgesShowcasePage />} />
              <Route path="/more" element={<MorePage />} />
              <Route path="*" element={<NotFoundPage />} />
            </Routes>
          </main>
        </div>
      </BrowserRouter>
    </DataProvider>
  );
}
