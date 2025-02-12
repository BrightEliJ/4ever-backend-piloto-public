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
