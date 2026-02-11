---
description: "Implementacao de novas features seguindo TDD e padroes arquiteturais com decomposicao, design e testes. Use quando: tiver um requisito, user story ou card para implementar."
---

# /itzamna.implement

Gerar codigo de producao para nova funcionalidade, garantindo testabilidade, manutenibilidade e aderencia a SOLID/DRY.

**Input:** `$ARGUMENTS` (requisito, user story ou descricao da feature)

---

## Instructions

### 1. Decomposicao de Requisitos

- Analise o pedido. Identifique entradas, saidas e regras de negocio.
- Liste os casos de uso: Caminho Feliz e Caminhos de Erro (Edge cases).

### 2. Design Arquitetural (Mini-Design)

- Quais classes/funcoes sao necessarias?
- Como elas interagem? (Interfaces, DTOs, Entidades).
- Onde isso se encaixa na arquitetura existente (Controller, Service, Repository)?

### 3. Estrategia de Testes (TDD Mental)

- Pense nos testes *antes* de escrever o codigo.
- Quais assercoes provam que a feature funciona?

### 4. Implementacao

- Gere o codigo seguindo Clean Code.
- Use tipagem forte (se a linguagem permitir).
- Trate erros explicitamente.

---

## Constraints

- SEMPRE incluir validacao de input.
- SEMPRE separar responsabilidades (Logica de negocio fora do Controller/View).
- SEMPRE gerar codigo de teste correspondente (Unitario ou Integracao).
- NAO deixar TODOs criticos no codigo gerado.
- NAO usar "magic numbers" ou strings hardcoded (use constantes).

---

## Output Format

```markdown
# Plano de Implementacao

## 1. Design
Breve explicacao da abordagem (ex: "Criaremos um Service X que usa o Repository Y").

## 2. Codigo da Solucao

### Interfaces / DTOs
```[lang]
...
```

### Implementacao Core
```[lang]
...
```

## 3. Testes (TDD/Verification)
```[lang]
// Testes unitarios cobrindo casos de sucesso e falha
...
```

## 4. Checklist de Validacao
- [ ] Validacao de input implementada?
- [ ] Tratamento de erros adicionado?
- [ ] Testes cobrem edge cases?
```
