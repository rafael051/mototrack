# 🛵 MotoTrack - Sistema Inteligente de Gestão e Mapeamento de Motos

## 📄 Descrição do Projeto

Este projeto faz parte do **Challenge 2025 - 1º Semestre** promovido pela FIAP em parceria com a empresa **Mottu**.

O **MotoTrack** é um sistema inteligente para mapeamento, monitoramento e gestão das motocicletas nos pátios das filiais da Mottu, utilizando tecnologias como visão computacional, API RESTful e banco de dados relacional.

---

## 👨‍💻 Integrantes

- **Nome:** Rafael Rodrigues de Almeida - RM: 557837
- **Nome:** Lucas Kenji Miyahira - RM: 555368
  

---

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Data JPA**
- **Spring Validation**
- **Swagger/OpenAPI 3**
- **Banco de Dados Oracle** (ou H2 para testes)
- **ModelMapper**
- **Lombok**
- **Docker** para deploy em nuvem
- **Azure CLI** para provisionamento da infraestrutura

---

## 📦 Funcionalidades da API

- CRUD completo para **Moto**, **Filial**, **Evento**, **Agendamento**, **Usuário**.
- Relacionamento entre entidades.
- Paginação, ordenação e filtros dinâmicos.
- Validação de campos com **Bean Validation**.
- Tratamento centralizado de erros.
- Cache para otimização de requisições.
- Documentação automatizada via **Swagger**.

---

## 📑 Rotas Disponíveis

### ✅ Motos
- `GET /motos` → Listar todas as motos.
- `GET /motos/{id}` → Buscar moto por ID.
- `POST /motos` → Cadastrar nova moto.
- `PUT /motos/{id}` → Atualizar moto existente.
- `DELETE /motos/{id}` → Remover moto.
- `GET /motos/filtro` → Filtrar motos com paginação e ordenação.

---

### ✅ Filiais
- `GET /filiais` → Listar todas as filiais.
- `GET /filiais/{id}` → Buscar filial por ID.
- `POST /filiais` → Cadastrar nova filial.
- `PUT /filiais/{id}` → Atualizar filial existente.
- `DELETE /filiais/{id}` → Remover filial.

---

### ✅ Eventos
- `GET /eventos` → Listar todos os eventos.
- `GET /eventos/{id}` → Buscar evento por ID.
- `POST /eventos` → Registrar novo evento.
- `PUT /eventos/{id}` → Atualizar evento existente.
- `DELETE /eventos/{id}` → Remover evento.
- `GET /eventos/filtro` → Filtrar eventos com paginação e ordenação.

---

### ✅ Agendamentos
- `GET /agendamentos` → Listar todos os agendamentos.
- `GET /agendamentos/{id}` → Buscar agendamento por ID.
- `POST /agendamentos` → Criar novo agendamento.
- `PUT /agendamentos/{id}` → Atualizar agendamento existente.
- `DELETE /agendamentos/{id}` → Remover agendamento.
- `GET /agendamentos/filtro` → Filtrar agendamentos com paginação e ordenação.

---

### ✅ Usuários
- `GET /usuarios` → Listar todos os usuários.
- `GET /usuarios/{id}` → Buscar usuário por ID.
- `POST /usuarios` → Criar novo usuário.
- `PUT /usuarios/{id}` → Atualizar dados de usuário.
- `DELETE /usuarios/{id}` → Remover usuário.
- `GET /usuarios/filtro` → Filtrar usuários com paginação e ordenação.

---

## 📝 Instruções de Instalação e Execução

### ✅ Pré-requisitos

- Java 21
- Maven 3.9+
- Docker (para execução em container)
- Banco Oracle ou H2 configurado

### ✅ Clonar o repositório

```bash
git clone https://github.com/seuusuario/mototrack.git
cd mototrack
