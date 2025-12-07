-- Inserir usuário de teste
-- Senha: "123456" (hash BCrypt)
INSERT INTO users (username, email, password, created_at)
VALUES ('admin', 'admin@analyx.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhCy', CURRENT_TIMESTAMP);

-- Inserir projeto de exemplo
INSERT INTO projects (user_id, name, description, created_at)
VALUES (1, 'Projeto Exemplo', 'Projeto de teste para análise', CURRENT_TIMESTAMP);