import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { supabase, isSupabaseConfigured } from '../lib/supabaseClient';

const AuthContext = createContext(null);

// ─── Demo / local-only user (when Supabase is not configured) ───────────────
const DEMO_USER = {
  id: 'demo-user',
  email: 'demo@baaki.app',
  user_metadata: { full_name: 'Demo User', avatar_url: null },
  isDemoUser: true,
};

export function AuthProvider({ children }) {
  const [user, setUser]         = useState(null);   // null = not yet known
  const [loading, setLoading]   = useState(true);
  const [error, setError]       = useState(null);

  // ── Bootstrap ──────────────────────────────────────────────────────────────
  useEffect(() => {
    if (!isSupabaseConfigured) {
      // No Supabase → check localStorage for a "remembered" demo session
      const remembered = localStorage.getItem('baaki_demo_session');
      setUser(remembered === 'true' ? DEMO_USER : null);
      setLoading(false);
      return;
    }

    // Supabase is configured: restore existing session
    supabase.auth.getSession().then(({ data: { session } }) => {
      setUser(session?.user ?? null);
      setLoading(false);
    });

    // Listen for future auth events
    const { data: { subscription } } = supabase.auth.onAuthStateChange((_event, session) => {
      setUser(session?.user ?? null);
      setLoading(false);
    });

    return () => subscription.unsubscribe();
  }, []);

  // ── OAuth sign-in ──────────────────────────────────────────────────────────
  const signInWithGoogle = useCallback(async () => {
    setError(null);
    if (!isSupabaseConfigured) {
      setError('Supabase is not configured. Using demo mode.');
      return;
    }
    const { error } = await supabase.auth.signInWithOAuth({
      provider: 'google',
      options: { redirectTo: `${window.location.origin}/auth/callback` },
    });
    if (error) setError(error.message);
  }, []);

  const signInWithApple = useCallback(async () => {
    setError(null);
    if (!isSupabaseConfigured) {
      setError('Supabase is not configured. Using demo mode.');
      return;
    }
    const { error } = await supabase.auth.signInWithOAuth({
      provider: 'apple',
      options: { redirectTo: `${window.location.origin}/auth/callback` },
    });
    if (error) setError(error.message);
  }, []);

  const signInWithMicrosoft = useCallback(async () => {
    setError(null);
    if (!isSupabaseConfigured) {
      setError('Supabase is not configured. Using demo mode.');
      return;
    }
    const { error } = await supabase.auth.signInWithOAuth({
      provider: 'azure',
      options: {
        scopes: 'email profile',
        redirectTo: `${window.location.origin}/auth/callback`,
      },
    });
    if (error) setError(error.message);
  }, []);

  // ── Email / password (optional fallback) ───────────────────────────────────
  const signInWithEmail = useCallback(async (email, password) => {
    setError(null);
    if (!isSupabaseConfigured) {
      setError('Supabase is not configured. Using demo mode.');
      return;
    }
    const { error } = await supabase.auth.signInWithPassword({ email, password });
    if (error) setError(error.message);
  }, []);

  const signUpWithEmail = useCallback(async (email, password) => {
    setError(null);
    if (!isSupabaseConfigured) {
      setError('Supabase is not configured. Using demo mode.');
      return;
    }
    const { error } = await supabase.auth.signUp({ email, password });
    if (error) setError(error.message);
  }, []);

  // ── Demo mode (no Supabase) ────────────────────────────────────────────────
  const signInAsDemo = useCallback(() => {
    localStorage.setItem('baaki_demo_session', 'true');
    setUser(DEMO_USER);
    setError(null);
  }, []);

  // ── Sign out ───────────────────────────────────────────────────────────────
  const signOut = useCallback(async () => {
    setError(null);
    if (!isSupabaseConfigured) {
      localStorage.removeItem('baaki_demo_session');
      setUser(null);
      return;
    }
    await supabase.auth.signOut();
    setUser(null);
  }, []);

  // ── Helpers ────────────────────────────────────────────────────────────────
  const displayName = user?.user_metadata?.full_name
    || user?.user_metadata?.name
    || user?.email
    || 'User';

  const avatarUrl = user?.user_metadata?.avatar_url || null;

  const initials = displayName
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((w) => w[0].toUpperCase())
    .join('');

  const value = {
    user,
    loading,
    error,
    isAuthenticated: !!user,
    isSupabaseConfigured,
    displayName,
    avatarUrl,
    initials,
    signInWithGoogle,
    signInWithMicrosoft,
    signInWithApple,
    signInWithEmail,
    signUpWithEmail,
    signInAsDemo,
    signOut,
    clearError: () => setError(null),
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider');
  return ctx;
}
