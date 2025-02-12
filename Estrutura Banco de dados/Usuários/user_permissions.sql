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
