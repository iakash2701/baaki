import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { supabase } from '../lib/supabaseClient';
import { Loader2 } from 'lucide-react';

/**
 * Handles the OAuth redirect callback from Supabase.
 * Supabase automatically parses the URL hash/query on the client
 * via onAuthStateChange, so we just need to wait and redirect.
 */
export default function AuthCallbackPage() {
  const navigate = useNavigate();

  useEffect(() => {
    document.title = 'Signing you in… — Baaki';

    if (!supabase) {
      navigate('/', { replace: true });
      return;
    }

    // Supabase processes the callback automatically via detectSessionInUrl.
    // We just need to listen for the session to be established.
    const { data: { subscription } } = supabase.auth.onAuthStateChange((event, session) => {
      if (event === 'SIGNED_IN' && session) {
        navigate('/', { replace: true });
      } else if (event === 'SIGNED_OUT') {
        navigate('/signin', { replace: true });
      }
    });

    // Fallback: redirect after 5 s in case the event already fired
    const timeout = setTimeout(() => navigate('/', { replace: true }), 5000);

    return () => {
      subscription.unsubscribe();
      clearTimeout(timeout);
    };
  }, [navigate]);

  return (
    <div className="min-h-screen flex flex-col items-center justify-center gap-4 bg-[#F8FAF9] dark:bg-[#0A0A0A] text-[#111C16] dark:text-[#F5F5F5]">
      <div className="w-12 h-12 rounded-2xl bg-[#1B4D3E] text-white flex items-center justify-center font-bold text-2xl shadow-lg">
        B
      </div>
      <Loader2 className="w-6 h-6 animate-spin text-[#1B4D3E]" aria-label="Completing sign-in…" />
      <p className="text-sm text-slate-500 dark:text-[#A3A3A3] font-medium">Completing sign-in…</p>
    </div>
  );
}
