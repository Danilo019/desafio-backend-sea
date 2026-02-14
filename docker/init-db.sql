-- ===================================================================
-- Script de Inicialização - Desafio Backend SEA
-- ===================================================================
-- Executado automaticamente na primeira criação do container PostgreSQL
--
-- Nota: As tabelas serão criadas automaticamente pelo Hibernate
--       com spring.jpa.hibernate.ddl-auto=update

-- Habilitar extensões úteis do PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Log de inicialização
DO $$
BEGIN
    RAISE NOTICE '==============================================';
    RAISE NOTICE 'Banco de dados: desafiodb';
    RAISE NOTICE 'Usuário: admin';
    RAISE NOTICE 'Status: Inicializado com sucesso!';
    RAISE NOTICE 'Schema: Será criado automaticamente pelo Hibernate';
    RAISE NOTICE '==============================================';
END $$;

-- Você pode adicionar dados de exemplo (seed data) aqui se desejar
-- Exemplo:
-- INSERT INTO usuario (nome, email, senha, created_at, updated_at) 
-- VALUES ('Admin', 'admin@test.com', '$2a$10$...', NOW(), NOW());
