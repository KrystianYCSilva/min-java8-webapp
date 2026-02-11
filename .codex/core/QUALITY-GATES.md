---
description: |
  Quality criteria for evaluating work before human approval: completeness, correctness, clarity, best practices.
  Use when: finishing a task and preparing to present work for approval.
---

# Quality Gates

> Criterios de qualidade. Aplique antes de entregar trabalho para aprovacao.

---

## Criterios

| Dimensao | OK | ALERTA | BLOQUEIO |
|----------|-----|--------|----------|
| **Completude** | Todos os requisitos atendidos | Secundarios pendentes | Principais faltando |
| **Corretude** | Funciona, testes passam | Edge cases nao cobertos | Erros, testes falhando |
| **Clareza** | Codigo limpo, nomes claros | Entendivel mas pode melhorar | Incompreensivel |
| **Boas praticas** | Segue T0, T1, T2 | Viola T2 com justificativa | Viola T0 ou T1 |

---

## Decisao

| Resultado | Acao |
|-----------|------|
| Tudo OK | Apresente para aprovacao |
| Qualquer ALERTA | Apresente com ressalvas |
| Qualquer BLOQUEIO | Corrija antes de apresentar |

---

## Apresentacao

```
## Resumo
O que foi feito e por que.

## Mudancas
Arquivos criados/modificados.

## Qualidade
- Completude: OK/ALERTA/BLOQUEIO
- Corretude: OK/ALERTA/BLOQUEIO
- Clareza: OK/ALERTA/BLOQUEIO
- Boas praticas: OK/ALERTA/BLOQUEIO
```

---

*Quality Gates v3.0*
