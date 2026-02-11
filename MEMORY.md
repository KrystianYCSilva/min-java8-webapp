---
description: |
  Project episodic memory: current state, decisions, next steps, session history.
  Use when: starting any K2+ task (MANDATORY read), ending significant sessions (MANDATORY update).
---

# Memory - Estado do Projeto

> Lido pelo agente no inicio de sessoes K2+.
> Atualizado ao final de sessoes significativas.
> Append-only: nunca apague entradas.

---

## Projeto

- **Nome:** Censo Superior 2025 - WebApp
- **Stack:** Java 1.6→1.8, ZK 3.6.2, Hibernate 4.2, H2, Maven
- **Repositorio:** min-java8-webapp

---

## Estado atual

Protótipo funcional de sistema de Censo Superior do INEP em fase 1/4 de migração incremental (Java 6 → Java 8 → ZK 8 → Spring Boot → MVVM). Sistema implementa CRUD completo de IES, Cursos, Alunos, Docentes com autenticação básica, testes automatizados (80% cobertura) e estrutura `.context/` Enterprise configurada.

---

## Decisoes

| Data | Decisao | Justificativa |
|------|---------|---------------|
| 2026-02-11 | Migração Lean → Enterprise na estrutura `.context/` | Melhor organização com tiers (T0/T1/T2), triggers JIT loading e separação clara entre meta, standards, patterns, knowledge e workflows |
| 2026-02-11 | Backup de arquivos Lean em `.context/.backup-lean/` | Preservar histórico antes de remover duplicação com estrutura Enterprise |
| 2026-02-11 | Estrutura de 4 fases de migração (Java8→ZK8→SpringBoot→MVVM) | Reduzir risco com validação incremental e demonstração de valor por etapa |

---

## Proximos passos

- [ ] Verificar compatibilidade Java 8 no branch main
- [ ] Validar cobertura de testes ≥80% em dao/service/util
- [ ] Preparar branch feature/zk8-bootstrap-ui (fase 2)
- [ ] Documentar resultados da fase 1 em docs/MIGRATION-CHANGELOG.md

---

## Sessoes

| # | Data | Nivel | Resumo |
|---|------|-------|--------|
| 1 | 2026-02-11 | K2 | Configuração inicial da estrutura `.context/`: atualização de project.md, tech.md, rules.md com detecção automática do projeto. Migração Lean→Enterprise concluída com backup dos arquivos legados em `.backup-lean/`. Estrutura Enterprise com 19 ADRs, triggers JIT, tiers T0/T1/T2 funcionando. |

---

*Ultima atualizacao: 2026-02-11.*
