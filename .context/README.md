---
description: Mapa principal de contexto do projeto. Use para decidir quais arquivos carregar por intencao e reduzir tokens com JIT loading.
tier: T2
triggers:
  - contexto
  - roteamento
  - jit
  - load
  - arquitetura
  - testes
  - hibernate
  - migracao
  - roadmap
  - java8
  - springboot
  - mvvm
  - docente
  - ies
  - municipio
last_updated: 2026-02-11
---
# Contexto do Projeto

Este diretorio segue estrutura em tiers para separar regras absolutas, padroes normativos e dados informativos.

## Carga estatica (prefixo cacheavel)

Carregar sempre:

1. `./ai-assistant-guide.md`
2. `./standards/architectural-rules.md`
3. `./standards/code-quality.md`

## Carga dinamica (JIT por gatilho)

| Gatilho no pedido | Arquivos recomendados |
| --- | --- |
| arquitetura, camadas, fluxo servlet | `./patterns/architecture.md`, `./_meta/project-overview.md` |
| java, maven, dependencias, versoes, hibernate | `./_meta/tech-stack.md` |
| migracao, roadmap, java8, zk8, springboot, mvvm | `./_meta/migration-roadmap.md`, `./_meta/key-decisions.md` |
| testes, cobertura, jacoco, e2e | `./standards/testing-strategy.md` |
| aluno, curso, curso-aluno, registro 21/41/42, layout | `./knowledge/domain-concepts.md` |
| docente, ies, registro 31/11, municipio, pais, uf | `./knowledge/domain-concepts.md`, `./_meta/project-overview.md` |
| decisao tecnica, tradeoff, historico | `./_meta/key-decisions.md` |
| deploy, tomcat, build, execucao | `./workflows/deployment.md` |
| agentes, handoff, memoria, token | `./workflows/context-loading.md` |

## Estrutura

```text
.context/
├── README.md
├── ai-assistant-guide.md
├── _meta/
│   ├── project-overview.md
│   ├── tech-stack.md
│   ├── key-decisions.md
│   └── migration-roadmap.md
├── standards/
│   ├── architectural-rules.md
│   ├── code-quality.md
│   └── testing-strategy.md
├── patterns/
│   └── architecture.md
├── knowledge/
│   └── domain-concepts.md
└── workflows/
    ├── deployment.md
    └── context-loading.md
```
