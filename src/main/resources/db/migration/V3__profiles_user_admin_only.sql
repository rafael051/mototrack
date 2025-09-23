-- V3: Perfis restritos a USER/ADMIN.
-- Objetivo: garantir integridade do domínio de TP_PERFIL.

-- Converte somente equivalente de ADMIN (sem mapear outros termos).
UPDATE TB_USUARIO
SET TP_PERFIL = 'ADMIN'
WHERE UPPER(TP_PERFIL) = 'ADMINISTRADOR';

-- Aborta a migração se houver valores fora do domínio permitido.
DECLARE
v_count NUMBER;
BEGIN
SELECT COUNT(*) INTO v_count
FROM TB_USUARIO
WHERE UPPER(TP_PERFIL) NOT IN ('USER','ADMIN');

IF v_count > 0 THEN
    RAISE_APPLICATION_ERROR(
      -20001,
      'V3 abortada: existem usuários com TP_PERFIL inválido. Ajuste para USER/ADMIN e execute novamente.'
    );
END IF;
END;
/

-- Remove constraint anterior (se existir) e cria a definitiva.
BEGIN
EXECUTE IMMEDIATE 'ALTER TABLE TB_USUARIO DROP CONSTRAINT CK_USUARIO_TP_PERFIL';
EXCEPTION
  WHEN OTHERS THEN
    IF SQLCODE != -2443 THEN RAISE; END IF; -- ORA-02443 = constraint inexistente
END;
/

ALTER TABLE TB_USUARIO
    ADD CONSTRAINT CK_USUARIO_TP_PERFIL
        CHECK (TP_PERFIL IN ('USER','ADMIN'))
            NOT DEFERRABLE INITIALLY IMMEDIATE;
