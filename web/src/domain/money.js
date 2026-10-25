/**
 * money.js — Exact integer paise monetary calculations reproducing Android's Money.kt
 * All financial storage and math uses paise (1 INR = 100 paise).
 */

export const ZERO = 0;

/**
 * Format Indian number grouping (1,23,456)
 */
export function formatIndianNumber(rupees) {
  if (rupees === 0) return '0';
  const s = Math.floor(Math.abs(rupees)).toString();
  if (s.length <= 3) return s;
  const last3 = s.slice(-3);
  const rest = s.slice(0, -3);
  const grouped = rest.replace(/\B(?=(\d{2})+(?!\d))/g, ',');
  return `${grouped},${last3}`;
}

/**
 * Format paise as full INR string (e.g. ₹1,50,000 or ₹1,50,000.50)
 */
export function formatINR(paise) {
  if (paise == null || isNaN(paise)) return '₹0';
  const isNegative = paise < 0;
  const absPaise = Math.abs(Math.round(paise));
  const rupees = Math.floor(absPaise / 100);
  const paiseRemainder = absPaise % 100;

  const sign = isNegative ? '-' : '';
  const formattedRupees = formatIndianNumber(rupees);

  if (paiseRemainder === 0) {
    return `${sign}₹${formattedRupees}`;
  } else {
    return `${sign}₹${formattedRupees}.${paiseRemainder.toString().padStart(2, '0')}`;
  }
}

/**
 * Compact Indian currency formatting reproducing Money.kt formatCompact()
 * e.g. ₹1.2L, ₹2.5Cr, ₹45K
 */
export function formatCompact(paise) {
  if (paise == null || isNaN(paise)) return '₹0';
  const isNegative = paise < 0;
  const absPaise = Math.abs(Math.round(paise));
  const rupees = absPaise / 100;
  const sign = isNegative ? '-' : '';

  if (rupees >= 10000000) {
    return `${sign}₹${(rupees / 10000000).toFixed(1)}Cr`;
  } else if (rupees >= 100000) {
    return `${sign}₹${(rupees / 100000).toFixed(1)}L`;
  } else if (rupees >= 1000) {
    return `${sign}₹${(rupees / 1000).toFixed(0)}K`;
  } else {
    return formatINR(paise);
  }
}

/**
 * Convert user-entered rupees to integer paise
 */
export function rupeesToPaise(rupees) {
  if (rupees == null || isNaN(rupees)) return 0;
  return Math.round(Number(rupees) * 100);
}

/**
 * Convert integer paise to decimal rupees for input fields
 */
export function paiseToRupees(paise) {
  if (paise == null || isNaN(paise)) return 0;
  return Number(paise) / 100;
}
