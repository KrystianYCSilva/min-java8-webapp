---
description: Visao geral do projeto Censo Superior 2025 com escopo funcional atual, fluxo de migracao e restricoes operacionais.
tier: T2
triggers:
  - overview
  - escopo
  - objetivo
  - fluxo
  - modulo
  - migracao
  - roadmap
  - java8
  - springboot
  - mvvm
  - zk
  - jpa
last_updated: 2026-02-11
---
# Visao Geral do Projeto

## Objetivo

Aplicacao web para cadastro, consulta, importacao e exportacao de dados do Censo Superior 2025.

Situacao atual: baseline legado em Java 6 + ZK 3.6.2 (MVC) + Spring tradicional.

Objetivo de evolucao: migrar gradualmente para Java 8 + ZK 8.6.0.1 + Spring Boot + MVVM.

## Escopo funcional atual

1. Autenticacao por login/senha com sessao HTTP.
2. Modulo `Aluno` (Registro 41): CRUD, listagem paginada, importacao/exportacao TXT pipe.
3. Modulo `Curso` (Registro 21): CRUD, listagem paginada, importacao/exportacao TXT pipe.
4. Modulo `CursoAluno` (Registro 42): vinculo de aluno em curso e dados complementares.
5. Modulo `Docente` (Registro 31): CRUD, importacao/exportacao TXT pipe.
6. Modulo `IES` (Registro 11): CRUD, importacao/exportacao TXT pipe.
7. Exportacao de registros individuais e em lote.

## Programa de migracao 2026

1. Fase 1 (`main`): Java 8 com comportamento preservado (ainda MVC).
2. Fase 2 (`feature/zk8-bootstrap-ui`): ZK 8.6.0.1 + Bootstrap + melhoria de frontend (ainda MVC).
3. Fase 3 (`feature/springboot-modernization`): Spring Boot + Spring Data + Spring Security + Spring MVC, com remocao gradual dos DAOs legados.
4. Fase 4 (`feature/zk-mvvm-final`): migracao final de MVC para MVVM.

## Restricoes arquiteturais

1. Camadas devem manter isolamento durante toda a migracao.
2. Mudanca de framework nao deve alterar regra de negocio sem necessidade.
3. Banco principal de desenvolvimento/teste permanece H2 ate decisao posterior.
4. Mudancas de layout/importacao devem continuar compativeis com os registros oficiais.

## Fluxos criticos

1. Login em `LoginComposer` e autorizacao via `AuthFilter`.
2. Navegacao no shell `menu.zul` com `view` (conteudo principal) e `sub` (modal).
3. Persistencia de entidades principais (`aluno`, `curso`, `curso_aluno`, `docente`, `ies`).
4. Validacao de consistencia UF/codigo de municipio via tabela de apoio `municipio`.
5. Mapeamento de campos complementares de layout (`layout_campo` e tabelas `_layout_valor`).
6. Importacao e exportacao em formato TXT separado por `|`.

## Artefatos centrais

1. Backend: `src/main/java/br/gov/inep/censo`.
2. Frontend ZK: `src/main/webapp/*.zul` e `src/main/webapp/app/*.zul`.
3. Shell e estilo: `src/main/webapp/app/menu.zul` e `src/main/webapp/css/app-shell.css`.
4. Esquema e seeds: `src/main/resources/db`.
5. Testes: `src/test/java`.
6. Arquitetura: `docs/ARCHITECTURE.md`.
7. Roadmap de migracao: `docs/MIGRATION-ROADMAP.md`.
