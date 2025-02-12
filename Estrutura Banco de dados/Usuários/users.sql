-- Tabela para Gerenciamento de Usuários
CREATE TABLE public.users (
    id BIGSERIAL PRIMARY KEY,          -- Identificador único do usuário
    username VARCHAR(50) NOT NULL,     -- Nome de usuário único para login
    password VARCHAR(255) NOT NULL,    -- Senha criptografada (ex.: BCrypt)
    email VARCHAR(255) NOT NULL,       -- Email do usuário
    email_verified VARCHAR(10) NOT NULL,        -- Indica se o email foi verificado
    full_name VARCHAR(150),            -- Nome completo do usuário
    phone_number VARCHAR(25),          -- Número de telefone
    status VARCHAR(10) NOT NULL,       -- Status ativo/inativo
    created_at TIMESTAMP DEFAULT NOW(),-- Data de criação do usuário
    updated_at TIMESTAMP,              -- Última atualização do registro
    last_access TIMESTAMP,             -- Último acesso do usuário
    login_attempts INT DEFAULT 0,      -- Tentativas de login falhas
    activation_token VARCHAR(255),     -- Token para ativação de conta
    two_factor_secret VARCHAR(255),    -- Código para autenticação 2FA
    qr_code_url VARCHAR(255),           -- URL do QR Code para 2FA
	matricula VARCHAR(255),
	bio TEXT,
	CONSTRAINT check_status_values CHECK (status IN ('ativo', 'inativo', 'bloqueado')), -- Regra de validação
	CONSTRAINT check_email_verified_values CHECK (email_verified IN ('sim', 'não')) -- Regra de validação
);
