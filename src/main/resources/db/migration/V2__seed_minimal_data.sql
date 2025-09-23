-- V2: Inserção de dados mínimos de referência.
-- Objetivo: disponibilizar registros básicos para testes de navegação.

-- Filiais
INSERT INTO TB_FILIAL (NM_FILIAL, DS_ENDERECO, DS_BAIRRO, NR_CEP, DS_CIDADE, DS_ESTADO, VL_LATITUDE, VL_LONGITUDE, RAIO_GEOFENCE_M)
VALUES ('Matriz São Paulo', 'Av. Paulista, 1000', 'Bela Vista', '01310-000', 'São Paulo', 'SP', -23.5617, -46.6559, 500);

INSERT INTO TB_FILIAL (NM_FILIAL, DS_ENDERECO, DS_BAIRRO, NR_CEP, DS_CIDADE, DS_ESTADO, VL_LATITUDE, VL_LONGITUDE, RAIO_GEOFENCE_M)
VALUES ('Filial Rio', 'Rua das Laranjeiras, 200', 'Laranjeiras', '22240-003', 'Rio de Janeiro', 'RJ', -22.9410, -43.1872, 400);

-- Motos (referenciando a filial por nome)
INSERT INTO TB_MOTO (NR_ANO, DT_CRIACAO, DS_MARCA, DS_MODELO, CD_PLACA, DS_STATUS, VL_LATITUDE, VL_LONGITUDE, ID_FILIAL)
VALUES (
           2021, SYSTIMESTAMP, 'Honda', 'CG 160', 'ABC1D23', 'ATIVA', -23.5618, -46.6562,
           (SELECT ID_FILIAL FROM TB_FILIAL WHERE NM_FILIAL = 'Matriz São Paulo')
       );

INSERT INTO TB_MOTO (NR_ANO, DT_CRIACAO, DS_MARCA, DS_MODELO, CD_PLACA, DS_STATUS, VL_LATITUDE, VL_LONGITUDE, ID_FILIAL)
VALUES (
           2022, SYSTIMESTAMP, 'Yamaha', 'Factor 150', 'EFG4H56', 'ATIVA', -22.9412, -43.1870,
           (SELECT ID_FILIAL FROM TB_FILIAL WHERE NM_FILIAL = 'Filial Rio')
       );

-- 1 agendamento (referenciando moto por placa)
INSERT INTO TB_AGENDAMENTO (DT_AGENDADA, DT_CRIACAO, DS_DESCRICAO, ID_MOTO)
VALUES (
           SYSTIMESTAMP + INTERVAL '1' DAY, SYSTIMESTAMP, 'Manutenção preventiva',
           (SELECT ID_MOTO FROM TB_MOTO WHERE CD_PLACA = 'ABC1D23')
       );
