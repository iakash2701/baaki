/**
 * financialYear.js — Reproduces Android's FinancialYearUtils.kt
 * Indian Financial Year runs from 1 April to 31 March.
 * e.g. Oct 2025 -> 2025-26
 */

export function getFinancialYear(timestamp = Date.now()) {
  const d = new Date(timestamp);
  const year = d.getFullYear();
  const month = d.getMonth(); // 0-indexed: 0=Jan, 3=Apr

  if (month < 3) {
    // Jan, Feb, Mar belong to previous FY
    const startYear = year - 1;
    const endYearSuffix = (year % 100).toString().padStart(2, '0');
    return `${startYear}-${endYearSuffix}`;
  } else {
    // Apr to Dec belong to current year FY
    const startYear = year;
    const endYear = year + 1;
    const endYearSuffix = (endYear % 100).toString().padStart(2, '0');
    return `${startYear}-${endYearSuffix}`;
  }
}
