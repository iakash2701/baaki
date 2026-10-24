import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  Home,
  Receipt,
  Users,
  TrendingUp,
  Wallet,
  Star,
  Activity,
  Menu,
  LineChart,
  BarChart3,
  Download
} from 'lucide-react';

export default function Navbar() {
  const navItems = [
    { to: '/', label: 'Home', icon: Home, end: true },
    { to: '/recovery', label: 'Smart Recovery', icon: TrendingUp },
    { to: '/invoices', label: 'Invoices', icon: Receipt },
    { to: '/customers', label: 'Customers', icon: Users },
    { to: '/cashflow', label: 'Cash Flow', icon: Wallet },
    { to: '/reports', label: 'Financial Reports', icon: BarChart3 },
    { to: '/intelligence', label: 'Intelligence', icon: LineChart },
    { to: '/showcase', label: 'Showcase', icon: Star },
    { to: '/more', label: 'More', icon: Menu, ariaLabel: 'More navigation and settings' }
  ];

  // Primary 5 tabs for mobile bottom navigation (mirrors Android Screen.bottomNavItems)
  const mobileNavItems = [
    { to: '/', label: 'Home', icon: Home, end: true },
    { to: '/recovery', label: 'Recovery', icon: TrendingUp },
    { to: '/invoices', label: 'Invoices', icon: Receipt },
    { to: '/reports', label: 'Reports', icon: BarChart3 },
    { to: '/more', label: 'More', icon: Menu, ariaLabel: 'More navigation and settings' }
  ];

  return (
    <>
      {/* Desktop Sidebar */}
      <aside className="hidden md:flex flex-col w-64 bg-white border-r border-[#E2E8F0] min-h-screen fixed left-0 top-0 bottom-0 z-30">
        {/* Brand */}
        <div className="p-6 border-b border-[#E2E8F0] bg-[#F8FAF9]">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-[#1B4D3E] text-white flex items-center justify-center font-bold text-xl shadow-md" aria-hidden="true">
              B
            </div>
            <div>
              <div className="flex items-center gap-1.5 font-extrabold text-lg text-[#111C16] tracking-tight">
                <span role="heading" aria-level="2">Baaki</span>
                <span className="text-xs px-1.5 py-0.5 rounded bg-[#A3E4D7] text-[#0A2920] font-semibold">
                  <abbr title="Professional Edition" className="no-underline">PRO</abbr>
                </span>
              </div>
              <p className="text-xs text-slate-600 font-medium leading-normal">Sharma Packaging 🇮🇳</p>
            </div>
          </div>
        </div>

        {/* Nav Links */}
        <nav aria-label="Main Navigation" className="flex-1 py-4 px-3 overflow-y-auto">
          <h2 className="px-3 mb-2 text-xs font-bold text-slate-600 uppercase tracking-wider">
            Main Menu
          </h2>
          <ul className="space-y-1">
            {navItems.map((item) => {
              const Icon = item.icon;
              return (
                <li key={item.to}>
                  <NavLink
                    to={item.to}
                    end={item.end}
                    aria-label={item.ariaLabel || item.label}
                    className={({ isActive }) =>
                      `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-semibold transition-all focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] ${
                        isActive
                          ? 'bg-[#1B4D3E] text-white shadow-sm'
                          : 'text-slate-700 hover:text-[#111C16] hover:bg-slate-100/70'
                      }`
                    }
                  >
                    <Icon className="w-5 h-5 flex-shrink-0" aria-hidden="true" />
                    <span>{item.label}</span>
                  </NavLink>
                </li>
              );
            })}
          </ul>
        </nav>

        {/* Footer info card with Android APK download */}
        <div className="p-4 border-t border-[#E2E8F0] bg-[#F8FAF9]/80">
          <div className="p-3 bg-white rounded-xl border border-slate-200/80 shadow-xs">
            <div className="flex items-center justify-between text-xs font-bold text-slate-700 mb-1">
              <span>Android App Active</span>
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" aria-hidden="true" />
            </div>
            <p className="text-xs text-slate-600 leading-relaxed">
              v1.0.0 built from source. Offline-first Room database.
            </p>
            <a
              href="/baaki.apk"
              download="Baaki.apk"
              className="mt-2.5 flex items-center justify-center gap-1.5 w-full py-2 px-3 rounded-xl bg-[#1B4D3E] hover:bg-[#153f33] text-white text-xs font-bold shadow-2xs transition-all focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
            >
              <Download className="w-3.5 h-3.5" aria-hidden="true" />
              <span>Download Android APK</span>
            </a>
          </div>
        </div>
      </aside>

      {/* Mobile Bottom Navigation */}
      <nav aria-label="Mobile Navigation" className="md:hidden fixed bottom-0 left-0 right-0 z-40 bg-white/95 backdrop-blur-md border-t border-[#E2E8F0] px-2 py-1 shadow-lg">
        <ul className="flex items-center justify-around list-none m-0 p-0">
          {mobileNavItems.map((item) => {
            const Icon = item.icon;
            return (
              <li key={item.to} className="flex-1 text-center">
                <NavLink
                  to={item.to}
                  end={item.end}
                  aria-label={item.ariaLabel || item.label}
                  className={({ isActive }) =>
                    `flex flex-col items-center justify-center py-1 px-2 min-w-[56px] rounded-lg transition-colors focus:outline-none focus:ring-1 focus:ring-[#1B4D3E] ${
                      isActive
                        ? 'text-[#1B4D3E] font-bold'
                        : 'text-slate-600 font-medium hover:text-slate-900'
                    }`
                  }
                >
                  <Icon className="w-5 h-5 mb-0.5" aria-hidden="true" />
                  <span className="text-xs leading-tight tracking-tight">{item.label}</span>
                </NavLink>
              </li>
            );
          })}
        </ul>
      </nav>
    </>
  );
}
