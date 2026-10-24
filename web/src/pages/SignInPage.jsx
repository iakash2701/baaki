import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { Sun, Moon, Monitor, AlertCircle, Loader2, Mail, ArrowRight } from 'lucide-react';

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

export default function SignInPage() {
  const navigate = useNavigate();
  const { theme, setTheme } = useTheme();
  const {
    isAuthenticated,
    loading,
    error,
    isSupabaseConfigured,
    signInWithGoogle,
    signInWithEmail,
    signUpWithEmail,
    signInAsDemo,
    clearError,
  } = useAuth();

  const [mode, setMode] = useState('signin'); // 'signin' | 'signup'
  const [emailMode, setEmailMode] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [busy, setBusy] = useState(false);
  const [localErr, setLocalErr] = useState('');

  // If already authenticated, redirect to dashboard
  useEffect(() => {
    if (!loading && isAuthenticated) navigate('/', { replace: true });
  }, [isAuthenticated, loading, navigate]);

  useEffect(() => {
    document.title = 'Sign In — Baaki';
  }, []);

  async function handleOAuth(fn) {
    clearError();
    setLocalErr('');
    setBusy(true);
    try {
      await fn();
    } catch {
      /* handled in context */
    } finally {
      setBusy(false);
    }
  }

  async function handleEmailSubmit(e) {
    e.preventDefault();
    clearError();
    setLocalErr('');
    if (!email || !password) {
      setLocalErr('Please enter both your email and password.');
      return;
    }
    if (password.length < 6) {
      setLocalErr('Password must be at least 6 characters.');
      return;
    }
    setBusy(true);
    try {
      if (mode === 'signup') await signUpWithEmail(email, password);
      else await signInWithEmail(email, password);
    } finally {
      setBusy(false);
    }
  }

  const displayError = localErr || error;

  const themeIcons = { light: Sun, dark: Moon, system: Monitor };
  const themeLabels = { light: 'Light', dark: 'Dark', system: 'System' };
  const themeOrder = ['light', 'dark', 'system'];

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-[#F8FAF9] dark:bg-[#0A0A0A]">
        <Loader2 className="w-8 h-8 animate-spin text-[#1B4D3E]" aria-label="Loading..." />
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col bg-[#F8FAF9] dark:bg-[#0A0A0A] transition-colors duration-200">
      {/* Top Header */}
      <header className="flex items-center justify-between px-6 py-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-2xl bg-[#1B4D3E] text-white flex items-center justify-center font-extrabold text-xl shadow-md" aria-hidden="true">
            B
          </div>
          <div>
            <div className="flex items-center gap-1.5 font-extrabold text-lg text-[#111C16] dark:text-[#F5F5F5] tracking-tight">
              <span>Baaki</span>
              <span className="text-xs px-1.5 py-0.5 rounded bg-[#A3E4D7] text-[#0A2920] font-semibold">PRO</span>
            </div>
            <p className="text-xs text-slate-500 dark:text-[#A3A3A3] font-medium">Smart Invoice & Cash Flow</p>
          </div>
        </div>

        {/* Theme Selector */}
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

      {/* Main Container */}
      <main className="flex-1 flex items-center justify-center px-4 py-8" id="main-content">
        <div className="w-full max-w-md">
          <div className="bg-white dark:bg-[#141414] rounded-3xl shadow-xl border border-[#E2E8F0] dark:border-[#2D2D2D] p-8 md:p-9 transition-colors">
            
            {/* Header Titles */}
            <div className="text-center mb-8">
              <h1 className="text-2xl font-black text-[#111C16] dark:text-[#F5F5F5] tracking-tight mb-2">
                {mode === 'signup' ? 'Create your account' : 'Welcome back'}
              </h1>
              <p className="text-sm text-slate-500 dark:text-[#A3A3A3]">
                {mode === 'signup'
                  ? 'Sign up to start tracking payments & invoices'
                  : 'Access your private financial workspace'}
              </p>
            </div>

            {/* Error Alert */}
            {displayError && (
              <div
                role="alert"
                className="flex items-start gap-2.5 mb-6 p-3.5 rounded-2xl bg-red-50 dark:bg-red-950/30 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-400 text-sm animate-in fade-in"
              >
                <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5" aria-hidden="true" />
                <span>{displayError}</span>
              </div>
            )}

            {/* Default Choice: Google or Email */}
            {isSupabaseConfigured && !emailMode && (
              <div className="space-y-3.5 mb-6">
                {/* 1. Continue with Google */}
                <button
                  type="button"
                  onClick={() => handleOAuth(signInWithGoogle)}
                  disabled={busy}
                  className="w-full h-12 flex items-center justify-center gap-3 px-4 rounded-2xl border border-[#E2E8F0] dark:border-[#2D2D2D] bg-white dark:bg-[#1C1C1C] text-[#111C16] dark:text-[#F5F5F5] font-semibold text-sm hover:bg-slate-50 dark:hover:bg-[#252525] transition-all focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] shadow-xs disabled:opacity-60 active:scale-[0.99]"
                >
                  {busy ? <Loader2 className="w-5 h-5 animate-spin text-[#1B4D3E]" /> : <GoogleIcon className="w-5 h-5" />}
                  <span>Continue with Google</span>
                </button>

                {/* Divider */}
                <div className="relative my-4">
                  <div className="absolute inset-0 flex items-center" aria-hidden="true">
                    <div className="w-full border-t border-[#E2E8F0] dark:border-[#2D2D2D]" />
                  </div>
                  <div className="relative flex justify-center text-xs">
                    <span className="px-3 bg-white dark:bg-[#141414] text-slate-400 dark:text-[#737373] font-medium uppercase tracking-wider">
                      or
                    </span>
                  </div>
                </div>

                {/* 2. Continue with Email */}
                <button
                  type="button"
                  onClick={() => setEmailMode(true)}
                  className="w-full h-12 flex items-center justify-center gap-2.5 px-4 rounded-2xl border border-[#E2E8F0] dark:border-[#2D2D2D] bg-[#F8FAF9] dark:bg-[#181818] text-[#111C16] dark:text-[#F5F5F5] font-semibold text-sm hover:bg-slate-100 dark:hover:bg-[#222222] transition-all focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] shadow-xs active:scale-[0.99]"
                >
                  <Mail className="w-4 h-4 text-slate-500 dark:text-[#A3A3A3]" />
                  <span>Continue with Email</span>
                </button>
              </div>
            )}

            {/* Email / Password Form View */}
            {(emailMode || !isSupabaseConfigured) && isSupabaseConfigured && (
              <form onSubmit={handleEmailSubmit} noValidate className="space-y-4 mb-6">
                <div>
                  <label htmlFor="email" className="block text-xs font-bold text-slate-700 dark:text-[#D4D4D4] uppercase tracking-wider mb-2">
                    Email Address
                  </label>
                  <input
                    id="email"
                    type="email"
                    autoComplete="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    placeholder="name@company.com"
                    className="w-full h-11 px-4 rounded-xl border border-[#E2E8F0] dark:border-[#2D2D2D] bg-white dark:bg-[#1C1C1C] text-[#111C16] dark:text-[#F5F5F5] text-sm placeholder:text-slate-400 dark:placeholder:text-[#737373] focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] transition-colors"
                  />
                </div>

                <div>
                  <label htmlFor="password" className="block text-xs font-bold text-slate-700 dark:text-[#D4D4D4] uppercase tracking-wider mb-2">
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
                    className="w-full h-11 px-4 rounded-xl border border-[#E2E8F0] dark:border-[#2D2D2D] bg-white dark:bg-[#1C1C1C] text-[#111C16] dark:text-[#F5F5F5] text-sm placeholder:text-slate-400 dark:placeholder:text-[#737373] focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] transition-colors"
                  />
                </div>

                <button
                  type="submit"
                  disabled={busy}
                  className="w-full h-11 px-4 rounded-xl bg-[#1B4D3E] hover:bg-[#153f33] text-white font-bold text-sm shadow-md transition-all focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] disabled:opacity-60 flex items-center justify-center gap-2 active:scale-[0.99]"
                >
                  {busy ? <Loader2 className="w-4 h-4 animate-spin" aria-hidden="true" /> : <ArrowRight className="w-4 h-4" />}
                  <span>{mode === 'signup' ? 'Create Account' : 'Sign In with Email'}</span>
                </button>

                {emailMode && (
                  <button
                    type="button"
                    onClick={() => {
                      setEmailMode(false);
                      clearError();
                      setLocalErr('');
                    }}
                    className="w-full py-2 text-xs font-semibold text-slate-500 dark:text-[#A3A3A3] hover:text-[#111C16] dark:hover:text-[#F5F5F5] transition-colors focus:outline-none focus:ring-1 focus:ring-[#1B4D3E] rounded-lg"
                  >
                    ← Back to Google sign in
                  </button>
                )}
              </form>
            )}

            {/* Instant Demo Sandbox Option */}
            <div className="border-t border-[#E2E8F0] dark:border-[#2D2D2D] pt-6">
              <button
                type="button"
                onClick={signInAsDemo}
                className="w-full h-11 px-4 rounded-2xl bg-emerald-50 dark:bg-emerald-950/30 border border-emerald-200 dark:border-emerald-800 text-[#1B4D3E] dark:text-emerald-400 font-bold text-sm hover:bg-emerald-100 dark:hover:bg-emerald-950/50 transition-colors focus:outline-none focus:ring-2 focus:ring-[#1B4D3E] flex items-center justify-center gap-2"
              >
                <span>🚀 Explore with Demo Data</span>
              </button>
              <p className="text-[11px] text-center text-slate-400 dark:text-[#737373] mt-2">
                Instant guest preview pre-populated with Indian GST invoices
              </p>
            </div>

            {/* Sign in / Sign up Mode Toggle */}
            {isSupabaseConfigured && (
              <p className="text-center text-xs text-slate-500 dark:text-[#A3A3A3] mt-6">
                {mode === 'signin' ? "Don't have an account? " : 'Already have an account? '}
                <button
                  type="button"
                  onClick={() => {
                    setMode(mode === 'signin' ? 'signup' : 'signin');
                    clearError();
                    setLocalErr('');
                  }}
                  className="font-bold text-[#1B4D3E] dark:text-emerald-400 underline underline-offset-2 hover:opacity-80 focus:outline-none focus:ring-1 focus:ring-[#1B4D3E] rounded"
                >
                  {mode === 'signin' ? 'Sign up' : 'Sign in'}
                </button>
              </p>
            )}
          </div>

          <p className="text-center text-xs text-slate-400 dark:text-[#737373] mt-6">
            Baaki — Smart Invoice Collection & Cash Flow Intelligence
          </p>
        </div>
      </main>
    </div>
  );
}
