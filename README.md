# 🛵 MotoTrack - Sistema Inteligente de Gestão e Mapeamento de Motos

## 📄 Descrição do Projeto

O **MotoTrack** é um sistema inteligente para **mapeamento, monitoramento e gestão** das motocicletas nos pátios das filiais da **Mottu**.

O projeto foi desenvolvido no contexto do **Challenge 2025 - 1º Semestre (FIAP + Mottu)**, integrando disciplinas de **Java Advanced, DevOps, Mobile e Banco de Dados**.

Funcionalidades centrais:
- Gestão de motos, filiais, eventos e agendamentos.
- API RESTful documentada.
- Integração com visão computacional (rastreamento de motos).
- Banco de dados relacional em nuvem.
- Deploy em **Docker + Azure**.

---

## 👨‍💻 Integrantes

- Rafael Rodrigues de Almeida — RM: 557837
- Lucas Kenji Miyahira — RM: 555368

---

## 🚀 Tecnologias Utilizadas

- **Java 21 + Spring Boot 3.4.5**
- **Spring Data JPA + Oracle / H2**
- **Swagger / OpenAPI 3**
- **ModelMapper + Lombok**
- **Spring Validation + Exception Handler**
- **Spring Cache**
- **Flyway** (migrations)
- **Spring Security** (login + perfis de acesso)
- **Docker / Docker Hub**
- **Azure CLI + App Service / ACR + ACI**
- **Thymeleaf** (frontend web)
- **React Native (Expo)** (aplicativo mobile)

---

## 📦 Funcionalidades Principais

- **CRUD Completo** para Motos, Filiais, Eventos, Agendamentos e Usuários.
- **Relacionamentos entre entidades** com JPA.
- **Paginação, ordenação e filtros dinâmicos**.
- **Validação de campos** com Bean Validation.
- **Cache** para otimizar requisições.
- **Tratamento centralizado de erros**.
- **Autenticação e autorização** com Spring Security.
- **Visão computacional**: detecção e rastreamento de motos em vídeo.

---

## 📑 Rotas da API

### 🔹 Motos
- `GET /motos` → listar
- `GET /motos/{id}` → buscar por ID
- `POST /motos` → cadastrar
- `PUT /motos/{id}` → atualizar
- `DELETE /motos/{id}` → remover
- `GET /motos/filtro` → filtrar

### 🔹 Filiais
- `GET /filiais` | `POST /filiais` | `PUT /filiais/{id}` | `DELETE /filiais/{id}`

### 🔹 Eventos
- `GET /eventos` | `POST /eventos` | `PUT /eventos/{id}` | `DELETE /eventos/{id}` | `GET /eventos/filtro`

### 🔹 Agendamentos
- `GET /agendamentos` | `POST /agendamentos` | `PUT /agendamentos/{id}` | `DELETE /agendamentos/{id}` | `GET /agendamentos/filtro`

### 🔹 Usuários
- `GET /usuarios` | `POST /usuarios` | `PUT /usuarios/{id}` | `DELETE /usuarios/{id}` | `GET /usuarios/filtro`

---

## 📝 Instalação e Execução

### ✅ Pré-requisitos
- **Java 21**
- **Maven 3.9+**
- **Docker** (para deploy)
- Banco **Oracle** (produção) ou **H2** (teste local)

### ✅ Clonar o repositório
```bash
git clone https://github.com/seuusuario/mototrack.git
cd mototrack
```

### ✅ Executar com Maven
```bash
mvn clean install
mvn spring-boot:run
```
Acesse em: [http://localhost:8080](http://localhost:8080)

### ✅ Executar com Docker
```bash
docker build -t mototrack:1.0 .
docker run -p 8080:8080 mototrack:1.0
```
Acesse em: [http://localhost:8080](http://localhost:8080)

### ✅ Deploy no Azure (App Service)
```bash
az group create --name rg-mototrack --location brazilsouth
az appservice plan create --name plan-mototrack --resource-group rg-mototrack --sku B1 --is-linux
az webapp create --resource-group rg-mototrack --plan plan-mototrack --name app-mototrack-rm557837 --deployment-container-image-name rafael051/mototrack:1.0
```
Acesse a aplicação no link público gerado pelo Azure.

---

## 🔑 Acesso à Aplicação

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Credenciais padrão (Spring Security)**:
    - Usuário: `admin`
    - Senha: `admin` (ou definida em variáveis de ambiente)

---

## 📽️ Demonstração em Vídeo
🔗 *(adicionar link do YouTube da entrega Sprint)*

---

## 📚 Benefícios para o Negócio

- **Eficiência operacional**: motos localizadas e monitoradas em tempo real.
- **Redução de fraudes e extravios**: visão computacional identifica uso irregular.
- **Gestão inteligente**: integração entre API, banco e app mobile.
- **Escalabilidade**: arquitetura em microsserviços preparada para nuvem.  
