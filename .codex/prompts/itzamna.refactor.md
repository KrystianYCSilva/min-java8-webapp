---
description: "Refatoracao segura de codigo com analise de code smells, selecao de tecnicas e preservacao de comportamento. Use quando: o codigo funciona mas esta sujo, complexo, duplicado ou dificil de estender."
---

# /itzamna.refactor

Melhorar a estrutura interna do codigo sem alterar seu comportamento externo.

**Input:** `$ARGUMENTS` (codigo ou arquivo para refatorar)

---

## Instructions

### 1. Analise de Code Smells

Identifique problemas como:
- Metodos longos / Classes grandes (God Class).
- Duplicacao de codigo.
- Nomes de variaveis obscuros.
- Complexidade condicional excessiva (Nested IFs).
- Acoplamento forte.

### 2. Selecao de Tecnicas de Refatoracao

Escolha as tecnicas apropriadas (ex: Extract Method, Rename Variable, Replace Conditional with Polymorphism, Introduce Parameter Object).

### 3. Preservacao de Comportamento

Garanta que a logica de negocio permaneca intacta. Se possivel, mencione como os testes existentes garantiriam isso.

### 4. Aplicacao

Reescreva o codigo aplicando as melhorias.

---

## Constraints

- SEMPRE manter a assinatura publica (se possivel) para nao quebrar clientes.
- SEMPRE priorizar legibilidade sobre micro-otimizacoes.
- SEMPRE explicar *por que* a mudanca foi feita.
- NAO alterar a logica de negocio (isso e feature, nao refactor).
- NAO introduzir novas dependencias pesadas sem necessidade.

---

## Output Format

```markdown
# Relatorio de Refatoracao

## Code Smells Identificados
- [Lista de problemas encontrados]

## Alteracoes Aplicadas
1. **[Tecnica Usada]:** [Onde e por que]
   - *Ex: Extract Method nas linhas 10-25 para melhorar legibilidade.*

## Codigo Refatorado
```[lang]
...
```

## Analise de Impacto
- Complexidade Ciclomatica: [Reduzida/Mantida]
- Legibilidade: [Melhorada]
```
