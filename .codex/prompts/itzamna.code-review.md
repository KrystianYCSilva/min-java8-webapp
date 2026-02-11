---
description: "Revisao estruturada de codigo com analise de seguranca, bugs, design e performance. Use quando: revisar PRs, snippets ou refatoracoes antes do merge."
---

# /itzamna.code-review

Revisao de codigo rigorosa e construtiva.

**Input:** `$ARGUMENTS` (codigo ou arquivo para revisar)

---

## Instructions

Siga este processo de pensamento (Chain of Thought) antes de gerar a resposta:

### 1. Analise de Seguranca (Prioridade 0)

- Procure por injecao (SQL, NoSQL, Command).
- Verifique exposicao de dados sensiveis.
- Valide tratamento de inputs externos.

### 2. Analise Logica e Bugs

- Rastreie o fluxo de dados. Existem "happy paths" e "edge cases"?
- Verifique condicoes de erro e excecoes (swallowed exceptions?).
- Identifique possiveis NullPointers ou undefined.

### 3. Design e Qualidade

- O codigo segue SRP (Single Responsibility Principle)?
- Ha duplicidade (DRY)?
- Nomes de variaveis e funcoes revelam a intencao?
- A complexidade ciclomatica parece alta?

### 4. Performance

- Existem operacoes O(n^2) ou pior em grandes datasets?
- Uso de memoria e eficiente?
- Recursos (streams, conexoes) sao fechados corretamente?

---

## Constraints

- SEMPRE classificar issues por severidade (Critico, Alto, Medio, Baixo).
- SEMPRE fornecer exemplos de codigo corrigido para problemas criticos/altos.
- SEMPRE citar o principio violado (ex: "Violacao de OCP").
- NAO comente sobre formatacao (espacos, virgulas) a menos que afete a logica (assuma que um linter existe).
- NAO seja vago. Em vez de "melhore a performance", diga "Use um HashMap para lookup O(1)".

---

## Output Format

Gere a resposta seguindo estritamente este template Markdown:

```markdown
# Code Review Report

## Critico / Bloqueante
*(Issues que causam bugs, falhas de seguranca ou quebra de arquitetura)*
- **[Linha X] Titulo do Problema**
  - **Motivo:** Explicacao clara do risco.
  - **Correcao Sugerida:**
    ```[lang]
    // Codigo corrigido
    ```

## Importante / Debt
*(Issues de design, performance ou manutenibilidade que devem ser corrigidos)*
- **[Linha X] Titulo do Problema**
  - **Analise:** Por que isso e um problema a longo prazo.
  - **Sugestao:** Abordagem recomendada.

## Sugestoes / Nitpicks
*(Melhorias de legibilidade ou estilo que nao bloqueiam)*
- [Linha X] Sugestao breve.

## Pontos Positivos
*(O que foi bem feito)*
- [Item]

## Resumo
**Status:** [APROVADO / APROVADO COM RESSALVAS / REJEITAR]
**Qualidade Geral:** [Nota 1-10]
```
