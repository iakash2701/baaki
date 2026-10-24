-- ==============================================================================
-- BAAKI MULTI-USER SUPABASE POSTGRESQL SCHEMA WITH ROW LEVEL SECURITY (RLS)
-- ==============================================================================

-- 1. Enable UUID Extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 2. CUSTOMERS TABLE
CREATE TABLE IF NOT EXISTS public.customers (
    id TEXT PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    phone TEXT NOT NULL,
    email TEXT,
    gstin TEXT,
    address TEXT,
    opening_balance BIGINT DEFAULT 0,
    credit_limit BIGINT DEFAULT 10000000,
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint
);

ALTER TABLE public.customers ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can only view their own customers"
    ON public.customers FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can only insert their own customers"
    ON public.customers FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can only update their own customers"
    ON public.customers FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can only delete their own customers"
    ON public.customers FOR DELETE
    USING (auth.uid() = user_id);

CREATE INDEX IF NOT EXISTS idx_customers_user_id ON public.customers(user_id);

-- 3. INVOICES TABLE
CREATE TABLE IF NOT EXISTS public.invoices (
    id TEXT PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    invoice_number TEXT NOT NULL,
    customer_id TEXT NOT NULL REFERENCES public.customers(id) ON DELETE RESTRICT,
    issue_date BIGINT NOT NULL,
    due_date BIGINT NOT NULL,
    sub_total BIGINT NOT NULL DEFAULT 0,
    tax_total BIGINT NOT NULL DEFAULT 0,
    discount_total BIGINT NOT NULL DEFAULT 0,
    total_amount BIGINT NOT NULL DEFAULT 0,
    status TEXT NOT NULL DEFAULT 'SENT', -- 'SENT', 'PAID', 'PARTIALLY_PAID', 'OVERDUE'
    notes TEXT,
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint
);

ALTER TABLE public.invoices ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can only view their own invoices"
    ON public.invoices FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can only insert their own invoices"
    ON public.invoices FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can only update their own invoices"
    ON public.invoices FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can only delete their own invoices"
    ON public.invoices FOR DELETE
    USING (auth.uid() = user_id);

CREATE INDEX IF NOT EXISTS idx_invoices_user_id ON public.invoices(user_id);
CREATE INDEX IF NOT EXISTS idx_invoices_customer_id ON public.invoices(customer_id);

-- 4. INVOICE LINE ITEMS TABLE
CREATE TABLE IF NOT EXISTS public.invoice_line_items (
    id TEXT PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    invoice_id TEXT NOT NULL REFERENCES public.invoices(id) ON DELETE CASCADE,
    description TEXT NOT NULL,
    hsn_sac TEXT,
    quantity NUMERIC NOT NULL DEFAULT 1,
    unit_price BIGINT NOT NULL DEFAULT 0,
    tax_percent NUMERIC NOT NULL DEFAULT 0,
    amount BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE public.invoice_line_items ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can only view their own invoice line items"
    ON public.invoice_line_items FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can only insert their own invoice line items"
    ON public.invoice_line_items FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can only update their own invoice line items"
    ON public.invoice_line_items FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can only delete their own invoice line items"
    ON public.invoice_line_items FOR DELETE
    USING (auth.uid() = user_id);

CREATE INDEX IF NOT EXISTS idx_line_items_user_id ON public.invoice_line_items(user_id);
CREATE INDEX IF NOT EXISTS idx_line_items_invoice_id ON public.invoice_line_items(invoice_id);

-- 5. PAYMENTS TABLE
CREATE TABLE IF NOT EXISTS public.payments (
    id TEXT PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    invoice_id TEXT REFERENCES public.invoices(id) ON DELETE SET NULL,
    customer_id TEXT NOT NULL REFERENCES public.customers(id) ON DELETE RESTRICT,
    amount BIGINT NOT NULL,
    payment_date BIGINT NOT NULL,
    payment_method TEXT NOT NULL DEFAULT 'UPI', -- 'UPI', 'BANK_TRANSFER', 'CASH', 'CHEQUE'
    reference_number TEXT,
    notes TEXT,
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint
);

ALTER TABLE public.payments ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can only view their own payments"
    ON public.payments FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can only insert their own payments"
    ON public.payments FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can only update their own payments"
    ON public.payments FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can only delete their own payments"
    ON public.payments FOR DELETE
    USING (auth.uid() = user_id);

CREATE INDEX IF NOT EXISTS idx_payments_user_id ON public.payments(user_id);
CREATE INDEX IF NOT EXISTS idx_payments_invoice_id ON public.payments(invoice_id);

-- 6. EXPENSES TABLE
CREATE TABLE IF NOT EXISTS public.expenses (
    id TEXT PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    category TEXT NOT NULL DEFAULT 'OTHER', -- 'RENT', 'SUPPLIES', 'SALARIES', 'UTILITIES', 'OTHER'
    amount BIGINT NOT NULL,
    expense_date BIGINT NOT NULL,
    notes TEXT,
    created_at BIGINT NOT NULL DEFAULT (extract(epoch from now()) * 1000)::bigint
);

ALTER TABLE public.expenses ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can only view their own expenses"
    ON public.expenses FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can only insert their own expenses"
    ON public.expenses FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can only update their own expenses"
    ON public.expenses FOR UPDATE
    USING (auth.uid() = user_id)
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can only delete their own expenses"
    ON public.expenses FOR DELETE
    USING (auth.uid() = user_id);

CREATE INDEX IF NOT EXISTS idx_expenses_user_id ON public.expenses(user_id);

-- 7. ACTIVITIES / AUDIT LOG TABLE
CREATE TABLE IF NOT EXISTS public.activities (
    id TEXT PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    description TEXT,
    event_type TEXT NOT NULL,
    timestamp BIGINT NOT NULL,
    related_id TEXT
);

ALTER TABLE public.activities ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can only view their own activities"
    ON public.activities FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can only insert their own activities"
    ON public.activities FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE INDEX IF NOT EXISTS idx_activities_user_id ON public.activities(user_id);
