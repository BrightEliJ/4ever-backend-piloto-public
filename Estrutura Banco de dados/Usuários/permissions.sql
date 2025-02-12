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
