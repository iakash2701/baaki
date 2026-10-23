import React from 'react';
import { Link } from 'react-router-dom';
import { Home, ArrowLeft } from 'lucide-react';

export default function NotFoundPage() {
  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-[#F8FAF9]">
      <div className="max-w-md w-full bg-white border border-slate-200 rounded-3xl p-8 text-center shadow-sm space-y-4">
        <div className="w-16 h-16 rounded-2xl bg-[#A3E4D7]/40 text-[#1B4D3E] font-black text-2xl flex items-center justify-center mx-auto">
          404
        </div>
        <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">
          Page Not Found
        </h2>
        <p className="text-xs sm:text-sm text-slate-500 leading-relaxed">
          The screen you are trying to visit does not exist or has moved. Return to the dashboard to monitor your invoice collection.
        </p>
        <div className="pt-2">
          <Link
            to="/"
            className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-[#1B4D3E] text-white text-xs sm:text-sm font-bold hover:bg-[#153f33] transition-all shadow-sm"
          >
            <Home className="w-4 h-4" />
            <span>Return to Dashboard</span>
          </Link>
        </div>
      </div>
    </div>
  );
}
