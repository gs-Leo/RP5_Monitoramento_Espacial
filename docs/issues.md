# Issues Propostas

## Objetivo

Este documento traduz as propostas dos documentos de requisitos e rastreabilidade em issues rastreáveis para organização do trabalho técnico, planejamento de sprint e abertura posterior no gerenciador do projeto.

## Convenção sugerida

- `BUG`: falha concreta já identificada no repositório
- `TECHDEBT`: lacuna estrutural ou desalinhamento entre documentação e código
- `FEATURE`: evolução funcional ou arquitetural
- `DOC`: atualização documental obrigatória para manter aderência

## Issues

### ISSUE-01. BUG Corrigir testes quebrados do simulador Python

- Tipo: `BUG`
- Severidade: alta
- Relacionada a: `TSK-01`
- Contexto:
- `simulacao-python/tests/test_api.py` usa `api.api:app` e chama `/simular`, mas a API operacional está em `api.main:app` com rotas `/simulacoes/*`
- `simulacao-python/tests/test_simulacao.py` importa `RocketSimulation` de `core.main`, módulo inexistente
- Impacto:
- A suíte atual não valida o executável real
- Há falso senso de cobertura para o simulador
- Critério de aceite:
- Testes passam usando os módulos e endpoints reais
- Pelo menos uma execução de simulação é validada fim a fim no serviço Python

### ISSUE-02. TECHDEBT Ausência de testes automatizados no backend Java

- Tipo: `TECHDEBT`
- Severidade: alta
- Relacionada a: `TSK-02`
- Contexto:
- O projeto possui `spring-boot-starter-test`, mas não há base de testes em `src/test/java`
- Impacto:
- Mudanças em missão, ameaça, protocolo e tratamento de erro têm alto risco de regressão
- Critério de aceite:
- Existe suíte inicial cobrindo controllers e fluxos críticos
- O projeto pode executar testes automatizados do backend em pipeline

### ISSUE-03. DOC Diagramas e rastreabilidade não refletem fielmente o código atual

- Tipo: `DOC`
- Severidade: alta
- Relacionada a: `TSK-03`
- Contexto:
- A matriz aponta divergência entre diagramas UML, implantação documentada e estrutura real do repositório
- Há lacunas documentais para UC-08 e UC-10
- Impacto:
- Dificulta manutenção, apresentação acadêmica e validação por stakeholder
- Critério de aceite:
- Documentação passa a explicitar implementação atual, visão alvo e artefatos faltantes

### ISSUE-04. FEATURE Introduzir versionamento da API pública em `/api/v1`

- Tipo: `FEATURE`
- Severidade: alta
- Relacionada a: `TSK-04`
- Contexto:
- A matriz propõe RNF01 para padronizar evolução contratual da API
- Hoje os controllers expõem rotas sem prefixo de versão
- Impacto:
- Evoluções futuras podem quebrar consumidores internos e externos
- Critério de aceite:
- Controllers, frontend e documentação usam `/api/v1`
- Existe estratégia clara para compatibilidade ou migração

### ISSUE-05. FEATURE Implementar autenticação, autorização e auditoria

- Tipo: `FEATURE`
- Severidade: alta
- Relacionada a: `TSK-05`
- Contexto:
- A ERS exige autenticação e autorização em operações críticas
- A matriz registra ausência de Spring Security e propõe JWT/OAuth2 e perfis
- Impacto:
- O sistema não atende NF01 nem os requisitos de compartilhamento e emergência com segurança adequada
- Critério de aceite:
- Operações críticas exigem contexto autenticado
- Perfis e escopos são aplicados no backend
- Eventos sensíveis ficam auditáveis

### ISSUE-06. FEATURE Tornar operador responsável obrigatório na missão

- Tipo: `FEATURE`
- Severidade: alta
- Relacionada a: `TSK-06`
- Contexto:
- A matriz propõe RNF04
- O contrato atual de criação de missão não exige `operadorId`
- Impacto:
- Falha de rastreabilidade operacional e fragilidade para fluxos de contingência e auditoria
- Critério de aceite:
- Criação e atualização de missão validam operador responsável
- DTOs, domínio e frontend refletem a obrigatoriedade

### ISSUE-07. FEATURE Integrar backend Java ao simulador Python com persistência rastreável

- Tipo: `FEATURE`
- Severidade: alta
- Relacionada a: `TSK-07`
- Contexto:
- RF04 exige envio ao motor Python, registro de status, persistência de resultado e tratamento de falha
- A matriz recomenda criação de `PythonSimulatorClient` e vínculo explícito da `Simulacao` ao resultado remoto
- Impacto:
- O requisito de simulação fica apenas parcialmente atendido
- Critério de aceite:
- Solicitação, execução, retorno e erro do simulador ficam persistidos e vinculados à missão

