---
description: |
  Project-specific coding rules and standards beyond CONSTITUTION.md.
  Use when: writing or reviewing code for this specific project.
---

# Project Rules & Standards

> Regras específicas DESTE projeto. Regras gerais estão em CONSTITUTION.md.

## Regras do projeto

### Migração incremental
- **NUNCA** migre múltiplas fases ao mesmo tempo
- **SEMPRE** mantenha compatibilidade com Java 6/7 até fase 1 completa
- **SEMPRE** execute testes completos antes de avançar de fase
- **NUNCA** quebre funcionalidade existente durante migração

### Persistência JPA
- **SEMPRE** use `AbstractJpaDao` como base para novos DAOs
- **SEMPRE** gerencie transações explicitamente (`EntityTransaction`)
- **NUNCA** use HQL/JPQL sem parâmetros (risco de SQL injection)
- **SEMPRE** feche `EntityManager` em finally block

### ZK Framework (MVC)
- **SEMPRE** use Composers para lógica de apresentação
- **NUNCA** coloque lógica de negócio nos Composers (use Service Layer)
- **SEMPRE** trate exceções e mostre mensagens amigáveis ao usuário
- **SEMPRE** use sub-windows modais para formulários de cadastro/edição

### Testes
- **SEMPRE** mantenha cobertura ≥ 80% em dao/service/util
- **SEMPRE** use DBUnit para testes de DAO (datasets XML)
- **SEMPRE** use Mockito para isolar testes de Service
- **NUNCA** faça commit de código com testes falhando

### Segurança
- **NUNCA** armazene senhas em plain text (use `PasswordUtil`)
- **SEMPRE** valide entrada de usuário (use `ValidationUtils`)
- **SEMPRE** use CSRF token em formulários (`CsrfFilter`)
- **NUNCA** exponha stack traces ao usuário final

## Padrões de código

### DAO Pattern
```java
public class ExemploDAO extends AbstractJpaDao<Exemplo> {
    public Exemplo buscarPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Exemplo.class, id);
        } finally {
            em.close();
        }
    }
}
```

### Service Layer
```java
public class ExemploService {
    private ExemploDAO dao = new ExemploDAO();

    public void salvar(Exemplo exemplo) {
        // 1. Validação
        ValidationUtils.validarNaoNulo(exemplo, "Exemplo");

        // 2. Lógica de negócio
        exemplo.processarRegras();

        // 3. Persistência
        dao.salvar(exemplo);
    }
}
```

### Builder Pattern (entidades complexas)
```java
public class Exemplo {
    public static class Builder {
        private String campo1;
        private Integer campo2;

        public Builder campo1(String valor) {
            this.campo1 = valor;
            return this;
        }

        public Exemplo build() {
            return new Exemplo(campo1, campo2);
        }
    }
}
```

## Decisões arquiteturais (ADRs)

| # | Data | Decisão | Status |
|---|------|---------|--------|
| 1 | 2026-01 | Migração incremental em 4 fases (Java8 → ZK8 → SpringBoot → MVVM) para reduzir risco | Aceita |
| 2 | 2026-01 | Uso de H2 embarcado para simplificar setup de desenvolvimento e testes | Aceita |
| 3 | 2026-01 | Manter JPA manual (sem Spring Data) até fase 3 para preservar controle transacional | Aceita |
| 4 | 2026-01 | Sub-windows modais ZK para formulários (melhor UX que páginas separadas) | Aceita |
| 5 | 2026-01 | Cobertura de testes mínima 80% em camadas dao/service/util (qualidade garantida) | Aceita |
| 6 | 2026-01 | Builder Pattern para entidades com 5+ campos obrigatórios (legibilidade) | Aceita |
| 7 | 2026-01 | AuthFilter customizado (sem Spring Security) até fase 3 para manter simplicidade | Aceita |
| 8 | 2026-01 | Navegação por querystring (?view=, ?sub=) centralizada no MenuComposer | Aceita |

## Convenções de commit

Seguir [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` - Nova funcionalidade
- `fix:` - Correção de bug
- `refactor:` - Refatoração sem mudança de comportamento
- `test:` - Adição ou correção de testes
- `docs:` - Atualização de documentação
- `chore:` - Mudanças de build, dependências, etc.

**Exemplos:**
```
feat: adicionar cadastro de laboratorios
fix: corrigir validacao de CPF em AlunoService
refactor: extrair metodo de validacao para ValidationUtils
test: adicionar testes para DocenteDAO
docs: atualizar MIGRATION-ROADMAP.md com fase 2
chore: atualizar hibernate para 4.2.21
```

## Referências

- Architecture: `docs/ARCHITECTURE.md`
- Testing: `docs/TEST-PLAN.md`
- Migration: `docs/MIGRATION-ROADMAP.md`
- Constitution: `CONSTITUTION.md` (raiz do projeto)
