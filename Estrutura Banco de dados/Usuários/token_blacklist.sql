-- Tabela para guardar tokens revogados
CREATE TABLE token_blacklist (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL,
    revoked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
