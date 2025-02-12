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
