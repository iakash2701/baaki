import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { Sun, Moon, Monitor, AlertCircle, Loader2 } from 'lucide-react';

function GoogleIcon({ className }) {
  return (
    <svg className={className} viewBox="0 0 24 24" aria-hidden="true">
      <path
        fill="#4285F4"
        d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
      />
      <path
        fill="#34A853"
        d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
      />
      <path
        fill="#FBBC05"
        d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
      />
      <path
        fill="#EA4335"
        d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
      />
    </svg>
  );
}

function AppleIcon({ className }) {
  return (
    <svg className={className} viewBox="0 0 170 170" fill="currentColor" aria-hidden="true">
      <path d="M150.37 130.25c-2.45 5.66-5.35 10.87-8.71 15.66-4.58 6.53-8.33 11.05-11.22 13.56-4.48 4.12-9.28 6.23-14.42 6.35-3.69 0-8.14-1.05-13.32-3.18-5.19-2.12-9.97-3.17-14.34-3.17-4.58 0-9.49 1.05-14.75 3.17-5.26 2.13-9.5 3.24-12.74 3.35-4.35.13-9.16-1.9-14.42-6.08-3.7-3.04-7.58-7.7-11.64-13.99-5.99-9.13-10.74-19.8-14.25-32.01-3.52-12.21-5.27-23.77-5.27-34.69 0-14.15 3.43-26.06 10.29-35.73 6.86-9.67 15.66-14.61 26.4-14.82 4.58 0 9.87 1.25 15.86 3.75 5.99 2.5 10.02 3.82 12.09 3.96 1.85-.27 5.99-1.63 12.43-4.08 6.44-2.45 11.83-3.56 16.17-3.33 12.21.65 22.08 5.27 29.6 13.86-10.78 6.53-16.05 15.49-15.81 26.89.24 8.92 3.73 16.42 10.47 22.5 6.74 6.09 14.75 9.47 24.03 10.13-2.18 6.96-5.06 14.58-8.66 22.86zM119.22 31.84c0-7.72 2.77-14.97 8.31-21.75 5.54-6.78 12.39-10.74 20.55-11.89.22 1.09.33 2.18.33 3.27 0 7.61-2.88 15.02-8.64 22.23-5.76 7.21-12.77 11.27-21.03 12.18-.11-1.3-.17-2.61-.17-4.04z" />
    </svg>
  );
}

// Microsoft icon (SVG path — not in lucide)
function MicrosoftIcon({ className }) {
  return (
    <svg className={className} viewBox="0 0 21 21" fill="none" aria-hidden="true">
      <rect x="1" y="1" width="9" height="9" fill="#F25022" />
      <rect x="11" y="1" width="9" height="9" fill="#7FBA00" />
      <rect x="1" y="11" width="9" height="9" fill="#00A4EF" />
      <rect x="11" y="11" width="9" height="9" fill="#FFB900" />
    </svg>
  );
}

