# RP5 Monitoramento Espacial

## Requisitos
- Docker Desktop (Windows/macOS) ou Docker Engine (Linux)

## Configuração do Ambiente

### 1. Criar o arquivo `.env`

Copie o arquivo de exemplo e preencha com seus valores reais:

```bash
cp .env.example .env
```

Edite o `.env` com as credenciais do seu banco de dados:

```env
POSTGRES_DB=monitoramento_db
POSTGRES_USER=seu_usuario
POSTGRES_PASSWORD=sua_senha_segura

SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/monitoramento_db
SPRING_DATASOURCE_USERNAME=seu_usuario
SPRING_DATASOURCE_PASSWORD=sua_senha_segura

SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
```

> ⚠️ **Importante:** O arquivo `.env` contém credenciais sensíveis e **não deve ser commitado** no repositório. Ele já está listado no `.gitignore`.

### 2. Rodar o projeto

```bash
docker compose up --build
```

Acesse: http://localhost:8080

### Endpoints
- `GET /` → retorna um JSON com `message` e `status`
- `GET /health` → retorna `{ "status": "UP" }`

## Como parar
- `Ctrl+C` para parar o compose em primeiro plano
- Em segundo plano: `docker compose up -d` / `docker compose down`

## Estrutura
```
.env.example                    ← Modelo de variáveis de ambiente
.env                            ← Credenciais reais (não commitado)
docker-compose.yml              ← Orquestração dos containers
Dockerfile                      ← Build da aplicação Spring Boot
src/main/java/resources/
  application.properties        ← Configuração do Spring (usa ${...} do .env)
pom.xml                         ← Dependências Maven
```