### ISSUE-08. FEATURE Adicionar paginação e filtros às listagens principais

- Tipo: `FEATURE`
- Severidade: média
- Relacionada a: `TSK-08`
- Contexto:
- RNF02 foi proposto para evitar listas completas e melhorar navegação
- Hoje a API expõe listagens simples sem paginação padronizada
- Impacto:
- Escalabilidade limitada e experiência ruim com crescimento de dados
- Critério de aceite:
- Endpoints principais aceitam `page`, `size` e filtros
- Frontend consome resposta paginada

### ISSUE-09. FEATURE Criar dashboard consolidado de ameaças

- Tipo: `FEATURE`
- Severidade: média
- Relacionada a: `TSK-09`
- Contexto:
- RNF03 propõe visão agregada de ameaças para monitoramento operacional
- O controller atual de ameaças expõe apenas registro, listagem por missão e críticas
- Impacto:
- Falta visão resumida para tomada de decisão
- Critério de aceite:
- Existe endpoint agregado de dashboard
- Frontend apresenta consolidação operacional utilizável

### ISSUE-10. FEATURE Implementar integração com observatórios e fontes externas

- Tipo: `FEATURE`
- Severidade: média
- Relacionada a: `TSK-10`
- Contexto:
- RF14 e RNF05/RF03-B aparecem como evolução acordada
- A matriz aponta ausência de integração implementada
- Impacto:
- O sistema não consome dados externos previstos no domínio
- Critério de aceite:
- Existe cliente, serviço de importação e rastreio de dados externos
- Dados importados podem alimentar ameaças ou monitoramento

### ISSUE-11. FEATURE Implementar ação autônoma em emergência por timeout do operador

- Tipo: `FEATURE`
- Severidade: média
- Relacionada a: `TSK-11`
- Contexto:
- RF13, UC-11 e UC-12 exigem resposta autônoma em cenários permitidos
- A matriz identifica ausência dessa lógica no backend Java
- Impacto:
- Emergências críticas dependem exclusivamente de ação manual
- Critério de aceite:
- O sistema detecta ausência de resposta humana e executa fluxo autônomo permitido com auditoria completa

### ISSUE-12. FEATURE Evoluir monitoramento biométrico para fluxo contínuo

- Tipo: `FEATURE`
- Severidade: média
- Relacionada a: `TSK-12`
- Contexto:
- RF07 foi expandido na matriz para streaming ou série temporal contínua
- Impacto:
- O acompanhamento biométrico fica aquém do cenário de monitoramento operacional em tempo real
- Critério de aceite:
- O sistema oferece histórico temporal, leitura atual e geração de anomalias/eventos

### ISSUE-13. FEATURE Definir backup e recuperação de desastres

- Tipo: `FEATURE`
- Severidade: média
- Relacionada a: `TSK-13`
- Contexto:
- NF07 exige estratégia de backup e restauração
- A matriz recomenda script operacional e plano documental
- Impacto:
- Não há garantia operacional de recuperação após falha grave
- Critério de aceite:
- Existe rotina de backup, procedimento de restauração e documentação validada

### ISSUE-14. TECHDEBT Melhorar disponibilidade e health checks dos serviços

- Tipo: `TECHDEBT`
- Severidade: média
- Relacionada a: `TSK-14`
- Contexto:
- NF05 propõe maior resiliência, health checks e base para redundância
- O simulador já possui `/health`, mas a estratégia ainda é parcial no conjunto da solução
- Impacto:
- Falhas de componente podem ser percebidas tarde e sem coordenação entre serviços
- Critério de aceite:
- Serviços críticos possuem health checks e documentação de disponibilidade mínima

### ISSUE-15. DOC Formalizar backlog separado para requisitos fora da frente imediata

- Tipo: `DOC`
- Severidade: baixa
- Contexto:
- `RF08`, `RF09`, `RF10`, `RF15`, `RF16` e `RF17` estão documentados, mas não aparecem como implementação prioritária nesta etapa
- Impacto:
- Sem classificação explícita, pode haver ruído entre escopo acadêmico, técnico e de produto
- Critério de aceite:
- O projeto possui registro claro de quais requisitos ficam em backlog posterior

## Sequência recomendada para abertura

1. `ISSUE-01`, `ISSUE-02`, `ISSUE-03`
2. `ISSUE-04`, `ISSUE-05`, `ISSUE-06`, `ISSUE-07`
3. `ISSUE-08`, `ISSUE-09`, `ISSUE-10`, `ISSUE-11`, `ISSUE-12`
4. `ISSUE-13`, `ISSUE-14`, `ISSUE-15`

## Observação final

Se estas issues forem migradas para GitHub, Jira ou Azure Boards, vale manter os IDs deste documento como referência inicial e depois complementar com labels por domínio:

- `backend`
- `frontend`
- `python-simulator`
- `docs`
- `security`
- `infra`
