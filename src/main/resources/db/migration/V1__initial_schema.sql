-- ============================================
-- V1: Initial database schema
-- ============================================

-- ============================================
-- ROLES
-- ============================================
CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       CONSTRAINT chk_role_name CHECK (name IN ('ROLE_EMPLOYEE', 'ROLE_CLIENT'))
);

-- ============================================
-- USERS (base table for inheritance)
-- ============================================
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL
);

-- ============================================
-- CLIENTS (extends users)
-- ============================================
CREATE TABLE clients (
                         user_id BIGINT PRIMARY KEY,
                         card VARCHAR(255) NOT NULL,
                         CONSTRAINT fk_clients_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- EMPLOYEES (extends users)
-- ============================================
CREATE TABLE employees (
                           user_id BIGINT PRIMARY KEY,
                           department VARCHAR(255) NOT NULL,
                           CONSTRAINT fk_employees_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- USER_ROLES (many-to-many)
-- ============================================
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ============================================
-- MANUFACTURERS
-- ============================================
CREATE TABLE manufacturers (
                               id BIGSERIAL PRIMARY KEY,
                               name VARCHAR(255) NOT NULL UNIQUE
);

-- ============================================
-- APPLIANCES
-- ============================================
CREATE TABLE appliances (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            category VARCHAR(50) NOT NULL CHECK (category IN ('BIG', 'SMALL')),
                            model VARCHAR(255) NOT NULL,
                            manufacturer_id BIGINT NOT NULL,
                            power_type VARCHAR(50) NOT NULL CHECK (power_type IN ('AC220', 'AC110', 'ACCUMULATOR')),
                            characteristic VARCHAR(255),
                            description VARCHAR(1000),
                            power INTEGER,
                            price NUMERIC(12, 2) NOT NULL,
                            CONSTRAINT fk_appliances_manufacturer FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(id)
);

-- ============================================
-- ORDERS
-- ============================================
CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        employee_id BIGINT NOT NULL,
                        client_id BIGINT NOT NULL,
                        approved BOOLEAN NOT NULL DEFAULT FALSE,
                        CONSTRAINT fk_orders_employee FOREIGN KEY (employee_id) REFERENCES employees(user_id),
                        CONSTRAINT fk_orders_client FOREIGN KEY (client_id) REFERENCES clients(user_id)
);

-- ============================================
-- ORDER_ROWS
-- ============================================
CREATE TABLE order_rows (
                            id BIGSERIAL PRIMARY KEY,
                            order_id BIGINT NOT NULL,
                            appliance_id BIGINT NOT NULL,
                            number BIGINT NOT NULL,
                            amount NUMERIC(12, 2) NOT NULL,
                            CONSTRAINT fk_order_rows_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                            CONSTRAINT fk_order_rows_appliance FOREIGN KEY (appliance_id) REFERENCES appliances(id)
);

-- ============================================
-- INDEXES
-- ============================================
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_name ON users(name);
CREATE INDEX idx_manufacturers_name ON manufacturers(name);
CREATE INDEX idx_appliances_name ON appliances(name);
CREATE INDEX idx_appliances_category ON appliances(category);
CREATE INDEX idx_appliances_manufacturer ON appliances(manufacturer_id);
CREATE INDEX idx_orders_client ON orders(client_id);
CREATE INDEX idx_orders_employee ON orders(employee_id);
CREATE INDEX idx_order_rows_order ON order_rows(order_id);