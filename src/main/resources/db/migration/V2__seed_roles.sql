-- ============================================
-- V2: Seed initial roles
-- ============================================
INSERT INTO roles (name) VALUES
                             ('ROLE_EMPLOYEE'),
                             ('ROLE_CLIENT')
ON CONFLICT (name) DO NOTHING;