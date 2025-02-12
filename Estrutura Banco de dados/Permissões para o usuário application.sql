DO $$
DECLARE
    object_name TEXT;
BEGIN
    -- Conceder permissões em todas as tabelas
    FOR object_name IN
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'public'
    LOOP
        EXECUTE FORMAT('GRANT ALL PRIVILEGES ON TABLE public.%I TO application;', object_name);
    END LOOP;

    -- Conceder permissões em todas as views
    FOR object_name IN
        SELECT viewname
        FROM pg_views
        WHERE schemaname = 'public'
    LOOP
        EXECUTE FORMAT('GRANT ALL PRIVILEGES ON TABLE public.%I TO application;', object_name);
    END LOOP;

    -- Conceder permissões em todas as sequences
    FOR object_name IN
        SELECT sequencename
        FROM pg_sequences
        WHERE schemaname = 'public'
    LOOP
        EXECUTE FORMAT('GRANT ALL PRIVILEGES ON SEQUENCE public.%I TO application;', object_name);
    END LOOP;

    -- Configurar padrão para novos objetos
    EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO application;';
    EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON SEQUENCES TO application;';
    EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON FUNCTIONS TO application;';
END $$;
