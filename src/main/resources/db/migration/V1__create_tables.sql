-- V1: Criação das tabelas base (Oracle 12c+ com IDENTITY).
-- Objetivo: disponibilizar o esquema inicial do banco.

CREATE TABLE TB_FILIAL (
                           ID_FILIAL           NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           NM_FILIAL           VARCHAR2(255 CHAR) NOT NULL,
                           DS_ENDERECO         VARCHAR2(255 CHAR),
                           DS_BAIRRO           VARCHAR2(255 CHAR),
                           NR_CEP              VARCHAR2(255 CHAR),
                           DS_CIDADE           VARCHAR2(255 CHAR),
                           DS_ESTADO           VARCHAR2(255 CHAR),
                           VL_LATITUDE         FLOAT(53),
                           VL_LONGITUDE        FLOAT(53),
                           RAIO_GEOFENCE_M     FLOAT(53)
);

CREATE TABLE TB_MOTO (
                         ID_MOTO       NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         NR_ANO        NUMBER(10) CHECK (NR_ANO >= 2000),
                         DT_CRIACAO    TIMESTAMP(6),
                         DS_MARCA      VARCHAR2(255 CHAR),
                         DS_MODELO     VARCHAR2(255 CHAR),
                         CD_PLACA      VARCHAR2(255 CHAR) NOT NULL UNIQUE,
                         DS_STATUS     VARCHAR2(255 CHAR),
                         VL_LATITUDE   FLOAT(53),
                         VL_LONGITUDE  FLOAT(53),
                         ID_FILIAL     NUMBER(19),
                         CONSTRAINT FK_MOTO_FILIAL FOREIGN KEY (ID_FILIAL) REFERENCES TB_FILIAL(ID_FILIAL)
);

CREATE TABLE TB_AGENDAMENTO (
                                ID_AGENDAMENTO NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                DT_AGENDADA    TIMESTAMP(6) NOT NULL,
                                DT_CRIACAO     TIMESTAMP(6),
                                DS_DESCRICAO   VARCHAR2(255 CHAR) NOT NULL,
                                ID_MOTO        NUMBER(19) NOT NULL,
                                CONSTRAINT FK_AGENDAMENTO_MOTO FOREIGN KEY (ID_MOTO) REFERENCES TB_MOTO(ID_MOTO)
);

CREATE TABLE TB_EVENTO (
                           ID_EVENTO      NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           DT_HR_EVENTO   TIMESTAMP(6) NOT NULL,
                           DS_LOCALIZACAO VARCHAR2(255 CHAR),
                           DS_MOTIVO      VARCHAR2(255 CHAR) NOT NULL,
                           TP_EVENTO      VARCHAR2(255 CHAR) NOT NULL,
                           ID_MOTO        NUMBER(19) NOT NULL,
                           CONSTRAINT FK_EVENTO_MOTO FOREIGN KEY (ID_MOTO) REFERENCES TB_MOTO(ID_MOTO)
);

CREATE TABLE TB_USUARIO (
                            ID_USUARIO NUMBER(19) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            DS_EMAIL   VARCHAR2(255 CHAR) NOT NULL UNIQUE,
                            NM_USUARIO VARCHAR2(255 CHAR) NOT NULL,
                            TP_PERFIL  VARCHAR2(255 CHAR) NOT NULL,
                            DS_SENHA   VARCHAR2(255 CHAR) NOT NULL,
                            ID_FILIAL  NUMBER(19),
                            CONSTRAINT FK_USUARIO_FILIAL FOREIGN KEY (ID_FILIAL) REFERENCES TB_FILIAL(ID_FILIAL)
);
