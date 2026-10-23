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
  Menu
} from 'lucide-react';

export default function Navbar() {
  const navItems = [
    { to: '/', label: 'Home', icon: Home, end: true },
    { to: '/recovery', label: 'Smart Recovery', icon: TrendingUp },
    { to: '/invoices', label: 'Invoices', icon: Receipt },
    { to: '/customers', label: 'Customers', icon: Users },
    { to: '/cashflow', label: 'Cash Flow', icon: Wallet },
    { to: '/intelligence', label: 'Intelligence', icon: Users },
    { to: '/showcase', label: 'Showcase', icon: Star },
    { to: '/more', label: 'More', icon: Menu }
  ];

  // Primary 5 tabs for mobile bottom navigation (mirrors Android Screen.bottomNavItems)
  const mobileNavItems = [
    { to: '/', label: 'Home', icon: Home, end: true },
    { to: '/recovery', label: 'Recovery', icon: TrendingUp },
    { to: '/invoices', label: 'Invoices', icon: Receipt },
    { to: '/customers', label: 'Customers', icon: Users },
    { to: '/more', label: 'More', icon: Menu }
  ];

  return (
    <>
      {/* Desktop Sidebar */}
      <aside className="hidden md:flex flex-col w-64 bg-white border-r border-[#E2E8F0] min-h-screen fixed left-0 top-0 bottom-0 z-30">
        {/* Brand */}
        <div className="p-6 border-b border-[#E2E8F0] bg-[#F8FAF9]">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-[#1B4D3E] text-white flex items-center justify-center font-bold text-xl shadow-md">
              B
            </div>
            <div>
              <div className="flex items-center gap-1.5 font-extrabold text-lg text-[#111C16] tracking-tight">
                <span>Baaki</span>
                <span className="text-xs px-1.5 py-0.5 rounded bg-[#A3E4D7] text-[#0A2920] font-semibold">
                  PRO
                </span>
              </div>
              <p className="text-xs text-slate-500 font-medium">Sharma Packaging 🇮🇳</p>
            </div>
          </div>
        </div>

        {/* Nav Links */}
        <div className="flex-1 py-6 px-3 space-y-1 overflow-y-auto">
          <div className="px-3 mb-2 text-xs font-semibold text-slate-400 uppercase tracking-wider">
            Main Menu
          </div>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-semibold transition-all ${
                    isActive
                      ? 'bg-[#1B4D3E] text-white shadow-sm'
                      : 'text-slate-600 hover:text-[#111C16] hover:bg-slate-100/70'
                  }`
                }
              >
                <Icon className="w-5 h-5 flex-shrink-0" />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </div>

        {/* Footer info card */}
        <div className="p-4 border-t border-[#E2E8F0] bg-[#F8FAF9]/80">
          <div className="p-3 bg-white rounded-xl border border-slate-200/80 shadow-xs">
            <div className="flex items-center justify-between text-xs font-bold text-slate-700 mb-1">
              <span>Collection Engine</span>
              <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse" />
            </div>
            <p className="text-xs text-slate-500 leading-relaxed">
              Offline-ready intelligence for Indian B2B suppliers.
            </p>
          </div>
        </div>
      </aside>

      {/* Mobile Bottom Navigation (Responsive 360px+ friendly, exactly like Android) */}
      <nav className="md:hidden fixed bottom-0 left-0 right-0 z-40 bg-white/95 backdrop-blur-md border-t border-[#E2E8F0] px-2 py-1 shadow-lg">
        <div className="flex items-center justify-around">
          {mobileNavItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.to}
                to={item.to}
                end={item.end}
                className={({ isActive }) =>
                  `flex flex-col items-center justify-center py-1 px-2 min-w-[56px] rounded-lg transition-colors ${
                    isActive
                      ? 'text-[#1B4D3E] font-bold'
                      : 'text-slate-500 font-medium hover:text-slate-800'
                  }`
                }
              >
                <Icon className="w-5 h-5 mb-0.5" />
                <span className="text-[10px] leading-tight tracking-tight">{item.label}</span>
              </NavLink>
            );
          })}
        </div>
      </nav>
    </>
  );
}
