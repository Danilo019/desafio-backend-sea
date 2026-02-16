-- ===================================
-- SCHEMA DE INICIALIZAÇÃO - PRODUÇÃO
-- ===================================

-- Criar extensão para UUID (se necessário no futuro)
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Verificar se banco foi criado
SELECT 'Database initialized successfully!' AS status;

-- Tabelas serão criadas automaticamente pelo Hibernate (ddl-auto=validate)
-- Mas você pode adicionar scripts de migração aqui se necessário
