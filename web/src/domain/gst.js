/**
 * gst.js — GST Calculation helpers
 * Calculates intra-state (CGST + SGST) or inter-state (IGST) and line item totals in integer paise.
 */

export function calculateLineItem({ quantity = 1, unitPricePaise = 0, taxPercent = 18 }) {
  const qty = Number(quantity) || 0;
  const price = Math.round(Number(unitPricePaise)) || 0;
  const tax = Number(taxPercent) || 0;

  const lineAmount = Math.round(qty * price);
  const lineTax = Math.round(lineAmount * (tax / 100));
  const totalLineAmount = lineAmount + lineTax;

  return {
    lineAmount,
    lineTax,
    totalLineAmount
  };
}

export function calculateInvoiceTotals(items = []) {
  let subTotal = 0;
  let taxTotal = 0;

  items.forEach(item => {
    const { lineAmount, lineTax } = calculateLineItem({
      quantity: item.quantity,
      unitPricePaise: item.unitPrice,
      taxPercent: item.taxPercent
    });
    subTotal += lineAmount;
    taxTotal += lineTax;
  });

  const totalAmount = subTotal + taxTotal;

  return {
    subTotal,
    taxTotal,
    totalAmount
  };
}
