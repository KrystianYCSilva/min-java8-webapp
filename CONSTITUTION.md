---
description: |
  Project constitution: T0 (inviolable), T1 (strong), T2 (convention) rules.
  Use when: making decisions, writing code, reviewing code, or when uncertain about what is allowed.
---

# Constitution - Regras do Projeto

> Hierarquia de regras que todo agente DEVE seguir.

---

## Hierarquia

| Tier | Nivel | Violacao |
|------|-------|----------|
| **T0** | Inviolavel | Pare imediatamente. Avise o usuario. |
| **T1** | Forte | Quebre raramente, com justificativa explicita. |
| **T2** | Convencao | Flexivel. Siga o padrao do projeto. |

---

## T0 - Inviolavel

### Seguranca

| ID | Regra |
|----|-------|
| T0-SEC-01 | Nunca inclua secrets hardcoded. Use .env ou secrets manager. |
| T0-SEC-02 | Nunca use SQL injection patterns. Use queries parametrizadas. |
| T0-SEC-03 | Nunca exponha dados sensiveis em logs. |
| T0-SEC-04 | Nunca desabilite validacoes de seguranca. |

### Controle humano

| ID | Regra |
|----|-------|
| T0-HUM-01 | Mudancas significativas requerem aprovacao. |
| T0-HUM-02 | Nunca faca commit automatico. |
| T0-HUM-03 | Nunca faca push automatico. |
| T0-HUM-04 | Nunca delete arquivos sem confirmar. |

### Validacao

| ID | Regra |
|----|-------|
| T0-VAL-01 | Nunca afirme sucesso sem verificar. |
| T0-VAL-02 | Nunca invente APIs ou metodos. |
| T0-VAL-03 | Nunca ignore erros de compilacao. |

---

## T1 - Forte

| ID | Regra | Quando quebrar |
|----|-------|----------------|
| T1-QUAL-01 | Siga SOLID | Prototipo, POC |
| T1-QUAL-02 | Escreva testes para codigo novo | Codigo trivial |
| T1-QUAL-03 | DRY | Duplicacao mais clara que abstracao |
| T1-ARCH-01 | Separacao de camadas | Scripts simples |
| T1-ARCH-02 | Tratamento de erros explicito | Nunca |
| T1-DOC-01 | Documente decisoes importantes | Decisao obvia |

---

## T2 - Convencao

| Elemento | Convencao | Exemplo |
|----------|-----------|---------|
| Arquivos | kebab-case | `user-service.ts` |
| Classes | PascalCase | `UserService` |
| Funcoes | camelCase | `getUserById()` |
| Commits | Conventional Commits | `feat: add auth` |

**Regra geral:** siga o padrao ja existente no projeto.

---

## Como aplicar

**Ao escrever:** T0 violado? Pare. T1 violado? Justifique. T2 violado? Siga o projeto.
**Ao revisar:** T0 = blocker. T1 = warning. T2 = sugestao.

---

*Constitution v3.0*
