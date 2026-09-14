-- ============================================
-- ROLES
-- ============================================
INSERT INTO roles (id, name) VALUES (1, 'ROLE_CLIENT');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_EMPLOYEE');

ALTER SEQUENCE roles_id_seq RESTART WITH 3;

-- ============================================
-- USERS (пароль для всіх: Password123!)
-- BCrypt hash для Password123!: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ============================================

-- Клієнти
INSERT INTO users (id, name, email, password)
VALUES (1, 'John Client', 'john@client.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

INSERT INTO users (id, name, email, password)
VALUES (2, 'Mary Client', 'mary@client.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Працівники
INSERT INTO users (id, name, email, password)
VALUES (3, 'Jane Employee', 'jane@employee.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

INSERT INTO users (id, name, email, password)
VALUES (4, 'Bob Employee', 'bob@employee.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

ALTER SEQUENCE users_id_seq RESTART WITH 5;

-- ============================================
-- CLIENTS (extends users)
-- ============================================
INSERT INTO clients (user_id, card) VALUES (1, '1234-5678-9012-3456');
INSERT INTO clients (user_id, card) VALUES (2, '9876-5432-1098-7654');

-- ============================================
-- EMPLOYEES (extends users)
-- ============================================
INSERT INTO employees (user_id, department) VALUES (3, 'IT Department');
INSERT INTO employees (user_id, department) VALUES (4, 'HR Department');

-- ============================================
-- USER_ROLES
-- ============================================
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 2);

-- ============================================
-- MANUFACTURERS
-- ============================================
INSERT INTO manufacturers (id, name) VALUES (1, 'Samsung Electronics');
INSERT INTO manufacturers (id, name) VALUES (2, 'Apple Inc.');
INSERT INTO manufacturers (id, name) VALUES (3, 'LG Electronics');
INSERT INTO manufacturers (id, name) VALUES (4, 'Sony Corporation');
INSERT INTO manufacturers (id, name) VALUES (5, 'Bosch');
INSERT INTO manufacturers (id, name) VALUES (6, 'Philips');
INSERT INTO manufacturers (id, name) VALUES (7, 'Dyson');
INSERT INTO manufacturers (id, name) VALUES (8, 'Whirlpool');

ALTER SEQUENCE manufacturers_id_seq RESTART WITH 9;

-- ============================================
-- APPLIANCES
-- ============================================
INSERT INTO appliances (id, name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (1, 'Samsung Smart Fridge', 'BIG', 'RF28R7351SR', 1, 'AC220', 'SmartThings compatible, 28 cu. ft.', 'Large capacity French door refrigerator with AI features', 600, 1999.99);

INSERT INTO appliances (id, name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (2, 'LG InstaView Fridge', 'BIG', 'LFXS28968S', 3, 'AC220', 'InstaView door-in-door, SmartThinQ', 'French door refrigerator with water dispenser', 550, 2499.00);

INSERT INTO appliances (id, name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (3, 'Bosch Washing Machine', 'BIG', 'WAW285H2EU', 5, 'AC220', '9kg capacity, 1400 RPM', 'Energy efficient washing machine with EcoSilence Drive', 2000, 799.99);

INSERT INTO appliances (id, name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (4, 'Dyson V15 Detect', 'SMALL', 'SV22', 7, 'ACCUMULATOR', 'Laser dust detection, 230 AW suction', 'Cordless vacuum with laser technology and LCD screen', 800, 699.99);

INSERT INTO appliances (id, name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (5, 'Sony Bravia OLED TV', 'BIG', 'XR-65A80L', 4, 'AC220', '65 inch, 4K OLED, HDR', 'Smart TV with Google TV and Acoustic Surface Audio', 450, 2499.99);

INSERT INTO appliances (id, name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (6, 'Philips Air Fryer', 'SMALL', 'HD9860/90', 6, 'AC220', '7.2L capacity, fat removal technology', 'Digital air fryer with Smart Sensing', 1700, 299.95);

INSERT INTO appliances (id, name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (7, 'Samsung Microwave', 'SMALL', 'ME9114S1', 1, 'AC220', '23L capacity, 800W', 'Compact microwave with ceramic enamel interior', 800, 149.99);

INSERT INTO appliances (id, name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (8, 'Philips Blender', 'SMALL', 'HR3655/00', 6, 'AC220', '1400W, 6 blades', 'High-speed blender with ProBlend technology', 1400, 199.99);

ALTER SEQUENCE appliances_id_seq RESTART WITH 9;

-- ============================================
-- ORDERS
-- ============================================
INSERT INTO orders (id, employee_id, client_id, approved)
VALUES (1, 3, 1, FALSE);

INSERT INTO orders (id, employee_id, client_id, approved)
VALUES (2, 4, 2, TRUE);

INSERT INTO orders (id, employee_id, client_id, approved)
VALUES (3, 3, 1, FALSE);

ALTER SEQUENCE orders_id_seq RESTART WITH 4;

-- ============================================
-- ORDER_ROWS
-- ============================================
-- Замовлення 1: Samsung Smart Fridge x2
INSERT INTO order_rows (id, order_id, appliance_id, number, amount)
VALUES (1, 1, 1, 2, 3999.98);

-- Замовлення 1: Bosch Washing Machine x1
INSERT INTO order_rows (id, order_id, appliance_id, number, amount)
VALUES (2, 1, 3, 1, 799.99);

-- Замовлення 2: Sony Bravia OLED TV x1
INSERT INTO order_rows (id, order_id, appliance_id, number, amount)
VALUES (3, 2, 5, 1, 2499.99);

-- Замовлення 2: Dyson V15 Detect x2
INSERT INTO order_rows (id, order_id, appliance_id, number, amount)
VALUES (4, 2, 4, 2, 1399.98);

-- Замовлення 3: Philips Air Fryer x3
INSERT INTO order_rows (id, order_id, appliance_id, number, amount)
VALUES (5, 3, 6, 3, 899.85);

ALTER SEQUENCE order_rows_id_seq RESTART WITH 6;

-- ============================================
-- VERIFY
-- ============================================
SELECT 'roles' AS table_name, COUNT(*) AS count FROM roles
UNION ALL
SELECT 'users', COUNT(*) FROM users
UNION ALL
SELECT 'clients', COUNT(*) FROM clients
UNION ALL
SELECT 'employees', COUNT(*) FROM employees
UNION ALL
SELECT 'user_roles', COUNT(*) FROM user_roles
UNION ALL
SELECT 'manufacturers', COUNT(*) FROM manufacturers
UNION ALL
SELECT 'appliances', COUNT(*) FROM appliances
UNION ALL
SELECT 'orders', COUNT(*) FROM orders
UNION ALL
SELECT 'order_rows', COUNT(*) FROM order_rows;