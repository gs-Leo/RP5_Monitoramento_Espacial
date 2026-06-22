# Spring Boot + Docker — Guia de Implantação

## Requisitos
- Docker Desktop (Windows/macOS) ou Docker Engine (Linux)

## Configuração do Ambiente

### 1. Criar o arquivo `.env`

O projeto usa variáveis de ambiente para gerenciar credenciais de forma segura.
**Nenhuma credencial é armazenada diretamente no `docker-compose.yml`.**

```bash
# Copie o modelo
cp .env.example .env

# Edite com seus valores reais
# (use seu editor preferido)
notepad .env       # Windows
nano .env          # Linux/macOS
```

### Variáveis disponíveis

| Variável | Descrição | Exemplo |
|---|---|---|
| `POSTGRES_DB` | Nome do banco PostgreSQL | `monitoramento_db` |
| `POSTGRES_USER` | Usuário do PostgreSQL | `admin` |
| `POSTGRES_PASSWORD` | Senha do PostgreSQL | `s3nh4_s3gur4` |
| `SPRING_DATASOURCE_URL` | JDBC URL (use `db` como host no Docker) | `jdbc:postgresql://db:5432/monitoramento_db` |
| `SPRING_DATASOURCE_USERNAME` | Mesmo que `POSTGRES_USER` | `admin` |
| `SPRING_DATASOURCE_PASSWORD` | Mesmo que `POSTGRES_PASSWORD` | `s3nh4_s3gur4` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Estratégia DDL do Hibernate | `update` |
| `SPRING_JPA_SHOW_SQL` | Exibir queries SQL no log | `true` |

> ⚠️ **Segurança:** O arquivo `.env` está no `.gitignore` e **nunca deve ser commitado**. Compartilhe credenciais por canais seguros.

### 2. Rodar com Docker Compose

```bash
# Build e execução (primeiro plano)
docker compose up --build

# Ou em segundo plano
docker compose up --build -d
```

Acesse: http://localhost:8080

### 3. Verificar conexão com o banco

```bash
# Verificar logs do backend
docker logs monitoramento-espacial-app

# Testar endpoint de saúde
curl http://localhost:8080/health
```

## Endpoints
- `GET /` → retorna um JSON com `message` e `status`
- `GET /health` → retorna `{ "status": "UP" }`

## Como parar
```bash
# Em primeiro plano
Ctrl+C

# Em segundo plano
docker compose down

# Remover volumes (apaga dados do banco)
docker compose down -v
```

## Arquitetura Docker

```
┌────────────────────────────────────────────────┐
│              docker-compose.yml                │
│                                                │
│  ┌──────────────┐      ┌──────────────────┐   │
│  │  app (8080)  │─────▶│  db (5432/5433)  │   │
│  │  Spring Boot │      │  PostgreSQL 16   │   │
│  └──────────────┘      └──────────────────┘   │
│         │                       │              │
│     env_file: .env         env_file: .env     │
└────────────────────────────────────────────────┘
```

## Fluxo das variáveis de ambiente

```
.env  →  docker-compose.yml (env_file)  →  Container Spring Boot
                                         →  Container PostgreSQL
```

O `application.properties` do Spring usa placeholders `${VARIAVEL}` que são
resolvidos automaticamente pelas variáveis de ambiente injetadas pelo Docker.

## Troubleshooting

| Problema | Solução |
|---|---|
| `Connection refused` ao banco | Verifique se `SPRING_DATASOURCE_URL` usa `db` (não `localhost`) como host |
| Banco não inicializa | Confira se `POSTGRES_DB`, `POSTGRES_USER` e `POSTGRES_PASSWORD` estão definidos no `.env` |
| Arquivo `.env` não encontrado | Execute `cp .env.example .env` e preencha os valores |
| Porta 8080 em uso | Altere a porta no `docker-compose.yml`: `"OUTRA_PORTA:8080"` |
