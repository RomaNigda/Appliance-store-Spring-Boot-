-- ============================================
-- 1. ОЧИЩЕННЯ ТАБЛИЦЬ
-- ============================================
TRUNCATE TABLE order_rows CASCADE;
TRUNCATE TABLE orders CASCADE;
TRUNCATE TABLE user_roles CASCADE;
TRUNCATE TABLE clients CASCADE;
TRUNCATE TABLE employees CASCADE;
TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE appliances CASCADE;
TRUNCATE TABLE manufacturers CASCADE;
TRUNCATE TABLE roles CASCADE;

-- ============================================
-- 2. СКИДАННЯ SEQUENCES (правильний спосіб для PostgreSQL)
-- ============================================
-- Використовуйте pg_get_serial_sequence() для отримання назви sequence
SELECT setval(pg_get_serial_sequence('roles', 'id'), 1, false);
SELECT setval(pg_get_serial_sequence('users', 'id'), 1, false);
SELECT setval(pg_get_serial_sequence('manufacturers', 'id'), 1, false);
SELECT setval(pg_get_serial_sequence('appliances', 'id'), 1, false);
SELECT setval(pg_get_serial_sequence('orders', 'id'), 1, false);
SELECT setval(pg_get_serial_sequence('order_rows', 'id'), 1, false);

-- ============================================
-- 3. РОЛІ (БЕЗ вказання id — використовуємо SERIAL/IDENTITY)
-- ============================================
INSERT INTO roles (name) VALUES ('ROLE_CLIENT');
INSERT INTO roles (name) VALUES ('ROLE_EMPLOYEE');

-- ============================================
-- 4. КОРИСТУВАЧІ (БЕЗ id — БД сама згенерує)
-- Пароль для всіх: password123
-- ============================================
INSERT INTO users (name, email, password)
VALUES ('John Client', 'john@client.com', '$2a$10$6tBJiP5TqYS4lBwZmS/a5O7H.LyWqB2lK8KqxWxNlKxWxNlKxWxN');

INSERT INTO users (name, email, password)
VALUES ('Mary Client', 'mary@client.com', '$2a$10$6tBJiP5TqYS4lBwZmS/a5O7H.LyWqB2lK8KqxWxNlKxWxNlKxWxN');

INSERT INTO users (name, email, password)
VALUES ('Jane Employee', 'jane@employee.com', '$2a$10$6tBJiP5TqYS4lBwZmS/a5O7H.LyWqB2lK8KqxWxNlKxWxNlKxWxN');

INSERT INTO users (name, email, password)
VALUES ('Bob Employee', 'bob@employee.com', '$2a$10$6tBJiP5TqYS4lBwZmS/a5O7H.LyWqB2lK8KqxWxNlKxWxNlKxWxN');

-- ============================================
-- 5. КЛІЄНТИ (використовуємо підзапити для отримання user_id)
-- ============================================
INSERT INTO clients (user_id, card)
VALUES ((SELECT id FROM users WHERE email = 'john@client.com'), '1234-5678-9012-3456');

INSERT INTO clients (user_id, card)
VALUES ((SELECT id FROM users WHERE email = 'mary@client.com'), '9876-5432-1098-7654');

-- ============================================
-- 6. ПРАЦІВНИКИ
-- ============================================
INSERT INTO employees (user_id, department)
VALUES ((SELECT id FROM users WHERE email = 'jane@employee.com'), 'IT Department');

INSERT INTO employees (user_id, department)
VALUES ((SELECT id FROM users WHERE email = 'bob@employee.com'), 'HR Department');

-- ============================================
-- 7. РОЛІ КОРИСТУВАЧІВ
-- ============================================
-- Клієнти
INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM users WHERE email = 'john@client.com'),
           (SELECT id FROM roles WHERE name = 'ROLE_CLIENT')
       );

INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM users WHERE email = 'mary@client.com'),
           (SELECT id FROM roles WHERE name = 'ROLE_CLIENT')
       );

-- Працівники
INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM users WHERE email = 'jane@employee.com'),
           (SELECT id FROM roles WHERE name = 'ROLE_EMPLOYEE')
       );

INSERT INTO user_roles (user_id, role_id)
VALUES (
           (SELECT id FROM users WHERE email = 'bob@employee.com'),
           (SELECT id FROM roles WHERE name = 'ROLE_EMPLOYEE')
       );

-- ============================================
-- 8. ВИРОБНИКИ (БЕЗ id)
-- ============================================
INSERT INTO manufacturers (name) VALUES ('Samsung Electronics');
INSERT INTO manufacturers (name) VALUES ('Apple Inc.');
INSERT INTO manufacturers (name) VALUES ('LG Electronics');
INSERT INTO manufacturers (name) VALUES ('Sony Corporation');
INSERT INTO manufacturers (name) VALUES ('Bosch');
INSERT INTO manufacturers (name) VALUES ('Philips');
INSERT INTO manufacturers (name) VALUES ('Dyson');
INSERT INTO manufacturers (name) VALUES ('Whirlpool');

