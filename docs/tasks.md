# Tasks de Manutenção e Evolução

## Objetivo

Este documento consolida as tasks de manutenção e evolução do projeto com base em:

- `docs/Matriz_Rastreabilidade_v2.5.docx`
- `docs/Especificação de Requisitos Sistema de Controle e Monitoramento de Agência Espacial - V2.docx`

O foco aqui é transformar as propostas dos documentos em um backlog executável, priorizado e alinhado ao estado atual do repositório.

## Premissas adotadas

- A priorização considera primeiro correções estruturais, aderência documental e bases técnicas para evolução.
- Itens explicitamente acordados na matriz receberam prioridade maior.
- Requisitos documentados mas fora do escopo atual continuam registrados, porém não entram como frente imediata se não houver encaminhamento técnico na matriz.

## Roadmap sugerido

### Fase 1. Estabilização e aderência

#### TSK-01. Corrigir testes do simulador Python e alinhar com a API real

- Tipo: manutenção
- Prioridade: alta
- Origem: matriz 4.2.2, RF04
- Áreas afetadas: `simulacao-python/tests`, `simulacao-python/api`
- Ações:
- Corrigir `tests/test_api.py` para usar `api.main:app`
- Ajustar cenários para `GET /health` e rotas reais `/simulacoes/*`
- Corrigir `tests/test_simulacao.py`, que hoje importa `RocketSimulation` de módulo inexistente
- Padronizar dependências mínimas para execução local e CI
- Critério de pronto:
- Testes Python executam sem falha
- Os cenários cobrem pelo menos health check e uma simulação real de foguete

#### TSK-02. Criar base de testes automatizados para o backend Java

- Tipo: manutenção
- Prioridade: alta
- Origem: matriz 4.2.1, NF02, RF01, RF04, RF11
- Áreas afetadas: `src/test/java`, controllers, services e tratamento de erro
- Ações:
- Criar suíte inicial com `MockMvc` ou `@SpringBootTest`
- Cobrir `MissaoController`, fluxos críticos de `AmeacaService` e `RestExceptionHandler`
- Definir massa mínima de testes para criação, consulta e validação de erro
- Critério de pronto:
- Existe árvore `src/test/java`
- Backend possui testes automatizados para fluxos críticos
- Falhas de validação e recurso inexistente têm cobertura

#### TSK-03. Alinhar documentação e rastreabilidade com o executável

- Tipo: manutenção
- Prioridade: alta
- Origem: matriz 4.1.5, 4.3, 5.4.14
- Áreas afetadas: `docs/`
- Ações:
- Atualizar diagramas de classe e mapeamento ER para refletir entidades JPA reais
- Revisar diagramas de sequência para refletir `Controller -> Service -> Repository` e integração com simulador Python
- Rotular diagramas de implantação como visão alvo quando não representarem exatamente a stack atual
- Completar lacunas documentais do UC-10 e revisar ausência de artefatos de UC-08
- Critério de pronto:
- Documentação principal deixa explícito o que é implementado hoje e o que é visão futura
- Não há contradição entre requisito, diagrama e código para os casos prioritários

### Fase 2. Fundamentos arquiteturais

#### TSK-04. Versionar a API REST em `/api/v1`

- Tipo: evolução
- Prioridade: alta
- Origem: matriz 5.3.2, RNF01
- Áreas afetadas: controllers Java, `frontend/space-control-center/lib/api.ts`, documentação OpenAPI
- Ações:
- Introduzir prefixo versionado em todos os endpoints públicos
- Ajustar cliente frontend e documentação
- Definir estratégia de transição para rotas antigas, se necessário
- Critério de pronto:
- Endpoints públicos respondem sob `/api/v1`
- Frontend consome a versão nova sem regressão funcional

#### TSK-05. Implementar autenticação, autorização por perfil e trilha de auditoria

- Tipo: evolução
- Prioridade: alta
- Origem: ERS NF01, RF12; matriz 5.3.1 e 5.5
- Áreas afetadas: backend Java, integrações externas, possivelmente frontend
- Ações:
- Adicionar `SecurityConfig`, entidade de usuário e serviço JWT/OAuth2
- Restringir operações críticas de missão, simulação, emergência, compartilhamento externo e gestão de pessoal
- Registrar auditoria de acessos negados e operações sensíveis
- Critério de pronto:
- Operações críticas exigem autenticação
- Perfis distintos têm permissões controladas
- Há registro auditável de ações sensíveis

#### TSK-06. Tornar obrigatória a associação de operador responsável na criação de missão

- Tipo: evolução
- Prioridade: alta
- Origem: matriz 5.3.2, RNF04
- Áreas afetadas: `Missao`, `MissaoService`, `CriarMissaoRequest`, `MissaoDTO`, `MissaoMapper`, frontend de criação de missão
- Ações:
- Incluir `operadorId` no contrato de criação
- Validar existência e vínculo do operador no backend
- Refletir a regra na interface de criação e edição de missão
- Critério de pronto:
- Não é possível criar missão sem operador responsável
- Regra fica explícita no backend, frontend e documentação

#### TSK-07. Persistir corretamente a integração Java ↔ simulador Python

- Tipo: evolução
- Prioridade: alta
- Origem: ERS RF04; matriz 5.3.1, 5.4.7 e 5.5
- Áreas afetadas: `MissaoService`, domínio `Simulacao`, cliente HTTP para Python, fluxo de missão
- Ações:
- Criar `PythonSimulatorClient`
- Registrar solicitação, status, parâmetros, identificador remoto e resultado da simulação
- Tratar indisponibilidade do simulador e retornos inválidos
- Critério de pronto:
- O backend deixa de tratar simulação apenas como passo local
- Cada simulação fica rastreável no domínio Java com vínculo à missão