export default function SignInPage() {
  const navigate   = useNavigate();
  const { theme, setTheme } = useTheme();
  const {
    isAuthenticated,
    loading,
    error,
    isSupabaseConfigured,
    signInWithGoogle,
    signInWithMicrosoft,
    signInWithApple,
    signInWithEmail,
    signUpWithEmail,
    signInAsDemo,
    clearError,
  } = useAuth();

  const [mode, setMode]         = useState('signin'); // 'signin' | 'signup'
  const [emailMode, setEmailMode] = useState(false);
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [busy, setBusy]         = useState(false);
  const [localErr, setLocalErr] = useState('');

  // If already authenticated, redirect to dashboard
  useEffect(() => {
    if (!loading && isAuthenticated) navigate('/', { replace: true });
  }, [isAuthenticated, loading, navigate]);

  // Keep page title fresh
  useEffect(() => { document.title = 'Sign In — Baaki'; }, []);

  async function handleOAuth(fn) {
    clearError();
    setLocalErr('');
    setBusy(true);
    try { await fn(); } catch { /* handled in context */ }
    finally { setBusy(false); }
  }

  async function handleEmailSubmit(e) {
    e.preventDefault();
    clearError();
    setLocalErr('');
    if (!email || !password) { setLocalErr('Please enter email and password.'); return; }
    if (password.length < 6) { setLocalErr('Password must be at least 6 characters.'); return; }
    setBusy(true);
    try {
      if (mode === 'signup') await signUpWithEmail(email, password);
      else await signInWithEmail(email, password);
    } finally { setBusy(false); }
  }

  const displayError = localErr || error;

  const themeIcons = { light: Sun, dark: Moon, system: Monitor };
  const themeLabels = { light: 'Light', dark: 'Dark', system: 'System' };
  const themeOrder = ['light', 'dark', 'system'];

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-[#F8FAF9] dark:bg-[#0A0A0A]">
        <Loader2 className="w-8 h-8 animate-spin text-[#1B4D3E]" aria-label="Loading…" />
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col bg-[#F8FAF9] dark:bg-[#0A0A0A] transition-colors duration-200">
      {/* Top bar */}
      <header className="flex items-center justify-between px-6 py-4">
        {/* Brand */}
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-[#1B4D3E] text-white flex items-center justify-center font-bold text-lg shadow-md" aria-hidden="true">
            B
          </div>
          <span className="font-extrabold text-lg text-[#111C16] dark:text-[#F5F5F5] tracking-tight">
            Baaki
            <span className="ml-1.5 text-xs px-1.5 py-0.5 rounded bg-[#A3E4D7] text-[#0A2920] font-semibold">PRO</span>
          </span>
        </div>

        {/* Theme toggle */}
        <div
          role="group"
          aria-label="Choose theme"
          className="flex items-center gap-1 bg-white dark:bg-[#141414] rounded-xl p-1 border border-[#E2E8F0] dark:border-[#2D2D2D] shadow-sm"
        >
          {themeOrder.map((t) => {
            const Icon = themeIcons[t];
            const isActive = theme === t;
            return (
              <button
                key={t}
                onClick={() => setTheme(t)}
                aria-label={`${themeLabels[t]} theme`}
                aria-pressed={isActive}
                className={`p-1.5 rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] ${
                  isActive
                    ? 'bg-[#1B4D3E] text-white shadow'
                    : 'text-slate-500 dark:text-[#A3A3A3] hover:text-[#111C16] dark:hover:text-[#F5F5F5]'
                }`}
              >
                <Icon className="w-4 h-4" aria-hidden="true" />
              </button>
            );
          })}
        </div>
      </header>

      {/* Card */}
      <main className="flex-1 flex items-center justify-center px-4 py-8" id="main-content">
        <div className="w-full max-w-md">
          <div className="bg-white dark:bg-[#141414] rounded-2xl shadow-xl border border-[#E2E8F0] dark:border-[#2D2D2D] p-8">
            {/* Heading */}
            <div className="text-center mb-8">
              <h1 className="text-2xl font-extrabold text-[#111C16] dark:text-[#F5F5F5] tracking-tight mb-1">
                {mode === 'signup' ? 'Create your account' : 'Welcome back'}
              </h1>
              <p className="text-sm text-slate-500 dark:text-[#A3A3A3]">
                {mode === 'signup'
                  ? 'Sign up to manage your invoices & cash flow'
                  : 'Sign in to your Baaki workspace'}
              </p>
            </div>

            {/* Error banner */}
            {displayError && (
              <div
                role="alert"
                className="flex items-start gap-2.5 mb-5 p-3 rounded-xl bg-red-50 dark:bg-red-950/30 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-400 text-sm"
              >
                <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5" aria-hidden="true" />
                <span>{displayError}</span>
              </div>
            )}

            {/* OAuth buttons (only when Supabase configured) */}
            {isSupabaseConfigured && !emailMode && (
              <div className="space-y-3 mb-5">
                <button
                  onClick={() => handleOAuth(signInWithGoogle)}
                  disabled={busy}
                  className="w-full flex items-center justify-center gap-3 px-4 py-2.5 rounded-xl border border-[#E2E8F0] dark:border-[#2D2D2D] bg-white dark:bg-[#1C1C1C] text-[#111C16] dark:text-[#F5F5F5] font-semibold text-sm hover:bg-slate-50 dark:hover:bg-[#242424] transition-colors focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] disabled:opacity-60"
                >
                  {busy ? <Loader2 className="w-5 h-5 animate-spin" aria-hidden="true" /> : <GoogleIcon className="w-5 h-5" />}
                  <span>Continue with Google</span>
                </button>

                <button
                  onClick={() => handleOAuth(signInWithMicrosoft)}
                  disabled={busy}
                  className="w-full flex items-center justify-center gap-3 px-4 py-2.5 rounded-xl border border-[#E2E8F0] dark:border-[#2D2D2D] bg-white dark:bg-[#1C1C1C] text-[#111C16] dark:text-[#F5F5F5] font-semibold text-sm hover:bg-slate-50 dark:hover:bg-[#242424] transition-colors focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] disabled:opacity-60"
                >
                  {busy ? <Loader2 className="w-5 h-5 animate-spin" aria-hidden="true" /> : <MicrosoftIcon className="w-5 h-5" />}
                  <span>Continue with Microsoft</span>
                </button>

                <button
                  onClick={() => handleOAuth(signInWithApple)}
                  disabled={busy}
                  className="w-full flex items-center justify-center gap-3 px-4 py-2.5 rounded-xl border border-[#E2E8F0] dark:border-[#2D2D2D] bg-white dark:bg-[#1C1C1C] text-[#111C16] dark:text-[#F5F5F5] font-semibold text-sm hover:bg-slate-50 dark:hover:bg-[#242424] transition-colors focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] disabled:opacity-60"
                >
                  {busy ? <Loader2 className="w-5 h-5 animate-spin" aria-hidden="true" /> : <AppleIcon className="w-5 h-5" />}
                  <span>Continue with Apple</span>
                </button>

                <div className="relative my-4">
                  <div className="absolute inset-0 flex items-center" aria-hidden="true">
                    <div className="w-full border-t border-[#E2E8F0] dark:border-[#2D2D2D]" />
                  </div>
                  <div className="relative flex justify-center text-xs">
                    <span className="px-3 bg-white dark:bg-[#141414] text-slate-500 dark:text-[#A3A3A3] font-medium">
                      or use email
                    </span>
                  </div>
                </div>

                <button
                  onClick={() => setEmailMode(true)}
                  className="w-full py-2.5 px-4 rounded-xl border border-[#E2E8F0] dark:border-[#2D2D2D] text-[#111C16] dark:text-[#F5F5F5] font-semibold text-sm hover:bg-slate-50 dark:hover:bg-[#1C1C1C] transition-colors focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
                >
                  Continue with Email
                </button>
              </div>
            )}

            {/* Email form */}
            {(emailMode || !isSupabaseConfigured) && isSupabaseConfigured && (
              <form onSubmit={handleEmailSubmit} noValidate className="space-y-4 mb-5">
                <div>
                  <label htmlFor="email" className="block text-sm font-semibold text-[#111C16] dark:text-[#F5F5F5] mb-1.5">
                    Email address
                  </label>
                  <input
                    id="email"
                    type="email"
                    autoComplete="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    placeholder="you@example.com"
                    className="w-full px-3.5 py-2.5 rounded-xl border border-[#E2E8F0] dark:border-[#2D2D2D] bg-white dark:bg-[#1C1C1C] text-[#111C16] dark:text-[#F5F5F5] text-sm placeholder:text-slate-400 dark:placeholder:text-[#A3A3A3] focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
                  />
                </div>
                <div>
                  <label htmlFor="password" className="block text-sm font-semibold text-[#111C16] dark:text-[#F5F5F5] mb-1.5">
                    Password
                  </label>
                  <input
                    id="password"
                    type="password"
                    autoComplete={mode === 'signup' ? 'new-password' : 'current-password'}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    minLength={6}
                    placeholder="••••••••"
                    className="w-full px-3.5 py-2.5 rounded-xl border border-[#E2E8F0] dark:border-[#2D2D2D] bg-white dark:bg-[#1C1C1C] text-[#111C16] dark:text-[#F5F5F5] text-sm placeholder:text-slate-400 dark:placeholder:text-[#A3A3A3] focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
                  />
                </div>
                <button
                  type="submit"
                  disabled={busy}
                  className="w-full py-2.5 px-4 rounded-xl bg-[#1B4D3E] hover:bg-[#153f33] text-white font-bold text-sm shadow transition-colors focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] disabled:opacity-60 flex items-center justify-center gap-2"
                >
                  {busy && <Loader2 className="w-4 h-4 animate-spin" aria-hidden="true" />}
                  {mode === 'signup' ? 'Create Account' : 'Sign In'}
                </button>
                {emailMode && (
                  <button
                    type="button"
                    onClick={() => { setEmailMode(false); clearError(); setLocalErr(''); }}
                    className="w-full py-2 text-sm text-slate-500 dark:text-[#A3A3A3] hover:text-[#111C16] dark:hover:text-[#F5F5F5] transition-colors focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] rounded-lg"
                  >
                    ← Back to sign-in options
                  </button>
                )}
              </form>
            )}

            {/* Demo mode (always shown at bottom) */}
            <div className="border-t border-[#E2E8F0] dark:border-[#2D2D2D] pt-5">
              {!isSupabaseConfigured && (
                <p className="text-xs text-amber-600 dark:text-amber-400 bg-amber-50 dark:bg-amber-950/30 border border-amber-200 dark:border-amber-800 rounded-xl p-3 mb-4 text-center">
                  <strong>Supabase not configured.</strong> OAuth is unavailable. Use Demo mode to explore the dashboard.
                </p>
              )}
              <button
                onClick={signInAsDemo}
                className="w-full py-2.5 px-4 rounded-xl bg-emerald-50 dark:bg-emerald-950/30 border border-emerald-200 dark:border-emerald-800 text-[#1B4D3E] dark:text-emerald-400 font-bold text-sm hover:bg-emerald-100 dark:hover:bg-emerald-950/50 transition-colors focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
              >
                🚀 Explore with Demo Data
              </button>
              <p className="text-xs text-center text-slate-400 dark:text-[#A3A3A3] mt-2">
                Pre-loaded with sample invoices, customers, and payments.
              </p>
            </div>

            {/* Toggle sign-in / sign-up */}
            {isSupabaseConfigured && (
              <p className="text-center text-sm text-slate-500 dark:text-[#A3A3A3] mt-5">
                {mode === 'signin' ? "Don't have an account? " : 'Already have an account? '}
                <button
                  onClick={() => { setMode(mode === 'signin' ? 'signup' : 'signin'); clearError(); setLocalErr(''); }}
                  className="font-semibold text-[#1B4D3E] dark:text-emerald-400 underline underline-offset-2 hover:opacity-80 focus:outline-none focus:ring-1 focus:ring-[#1B4D3E] rounded"
                >
                  {mode === 'signin' ? 'Sign up' : 'Sign in'}
                </button>
              </p>
            )}
          </div>

          {/* Footer */}
          <p className="text-center text-xs text-slate-400 dark:text-[#A3A3A3] mt-6">
            Baaki — Smart Invoice Collection & Cash Flow Intelligence
          </p>
        </div>
      </main>
    </div>
  );
}
