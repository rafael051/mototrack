-- V5: Usuários iniciais (ADMIN e USER) com MERGE (idempotente).
-- Objetivo: permitir autenticação inicial no sistema.

-- ATENÇÃO: Substitua os hashes BCRYPT antes de executar.
-- Gere com BCryptPasswordEncoder na aplicação e cole abaixo.

-- ADMIN
MERGE INTO TB_USUARIO t
    USING (SELECT 'admin@moto.local' AS DS_EMAIL FROM dual) s
    ON (t.DS_EMAIL = s.DS_EMAIL)
    WHEN NOT MATCHED THEN
        INSERT (DS_EMAIL, NM_USUARIO, TP_PERFIL, DS_SENHA, ID_FILIAL)
            VALUES ('admin@moto.local', 'Administrador', 'ADMIN', '$2a$10$SUBSTITUA_HASH_ADMIN', NULL);

-- USER
MERGE INTO TB_USUARIO t
    USING (SELECT 'user@moto.local' AS DS_EMAIL FROM dual) s
    ON (t.DS_EMAIL = s.DS_EMAIL)
    WHEN NOT MATCHED THEN
        INSERT (DS_EMAIL, NM_USUARIO, TP_PERFIL, DS_SENHA, ID_FILIAL)
            VALUES ('user@moto.local', 'Usuário Padrão', 'USER', '$2a$10$SUBSTITUA_HASH_USER', NULL);
