---
description: |
  Project overview: goals, scope, and current state.
  Use when: you need to understand what this project is and why it exists.
---

# Project Overview

## What

Sistema web para gerenciamento do Censo da Educação Superior do INEP, permitindo cadastro e visualização de IES (Instituições de Ensino Superior), cursos, alunos, docentes e vínculos curso-aluno.

## Why

Este projeto existe como protótipo funcional para demonstrar ao time os benefícios de migração incremental de stack legado (Java 6, ZK 3.6, JPA puro) para stack moderna (Java 8+, Spring Boot, ZK 8+, MVVM). O objetivo é reduzir risco de migração através de fases bem definidas e validação contínua.

## Scope

**Dentro do escopo:**
- CRUD completo de entidades principais (Aluno, Curso, CursoAluno, Docente, IES)
- Autenticação básica com filtro customizado
- Interface ZK com padrão MVC Composer
- Persistência JPA com Hibernate ORM
- Suporte a campos dinâmicos via layout configurável
- Testes unitários com JUnit 4, Mockito e DBUnit
- Testes E2E com Selenium WebDriver
- Cobertura de código com JaCoCo (mínimo 80%)

**Fora do escopo:**
- Integração com sistemas externos do INEP
- Importação de dados reais do censo
- Deploy em produção
- Múltiplos perfis de usuário
- Funcionalidades de relatório avançadas

## Status

**Atual:** Protótipo funcional em migração (fase 1/4)

**Roadmap de migração 2026:**

1. **Fase 1 (main):** Migração para Java 8 mantendo comportamento atual ✅ *← você está aqui*
2. **Fase 2 (feature/zk8-bootstrap-ui):** Upgrade ZK 8.6.0.1 + Bootstrap UI
3. **Fase 3 (feature/springboot-modernization):** Spring Boot + Spring Data + Spring Security
4. **Fase 4 (feature/zk-mvvm-final):** Migração MVC → MVVM

**Artefatos de documentação:**
- `docs/MIGRATION-ROADMAP.md` - Planejamento completo das fases
- `docs/MIGRATION-CHANGELOG.md` - Histórico de mudanças
- `docs/ARCHITECTURE.md` - Arquitetura atual e futura
- `docs/TEST-PLAN.md` - Estratégia de testes
