---
description: Protocolo de execucao para agentes neste repositorio. Define precedencia, regras obrigatorias e estrategia de carregamento de contexto.
tier: T0
triggers:
  - protocolo
  - regras
  - prioridades
  - assistente
  - agente
  - migracao
  - roadmap
last_updated: 2026-02-11
---
# Guia do Assistente AI

## Ordem de precedencia

1. Instrucoes de sistema e da plataforma de execucao.
2. `AGENTS.md` na raiz do workspace.
3. Este arquivo (`T0` local).
4. Arquivos em `./standards/` (`T1` normativo).
5. Arquivos em `./_meta/`, `./patterns/`, `./knowledge/`, `./workflows/` (`T2/T3` informativo).

## Regras obrigatorias

1. Preservar dependencia por camadas: `web -> service -> dao -> db`.
2. Nao colocar SQL fora de `src/main/java/.../dao`.
3. Nao colocar regra de negocio em controllers de tela (`Composer`/camada `web`).
4. Respeitar regra de compatibilidade por fase: baseline legado Java 6; apos Fase 1 usar Java 8.
5. Nao alterar artefatos gerados como entrega funcional (`target/`, `.m2/`).
6. Ao alterar layout/importacao/persistencia, sincronizar banco, service e testes.
7. Carregar contexto por JIT: comece no minimo necessario e expanda por gatilho.
8. Registrar decisoes persistentes novas em `./_meta/key-decisions.md`.

## Fluxo de execucao recomendado

1. Classificar o tipo de pedido (arquitetura, bug, testes, deploy, dominio).
2. Carregar contexto estatico do `README` desta pasta.
3. Carregar contexto dinamico por gatilho (somente arquivos relevantes).
4. Executar mudanca com validacao tecnica.
5. Atualizar contexto quando houver nova regra, decisao ou workflow.

## Perfil de contexto por papel

| Papel | Contexto minimo |
| --- | --- |
| Dev | `standards/code-quality.md`, `patterns/architecture.md`, `_meta/tech-stack.md` |
| QA | `standards/testing-strategy.md`, `_meta/tech-stack.md`, `knowledge/domain-concepts.md` |
| Planejamento | `_meta/project-overview.md`, `_meta/key-decisions.md`, `knowledge/domain-concepts.md` |
