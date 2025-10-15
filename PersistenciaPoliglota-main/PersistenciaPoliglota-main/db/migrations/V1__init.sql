-- V1__init.sql
-- Inicial: estructuras relacionales para Usuarios, Roles, Procesos, Solicitudes, Historial, Facturas, Pagos, Cuentas y Mensajería

-- Tabla users (ya existe en JPA):
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    registered_at TIMESTAMPTZ,
    last_login_at TIMESTAMPTZ
);

-- Roles y asociación N-M
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Catalogo de procesos ofrecidos
CREATE TABLE IF NOT EXISTS processes (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    process_type TEXT,
    cost NUMERIC(12,2) DEFAULT 0,
    is_periodic BOOLEAN DEFAULT FALSE,
    schedule_cron TEXT,
    created_by INT REFERENCES users(id),
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Solicitudes de ejecución de procesos por parte de usuarios
CREATE TABLE IF NOT EXISTS process_requests (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    process_id INT NOT NULL REFERENCES processes(id),
    requested_at TIMESTAMPTZ DEFAULT now(),
    status VARCHAR(32) DEFAULT 'pending',
    params JSONB,
    result_location TEXT
);

CREATE INDEX IF NOT EXISTS idx_process_requests_user_status ON process_requests(user_id, status);

-- Historial de ejecuciones
CREATE TABLE IF NOT EXISTS execution_history (
    id SERIAL PRIMARY KEY,
    request_id INT NOT NULL REFERENCES process_requests(id) ON DELETE CASCADE,
    executed_at TIMESTAMPTZ DEFAULT now(),
    result_status VARCHAR(32),
    result_summary JSONB,
    logs TEXT
);

CREATE INDEX IF NOT EXISTS idx_execution_history_request_executed_at ON execution_history(request_id, executed_at);

-- Facturación y pagos
CREATE TABLE IF NOT EXISTS invoices (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id),
    issued_at TIMESTAMPTZ DEFAULT now(),
    due_date DATE,
    status VARCHAR(32) DEFAULT 'pending',
    total_amount NUMERIC(12,2) DEFAULT 0,
    lines JSONB
);

CREATE INDEX IF NOT EXISTS idx_invoices_user_status_issued ON invoices(user_id, status, issued_at);

CREATE TABLE IF NOT EXISTS payments (
    id SERIAL PRIMARY KEY,
    invoice_id INT NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    paid_at TIMESTAMPTZ DEFAULT now(),
    amount NUMERIC(12,2) NOT NULL,
    method VARCHAR(64),
    transaction_ref TEXT
);

-- Cuenta corriente y asientos
CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE REFERENCES users(id),
    balance NUMERIC(12,2) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS account_entries (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    amount NUMERIC(12,2) NOT NULL,
    entry_type VARCHAR(16) NOT NULL, -- credit/debit
    created_at TIMESTAMPTZ DEFAULT now(),
    description TEXT,
    related_invoice_id INT REFERENCES invoices(id)
);

CREATE INDEX IF NOT EXISTS idx_account_entries_account_created_at ON account_entries(account_id, created_at);

-- Mensajería: mensajes privados o de grupo
CREATE TABLE IF NOT EXISTS groups (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE IF NOT EXISTS group_members (
    group_id INT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    member_role VARCHAR(32),
    PRIMARY KEY (group_id, user_id)
);

CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY,
    sender_id INT NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    recipient_user_id INT REFERENCES users(id) ON DELETE SET NULL,
    group_id INT REFERENCES groups(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT now(),
    content TEXT,
    message_type VARCHAR(16), -- private/group
    metadata JSONB
);

CREATE INDEX IF NOT EXISTS idx_messages_sender_created_at ON messages(sender_id, created_at);

-- Recomendaciones:
-- 1) Añadir políticas de particionado (partitioning) para tablas grandes (invoices, execution_history) por rango de fecha si se espera crecimiento masivo.
-- 2) Habilitar pg_trgm/gist/gin indexes para búsquedas de texto si se implementan consultas complejas.
-- 3) Usar migraciones (Flyway/Liquibase) y respaldos periódicos.