-- ============================================
-- 9. ПРИЛАДИ (використовуємо підзапити)
-- ============================================
INSERT INTO appliances (name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (
           'Samsung Smart Fridge',
           'BIG',
           'RF28R7351SR',
           (SELECT id FROM manufacturers WHERE name = 'Samsung Electronics'),
           'AC220',
           'SmartThings compatible, 28 cu. ft.',
           'Large capacity French door refrigerator with AI features',
           600,
           1999.99
       );

INSERT INTO appliances (name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (
           'LG InstaView Fridge',
           'BIG',
           'LFXS28968S',
           (SELECT id FROM manufacturers WHERE name = 'LG Electronics'),
           'AC220',
           'InstaView door-in-door, SmartThinQ',
           'French door refrigerator with water dispenser',
           550,
           2499.00
       );

INSERT INTO appliances (name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (
           'Bosch Washing Machine',
           'BIG',
           'WAW285H2EU',
           (SELECT id FROM manufacturers WHERE name = 'Bosch'),
           'AC220',
           '9kg capacity, 1400 RPM',
           'Energy efficient washing machine with EcoSilence Drive',
           2000,
           799.99
       );

INSERT INTO appliances (name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (
           'Dyson V15 Detect',
           'SMALL',
           'SV22',
           (SELECT id FROM manufacturers WHERE name = 'Dyson'),
           'ACCUMULATOR',
           'Laser dust detection, 230 AW suction',
           'Cordless vacuum with laser technology and LCD screen',
           800,
           699.99
       );

INSERT INTO appliances (name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (
           'Sony Bravia OLED TV',
           'BIG',
           'XR-65A80L',
           (SELECT id FROM manufacturers WHERE name = 'Sony Corporation'),
           'AC220',
           '65 inch, 4K OLED, HDR',
           'Smart TV with Google TV and Acoustic Surface Audio',
           450,
           2499.99
       );

INSERT INTO appliances (name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (
           'Philips Air Fryer',
           'SMALL',
           'HD9860/90',
           (SELECT id FROM manufacturers WHERE name = 'Philips'),
           'AC220',
           '7.2L capacity, fat removal technology',
           'Digital air fryer with Smart Sensing',
           1700,
           299.95
       );

INSERT INTO appliances (name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (
           'Samsung Microwave',
           'SMALL',
           'ME9114S1',
           (SELECT id FROM manufacturers WHERE name = 'Samsung Electronics'),
           'AC220',
           '23L capacity, 800W',
           'Compact microwave with ceramic enamel interior',
           800,
           149.99
       );

INSERT INTO appliances (name, category, model, manufacturer_id, power_type, characteristic, description, power, price)
VALUES (
           'Philips Blender',
           'SMALL',
           'HR3655/00',
           (SELECT id FROM manufacturers WHERE name = 'Philips'),
           'AC220',
           '1400W, 6 blades',
           'High-speed blender with ProBlend technology',
           1400,
           199.99
       );

-- ============================================
-- 10. ЗАМОВЛЕННЯ
-- ============================================
INSERT INTO orders (approved, client_id, employee_id)
VALUES (
           false,
           (SELECT id FROM users WHERE email = 'john@client.com'),
           (SELECT id FROM users WHERE email = 'jane@employee.com')
       );

INSERT INTO orders (approved, client_id, employee_id)
VALUES (
           true,
           (SELECT id FROM users WHERE email = 'mary@client.com'),
           (SELECT id FROM users WHERE email = 'bob@employee.com')
       );

INSERT INTO orders (approved, client_id, employee_id)
VALUES (
           false,
           (SELECT id FROM users WHERE email = 'john@client.com'),
           (SELECT id FROM users WHERE email = 'jane@employee.com')
       );

-- ============================================
-- 11. РЯДКИ ЗАМОВЛЕНЬ
-- ============================================

-- Замовлення 1: Samsung Smart Fridge x2
INSERT INTO order_rows (order_id, appliance_id, number, amount)
VALUES (
           (SELECT id FROM orders WHERE approved = false AND client_id = (SELECT id FROM users WHERE email = 'john@client.com') LIMIT 1),
           (SELECT id FROM appliances WHERE name = 'Samsung Smart Fridge'),
           2,
           3999.98
       );

-- Замовлення 1: Bosch Washing Machine x1
INSERT INTO order_rows (order_id, appliance_id, number, amount)
VALUES (
           (SELECT id FROM orders WHERE approved = false AND client_id = (SELECT id FROM users WHERE email = 'john@client.com') LIMIT 1),
           (SELECT id FROM appliances WHERE name = 'Bosch Washing Machine'),
           1,
           799.99
       );

-- Замовлення 2: Sony Bravia OLED TV x1
INSERT INTO order_rows (order_id, appliance_id, number, amount)
VALUES (
           (SELECT id FROM orders WHERE approved = true AND client_id = (SELECT id FROM users WHERE email = 'mary@client.com') LIMIT 1),
           (SELECT id FROM appliances WHERE name = 'Sony Bravia OLED TV'),
           1,
           2499.99
       );

-- Замовлення 2: Dyson V15 Detect x2
INSERT INTO order_rows (order_id, appliance_id, number, amount)
VALUES (
           (SELECT id FROM orders WHERE approved = true AND client_id = (SELECT id FROM users WHERE email = 'mary@client.com') LIMIT 1),
           (SELECT id FROM appliances WHERE name = 'Dyson V15 Detect'),
           2,
           1399.98
       );

-- ============================================
-- 12. ПЕРЕВІРКА
-- ============================================
SELECT 'users' AS table_name, COUNT(*) AS count FROM users
UNION ALL
SELECT 'roles', COUNT(*) FROM roles
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