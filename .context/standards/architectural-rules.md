---
description: Regras arquiteturais obrigatorias do projeto (T0/T1), incluindo dependencias entre camadas e politicas de mudanca estrutural.
tier: T1
triggers:
  - regra
  - arquitetura
  - camadas
  - dependencia
  - sql
  - jpa
  - zk
  - migracao
  - springboot
  - mvvm
last_updated: 2026-02-11
---
# Regras Arquiteturais

## Regras obrigatorias (MUST)

1. `web/zk` deve apenas orquestrar interacao de tela e delegar para `service`.
2. Regra de negocio deve ficar em `service`.
3. Persistencia deve ficar em componentes dedicados de infraestrutura (`dao` legado ou `repository` Spring Data, conforme fase).
4. SQL nativo em `dao` deve usar bind seguro de parametros.
5. Classes `model` nao devem depender de `web` nem de `dao`.
6. Novos fluxos autenticados devem passar por `AuthFilter`.
7. Mudancas de schema devem manter compatibilidade com seeds e testes.
8. Durante a Fase 1 e Fase 2, DAOs devem usar `AbstractJpaDao` (sem `DriverManager` direto).
9. Durante a Fase 3+, novos acessos de persistencia devem priorizar `repository` Spring Data.

## Direcao de dependencias permitida

1. `web/zk -> service`
2. `service -> dao | model | util`
3. `service -> repository | model | util` (Fase 3+)
4. `dao -> model | config | domain`
5. `util` deve ser reutilizavel e sem dependencia de camada web

## Regras de mudanca de banco

1. Alterou tabela? atualizar `schema.sql`.
2. Alterou dominio base? atualizar `seed.sql`.
3. Alterou metadados de layout de aluno/curso/curso-aluno? atualizar `seed_layout.sql`.
4. Alterou metadados de layout de docente/ies? atualizar `seed_layout_ies_docente.sql`.
5. Alterou referencia de municipio/UF? atualizar `seed_municipio.sql`.
6. Alterou persistencia? atualizar testes de `dao` e `service`.

## Regras de compatibilidade por fase

1. Baseline atual pre-Fase 1: Java 6.
2. A partir da Fase 1 (`main`): Java 8 e proibido usar recursos acima de Java 8.
3. Fase 2: manter MVC durante upgrade ZK/UI.
4. Fase 3: permitir coexistencia controlada de DAO e Repository durante transicao.
5. Fase 4: novo desenvolvimento de tela deve seguir MVVM.
6. `web.xml` legado permanece obrigatorio ate a conclusao da Fase 3.
