---
description: Estrategia de carregamento de contexto com JIT, compressao e handoff entre agentes para reduzir token cost e ruido.
tier: T2
triggers:
  - jit
  - token
  - handoff
  - memoria
  - multi-agent
  - migracao
  - roadmap
last_updated: 2026-02-11
---
# Workflow de Carregamento de Contexto

## Objetivo

Carregar apenas contexto necessario para cada tarefa, reduzindo latencia, custo de token e risco de confusao.

## Sequencia recomendada

1. Ler `../README.md` para roteamento inicial.
2. Carregar contexto estatico: `../ai-assistant-guide.md` + regras em `../standards/`.
3. Identificar gatilhos do pedido (ex.: "teste", "deploy", "registro 42").
4. Carregar somente os arquivos dinamicos correspondentes.
5. Executar tarefa e produzir resumo curto para memoria de sessao.

## Heuristicas de JIT

1. Pedido de bug em SQL: carregar `standards/architectural-rules.md` + `patterns/architecture.md` + `knowledge/domain-concepts.md`.
2. Pedido de cobertura/teste: carregar `standards/testing-strategy.md`.
3. Pedido de build/deploy: carregar `_meta/tech-stack.md` + `workflows/deployment.md`.
4. Pedido de desenho/planejamento: carregar `_meta/project-overview.md` + `_meta/key-decisions.md`.
5. Pedido envolvendo registros 11/31 ou enums novos: carregar `knowledge/domain-concepts.md` + `docs/ARCHITECTURE.md`.
6. Pedido envolvendo municipios/UF: carregar `knowledge/domain-concepts.md` + `src/main/resources/db/seed_municipio.sql`.
7. Pedido envolvendo Hibernate/persistencia: carregar `_meta/tech-stack.md` + `patterns/architecture.md` + `_meta/key-decisions.md`.
8. Pedido de migracao (java8, zk8, springboot, mvvm): carregar `_meta/migration-roadmap.md` + `_meta/tech-stack.md` + `_meta/key-decisions.md`.

## Formato de handoff entre agentes

Usar resumo comprimido, nao historico bruto:

```yaml
task: "Ajustar validacao de Registro 42"
done:
  - "Regra implementada em service"
  - "Teste unitario adicionado"
pending:
  - "Executar suite completa"
files_touched:
  - "src/main/java/.../CursoAlunoService.java"
  - "src/test/java/.../CursoAlunoServiceTest.java"
risks:
  - "Possivel impacto em importacao TXT"
```
