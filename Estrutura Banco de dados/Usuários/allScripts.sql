-- Start of users.sql
-- Tabela para Gerenciamento de Usuários
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


-- End of script

-- Start of logs.sql
-- public.logs definição com FK para tabela users

-- Drop table

-- DROP TABLE public.logs;

CREATE TABLE public.logs (
    id BIGSERIAL NOT NULL,                  -- Identificador único do log
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(), -- Data e hora do evento
    user_id BIGINT NULL,                    -- ID do usuário (FK para a tabela users)
    log_type VARCHAR(50) NOT NULL,          -- Tipo de log: ACCESS, REQUEST, etc.
    event VARCHAR(255) NULL,                -- Evento ou descrição do log
    endpoint VARCHAR(500) NULL,             -- Endpoint acessado (para logs de requisição)
    http_method VARCHAR(10) NULL,           -- Método HTTP usado: GET, POST, etc.
    response_status SMALLINT NULL,          -- Código de status da resposta HTTP
    client_ip VARCHAR(45) NULL,             -- IP do cliente (IPv4 ou IPv6)
    user_agent VARCHAR(500) NULL,           -- Informação sobre o agente de usuário
    execution_time DECIMAL(10, 3) NULL,     -- Tempo de execução em milissegundos
    payload JSONB NULL,                     -- Dados enviados ou retornados (opcional, para auditoria)
    CONSTRAINT logs_pkey PRIMARY KEY (id),
    CONSTRAINT fk_logs_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Adiciona índice para busca rápida por user_id
CREATE INDEX idx_logs_user_id ON public.logs USING btree (user_id);

-- Adiciona índice para busca rápida por timestamp
CREATE INDEX idx_logs_timestamp ON public.logs USING btree (timestamp);

-- Adiciona índice para busca rápida por log_type
CREATE INDEX idx_logs_log_type ON public.logs USING btree (log_type);

-- End of script

-- Start of groups.sql
-- public.groups definição

-- Drop table

-- DROP TABLE public.groups;

CREATE TABLE public.groups (
    id BIGSERIAL NOT NULL,                -- Identificador único do grupo
    name VARCHAR(255) NOT NULL,           -- Nome do grupo
    description TEXT NULL,                -- Descrição opcional do grupo
    group_type VARCHAR(50) NOT NULL,      -- Tipo do grupo: TEAM, COMPANY, etc.
    created_at TIMESTAMP DEFAULT NOW(),   -- Data de criação
    updated_at TIMESTAMP NULL,            -- Última atualização
    CONSTRAINT groups_pkey PRIMARY KEY (id),
    CONSTRAINT ck_group_type CHECK (group_type IN ('TEAM', 'COMPANY', 'OTHER')) -- Restringe os tipos válidos
);

-- Adiciona índice para busca rápida por tipo de grupo
CREATE INDEX idx_groups_type ON public.groups USING btree (group_type);

-- End of script

-- Start of permissions.sql
-- public.permissions definição

-- Drop table

-- DROP TABLE public.permissions;

CREATE TABLE public.permissions (
    id BIGSERIAL NOT NULL,                -- Identificador único da permissão
    name VARCHAR(255) NOT NULL,           -- Nome da permissão (ex.: VIEW_DASHBOARD)
    description TEXT NULL,                -- Descrição da permissão
    created_at TIMESTAMP DEFAULT NOW(),   -- Data de criação
    updated_at TIMESTAMP NULL,            -- Última atualização
    CONSTRAINT permissions_pkey PRIMARY KEY (id)
);

-- Adiciona índice para busca rápida por nome de permissão
CREATE INDEX idx_permissions_name ON public.permissions USING btree (name);

-- End of script

-- Start of group_permissions.sql
-- public.group_permissions definição

-- Drop table

-- DROP TABLE public.group_permissions;

CREATE TABLE public.group_permissions (
    group_id BIGINT NOT NULL,             -- ID do grupo (FK para groups)
    permission_id BIGINT NOT NULL,        -- ID da permissão (FK para permissions)
    granted_by BIGINT NULL,               -- ID do usuário que concedeu a permissão
    granted_at TIMESTAMP DEFAULT NOW(),   -- Data de concessão da permissão
    CONSTRAINT group_permissions_pkey PRIMARY KEY (group_id, permission_id),
    CONSTRAINT fk_group_permissions_group FOREIGN KEY (group_id) REFERENCES public.groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_group_permissions_permission FOREIGN KEY (permission_id) REFERENCES public.permissions(id) ON DELETE CASCADE
);

-- Índices para otimizar buscas por grupos e permissões
CREATE INDEX idx_group_permissions_group_id ON public.group_permissions USING btree (group_id);
CREATE INDEX idx_group_permissions_permission_id ON public.group_permissions USING btree (permission_id);

-- End of script

-- Start of user_groups.sql
-- public.user_groups definição

-- Drop table

-- DROP TABLE public.user_groups;

CREATE TABLE public.user_groups (
    user_id BIGINT NOT NULL,              -- ID do usuário (FK para users)
    group_id BIGINT NOT NULL,             -- ID do grupo (FK para groups)
    joined_at TIMESTAMP DEFAULT NOW(),    -- Data de entrada no grupo
    role_in_group VARCHAR(50) NULL,       -- Papel do usuário no grupo: ADMIN, MEMBER, etc.
    CONSTRAINT user_groups_pkey PRIMARY KEY (user_id, group_id),
    CONSTRAINT fk_user_groups_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_groups_group FOREIGN KEY (group_id) REFERENCES public.groups(id) ON DELETE CASCADE
);

-- Índices para otimizar buscas por usuários e grupos
CREATE INDEX idx_user_groups_user_id ON public.user_groups USING btree (user_id);
CREATE INDEX idx_user_groups_group_id ON public.user_groups USING btree (group_id);

-- End of script

-- Start of user_permissions.sql
-- public.user_permissions definição

-- Drop table

-- DROP TABLE public.user_permissions;

CREATE TABLE public.user_permissions (
    user_id BIGINT NOT NULL,              -- ID do usuário (FK para users)
    permission_id BIGINT NOT NULL,        -- ID da permissão (FK para permissions)
    granted_by BIGINT NULL,               -- ID do administrador ou sistema que concedeu a permissão
    granted_at TIMESTAMP DEFAULT NOW(),   -- Data de concessão da permissão
    expiration_at TIMESTAMP NULL,         -- Data de expiração da permissão (opcional)
    CONSTRAINT user_permissions_pkey PRIMARY KEY (user_id, permission_id),
    CONSTRAINT fk_user_permissions_user FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_permissions_permission FOREIGN KEY (permission_id) REFERENCES public.permissions(id) ON DELETE CASCADE
);

-- Índices para otimizar buscas por usuários e permissões
CREATE INDEX idx_user_permissions_user_id ON public.user_permissions USING btree (user_id);
CREATE INDEX idx_user_permissions_permission_id ON public.user_permissions USING btree (permission_id);

-- Tabela para associar convites para os grupos
CREATE TABLE group_invitation_codes (
    id BIGSERIAL PRIMARY KEY,
    group_id BIGINT NOT NULL,
    code VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT,
    CONSTRAINT fk_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE,
    CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Tabela para guardar tokens revogados
CREATE TABLE token_blacklist (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL,
    revoked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- End of script

