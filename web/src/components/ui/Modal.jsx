import React, { useEffect, useRef } from 'react';
import { X } from 'lucide-react';

export default function Modal({ isOpen, onClose, title, children, maxWidth = 'max-w-lg' }) {
  const modalRef = useRef(null);

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
      // Simple focus trap for WCAG 2.4.11 / 1.4.13
      if (e.key === 'Tab' && isOpen && modalRef.current) {
        const focusableElements = modalRef.current.querySelectorAll(
          'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        );
        if (focusableElements.length > 0) {
          const firstElement = focusableElements[0];
          const lastElement = focusableElements[focusableElements.length - 1];

          if (e.shiftKey && document.activeElement === firstElement) {
            e.preventDefault();
            lastElement.focus();
          } else if (!e.shiftKey && document.activeElement === lastElement) {
            e.preventDefault();
            firstElement.focus();
          }
        }
      }
    };

    if (isOpen) {
      document.body.style.overflow = 'hidden';
      window.addEventListener('keydown', handleKeyDown);
    }
    return () => {
      document.body.style.overflow = 'unset';
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div
      className="fixed inset-0 z-50 overflow-y-auto bg-black/50 backdrop-blur-sm flex items-center justify-center p-4"
      role="presentation"
    >
      <div
        ref={modalRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-title"
        className={`relative w-full ${maxWidth} bg-white dark:bg-[#141414] rounded-2xl shadow-2xl border border-slate-200 dark:border-[#2D2D2D] overflow-hidden animate-in fade-in zoom-in-95 duration-150`}
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header - H2 maintains proper heading order from H1 */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 dark:border-[#2D2D2D] bg-[#F8FAF9] dark:bg-[#0A0A0A]">
          <h2 id="modal-title" className="text-lg font-bold text-[#111C16] dark:text-[#F5F5F5]">
            {title}
          </h2>
          <button
            onClick={onClose}
            aria-label="Close modal"
            className="p-1 rounded-lg text-slate-400 dark:text-[#A3A3A3] hover:text-slate-600 dark:hover:text-[#F5F5F5] hover:bg-slate-200/50 dark:hover:bg-[#1C1C1C] transition-colors focus:outline-none focus:ring-2 focus:ring-[#1B4D3E]"
          >
            <X className="w-5 h-5" aria-hidden="true" />
          </button>
        </div>

        {/* Body */}
        <div className="p-6 max-h-[calc(100vh-160px)] overflow-y-auto">
          {children}
        </div>
      </div>
    </div>
  );
}
