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