### Fase 3. Escalabilidade funcional

#### TSK-08. Implementar paginação e filtros nas listagens principais

- Tipo: evolução
- Prioridade: média
- Origem: matriz 5.3.2, RNF02
- Áreas afetadas: repositories, services, controllers, frontend
- Ações:
- Introduzir paginação em missões, ameaças, astronautas, operadores e espaçonaves
- Adicionar filtros por status, criticidade, período e nome conforme domínio
- Criar DTO padrão de resposta paginada
- Critério de pronto:
- Listagens principais suportam `page`, `size` e filtros
- Frontend consegue navegar e filtrar sem carregar coleções completas

#### TSK-09. Criar dashboard consolidado de ameaças

- Tipo: evolução
- Prioridade: média
- Origem: matriz 5.3.2, RNF03
- Áreas afetadas: `AmeacaService`, `AmeacaController`, `AmeacaRepository`, frontend
- Ações:
- Criar endpoint agregado para resumo operacional de ameaças
- Expor métricas por criticidade, missão, status e tendência
- Criar DTO próprio para visualização consolidada
- Critério de pronto:
- Existe endpoint de dashboard
- Frontend exibe visão consolidada útil para monitoramento

#### TSK-10. Implementar integração com observatórios e fontes externas

- Tipo: evolução
- Prioridade: média
- Origem: ERS RF14; matriz 5.3.2, RNF05/RF03-B
- Áreas afetadas: cliente externo, serviço de importação, ameaças, logs de integração
- Ações:
- Criar `ObservatorioClient` e `ObservatorioService`
- Validar origem, formato e integridade dos dados externos
- Relacionar dados importados ao fluxo de ameaças e monitoramento
- Registrar staging ou trilha de importação
- Critério de pronto:
- Sistema consegue importar ou consultar dados externos de forma rastreável
- Integração possui tratamento de erro e validação de origem

#### TSK-11. Implementar ação autônoma em emergência por operador inativo

- Tipo: evolução
- Prioridade: média
- Origem: ERS RF13, UC-11 e UC-12; matriz 5.3.1, 5.4.5 e 5.5
- Áreas afetadas: missão, evento, protocolo, scheduler e contingência
- Ações:
- Criar serviço de contingência autônoma
- Detectar ausência de resposta humana por timeout configurável
- Executar protocolo seguro ou usar recomendação de contingência quando permitido
- Auditar integralmente motivo, contexto e ação executada
- Critério de pronto:
- Há fluxo técnico para atuação autônoma controlada
- Decisão automática deixa trilha completa para auditoria

#### TSK-12. Evoluir monitoramento biométrico para streaming ou série temporal contínua

- Tipo: evolução
- Prioridade: média
- Origem: matriz 5.3.1, RF07 e 5.5
- Áreas afetadas: `DadosBiometricos`, `Astronauta`, backend, frontend em tempo real
- Ações:
- Definir modelo de captura contínua ou quase contínua
- Expor consulta histórica e estado atual com atualização operacional
- Integrar anomalias biométricas a eventos e alertas
- Critério de pronto:
- Biometria deixa de ser apenas último valor isolado
- Monitoramento suporta histórico temporal e sinalização de anomalias

### Fase 4. Operação e resiliência

#### TSK-13. Implantar estratégia de backup e recuperação de desastres

- Tipo: evolução
- Prioridade: média
- Origem: ERS NF07; matriz 5.3.1, 5.4.6 e 5.5
- Áreas afetadas: banco, scripts operacionais, documentação
- Ações:
- Criar rotina de backup do PostgreSQL
- Definir procedimento de restauração
- Documentar RPO, RTO, periodicidade e responsáveis
- Critério de pronto:
- Existe script executável e documento operacional de recuperação
- Processo foi testado ao menos uma vez em ambiente controlado

#### TSK-14. Melhorar disponibilidade e observabilidade operacional

- Tipo: evolução
- Prioridade: média
- Origem: ERS NF05; matriz 5.3.1 e 5.4.6
- Áreas afetadas: `docker-compose.yml`, health checks, deploy
- Ações:
- Incluir health checks explícitos para serviços
- Preparar base para execução redundante ou orquestrada
- Documentar limitações atuais e evolução desejada de alta disponibilidade
- Critério de pronto:
- Serviços críticos expõem health check confiável
- Ambiente fica preparado para evolução de disponibilidade

## Itens de escopo secundário

Os requisitos `RF08`, `RF09`, `RF10`, `RF15`, `RF16` e `RF17` permanecem documentados, mas não aparecem na matriz como prioridade imediata de implementação técnica nesta etapa. Eles devem ficar em backlog de produto para decisão posterior de escopo.

## Ordem recomendada de execução

1. `TSK-01`, `TSK-02`, `TSK-03`
2. `TSK-04`, `TSK-05`, `TSK-06`, `TSK-07`
3. `TSK-08`, `TSK-09`, `TSK-10`, `TSK-11`, `TSK-12`
4. `TSK-13`, `TSK-14`

## Dependências críticas

- Segurança e versionamento de API impactam várias evoluções seguintes.
- Integração Java ↔ Python é pré-requisito para fechar RF04, UC-05 e UC-12 com aderência real.
- Operador responsável obrigatório simplifica auditoria, contingência e rastreabilidade operacional.
- Testes automatizados devem vir antes de mudanças estruturais amplas para reduzir regressão.
