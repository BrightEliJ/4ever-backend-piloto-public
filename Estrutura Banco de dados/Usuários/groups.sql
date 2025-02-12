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
