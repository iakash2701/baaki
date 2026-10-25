# baaki

> **Smart Invoice Collection & Cash Flow Intelligence for Indian Small Businesses 🇮🇳**  
> *"Know who owes you. Chase the right payments. Protect your cash flow."*

[![Download Android APK](https://img.shields.io/badge/Download-Android%20APK-1B4D3E?style=for-the-badge&logo=android&logoColor=white)](https://github.com/iakash2701/baaki/raw/main/Baaki.apk)
[![Web Application](https://img.shields.io/badge/Web%20App-React%20%2B%20Vite-00796B?style=for-the-badge&logo=react&logoColor=white)](https://github.com/iakash2701/baaki/tree/main/web)

---

## 📱 Android APK Download

You can directly download and install the native Android APK on any Android device:

👉 **[Download Baaki.apk (v1.0.0)](https://github.com/iakash2701/baaki/raw/main/Baaki.apk)** (~20.8 MB)

## 🌟 Overview

**Baaki** is an offline-ready, mobile-first financial management and invoice collection web application tailored for Indian MSMEs and B2B suppliers. Built with **React**, **Vite**, **Tailwind CSS**, and **Recharts**, it addresses the critical liquidity problem where small businesses supply goods on 15–30 day credit and struggle to track overdue payments, forecast cash shortages, or chase customers effectively.

---

## 🚀 Key Features

- **📊 Executive Dashboard**: Real-time tracking of Total Outstanding, Confirmed Collections, Overdue Receivables, and Active Customers.
- **📑 Financial Reports & Analytics**: Audited financial statements, date filters (All Time, This Month, 30 Days, Current FY), invoice status summaries, and customer receivables breakdown.
- **⚡ Smart Recovery Queue**: Explainable 0–100 priority scoring for overdue invoices based on overdue days, relative balance ratio, partial payment status, and prior ledger opening balances.
- **📄 GST Invoicing**: Create multi-item invoices with live GST calculations (CGST/SGST/IGST), custom payment terms, and automated numbering (`SP/2025-26/XXX`).
- **💰 Payment Recording**: Supports partial or full payments via UPI, Bank Transfer (NEFT/RTGS), Cash, and Cheque with live balance validation.
- **📲 Instant Share & Print**: Generate WhatsApp-ready formatted payment reminders and clean printable invoice summaries.
- **👥 Customer Credit Intelligence**: Analyze historical settlement patterns, average payment delay days, and customer credit reliability.
- **📈 Cash Flow Monitoring**: Visualize inflows vs. operational expenses with interactive Recharts bar charts and an audit transaction log.
- **🏆 Judges Showcase**: Dedicated hackathon tour with guided evaluation workflows.
- **💾 LocalStorage Persistence**: Offline-first persistence with idempotent initialization and demo data reset tools.

---

## 🛠️ Technology Stack

- **Frontend**: React 18, Vite 5, Tailwind CSS
- **Routing**: React Router v6
- **Data Visualization**: Recharts
- **Icons**: Lucide React
- **Typography**: Plus Jakarta Sans
- **Storage**: Browser LocalStorage with idempotent seeding

---

## 💻 Local Development

```bash
# Clone the repository
git clone https://github.com/iakash2701/baaki.git
cd baaki/web

# Install dependencies
npm install

# Start development server
npm run dev
```

Visit `http://localhost:5173/` in your browser.

### Run Production Build & Tests
```bash
# Run unit tests
npm test

# Build for production
npm run build
```

---

## 📄 License

MIT License. Built for Hackathon 2026.
