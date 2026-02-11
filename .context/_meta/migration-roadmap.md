---
description: Plano de migracao incremental para Java 8, ZK 8, Spring Boot e MVVM, com estrategia por branch e criterios de aceite.
tier: T2
triggers:
  - migracao
  - roadmap
  - java8
  - zk8
  - springboot
  - spring data
  - spring security
  - mvvm
last_updated: 2026-02-11
---
# Roadmap de Migracao

## Objetivo

Modernizar o projeto sem Big Bang, mantendo entregas demonstraveis para o time em cada etapa.

## Ordem das fases

| Fase | Branch | Foco |
| --- | --- | --- |
| 1 | `main` | Java 8 com comportamento atual preservado |
| 2 | `feature/zk8-bootstrap-ui` | ZK 8.6.0.1 + Bootstrap + melhoria de frontend (MVC) |
| 3 | `feature/springboot-modernization` | Spring Boot + Spring Data + Spring Security + Spring MVC |
| 4 | `feature/zk-mvvm-final` | Migracao final de MVC para MVVM |

## Regras de execucao

1. Uma mudanca estrutural principal por fase.
2. Nao misturar mudanca funcional de negocio com mudanca de framework.
3. Proteger fluxos criticos com testes antes da refatoracao.
4. So remover codigo legado depois de validar o caminho novo (parallel change).

## Entregaveis por fase

1. Fase 1: build em Java 8, testes e fluxos basicos sem regressao.
2. Fase 2: UI atualizada em ZK 8 + Bootstrap, mantendo comportamento.
3. Fase 3: infraestrutura Spring Boot e repositorios Spring Data ativos, com seguranca equivalente.
4. Fase 4: telas e fluxo principais no padrao MVVM.

## Referencias

1. `docs/MIGRATION-ROADMAP.md`
2. `docs/MIGRATION-CHANGELOG.md`
