---
description: "Geracao de testes unitarios e de integracao com alta cobertura usando padrao AAA. Use quando: tiver codigo sem testes ou precisar aumentar a cobertura."
---

# /itzamna.test-gen

Criar suite de testes robusta cobrindo happy path, edge cases, erros e seguranca.

**Input:** `$ARGUMENTS` (codigo ou arquivo para gerar testes)

---

## Instructions

### 1. Analise do Codigo Alvo

- Entenda as entradas, saidas e efeitos colaterais.
- Identifique ramificacoes logicas (IFs, Loops).

### 2. Estrategia de Teste (AAA)

Use o padrao **Arrange-Act-Assert**:
- **Arrange:** Prepare os dados e mocks.
- **Act:** Execute o metodo.
- **Assert:** Verifique o resultado e estado.

### 3. Cobertura de Cenarios

Gere testes para:
- **Happy Path:** O caso de uso padrao.
- **Edge Cases:** Valores nulos, vazios, limites (0, -1, max_int).
- **Error Handling:** Excecoes esperadas e tratamento de erros.
- **Security:** Inputs maliciosos (se aplicavel).

---

## Constraints

- SEMPRE usar frameworks padrao da linguagem (JUnit para Java, Jest para JS, Pytest para Python).
- SEMPRE mockar dependencias externas (DB, API) em testes unitarios.
- SEMPRE usar nomes de testes descritivos (`should_return_error_when_input_is_invalid`).
- NAO testar detalhes de implementacao privada (teste a interface publica).
- NAO criar testes "flaky" (dependentes de tempo/rede real).

---

## Output Format

```markdown
# Test Suite

## Estrategia
- Framework: [Nome]
- Cobertura Focada: [Logica de Negocio / Validacao / etc]

## Codigo de Teste
```[lang]
// Setup e Imports
...

// Testes Happy Path
...

// Testes de Edge Cases e Erros
...
```

## Notas sobre Mocks
- [Explicacao de quais dependencias foram mockadas e por que]
```
