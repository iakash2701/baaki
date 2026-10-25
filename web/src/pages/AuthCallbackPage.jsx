import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { supabase } from '../lib/supabaseClient';
import { Loader2 } from 'lucide-react';

export default function AuthCallbackPage() {
  const navigate = useNavigate();

  useEffect(() => {
    document.title = 'Signing you in… — Baaki';

    if (!supabase) {
      navigate('/', { replace: true });
      return;
    }

    let isHandled = false;

    // Check if session is already retrieved from the hash / URL
    supabase.auth.getSession().then(({ data: { session }, error }) => {
      if (!isHandled && session) {
        isHandled = true;
        navigate('/', { replace: true });
      }
    });

    // Listen for the auth state transition
    const { data: { subscription } } = supabase.auth.onAuthStateChange((event, session) => {
      if (!isHandled && (event === 'SIGNED_IN' || session)) {
        isHandled = true;
        navigate('/', { replace: true });
      } else if (!isHandled && event === 'SIGNED_OUT') {
        isHandled = true;
        navigate('/signin', { replace: true });
      }
    });

    // Safety timeout: if after 3s we have a session, go to root, otherwise signin
    const timeout = setTimeout(async () => {
      if (!isHandled) {
        isHandled = true;
        const { data: { session } } = await supabase.auth.getSession();
        if (session) {
          navigate('/', { replace: true });
        } else {
          navigate('/signin', { replace: true });
        }
      }
    }, 3500);

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
