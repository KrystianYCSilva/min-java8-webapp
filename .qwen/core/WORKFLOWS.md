---
description: |
  Standard workflows for common tasks: implement feature, fix bugs, code review, testing, refactoring, docs, create skills.
  Use when: you need guidance on how to approach a specific type of task in K2+ level.
---

# Workflows

> Padroes de trabalho para tarefas comuns.
> Carregue este arquivo em tarefas K2+ quando precisar de orientacao.

---

## 1. Implementar feature

```
1. Entenda o requisito (pergunte se ambiguo)
2. Identifique arquivos afetados
3. Planeje as mudancas (liste para o usuario)
4. Peca aprovacao do plano
5. Implemente um arquivo por vez
6. Rode testes/build
7. Apresente resultado final
```

**Se spec-kit detectado:** delegue para `/speckit.specify` -> `/speckit.plan` -> `/speckit.implement`

---

## 2. Corrigir bug

```
1. Reproduza ou entenda o erro
2. Trace a causa raiz
3. Proponha a correcao
4. Peca aprovacao
5. Implemente e valide
```

---

## 3. Code review

```
1. Leia as mudancas (diff ou arquivos)
2. Avalie contra CONSTITUTION.md:
   T0 = blocker, T1 = warning, T2 = sugestao
3. Apresente achados por severidade
4. Sugira correcoes concretas
```

---

## 4. Escrever testes

```
1. Identifique o alvo (funcao, modulo, endpoint)
2. Defina cenarios: happy path, edge cases, erros
3. Siga o padrao existente no projeto
4. Rode e garanta cobertura critica
```

---

## 5. Refatorar

```
1. Garanta que testes existem ANTES
2. Faca mudancas incrementais
3. Rode testes apos cada mudanca
4. Nao mude comportamento
```

---

## 6. Criar skill

**Se Hefesto detectado:** delegue para `/hefesto.create`

**Se nao:**
```
1. Pesquise em documentacao oficial (min 2 fontes)
2. Use templates/skill-template.md
3. Escreva SKILL.md (max 500 linhas)
4. Peca aprovacao
```

---

## Principios

- Um passo por vez
- Mostre o trabalho
- Falhe rapido
- Na duvida, pergunte

---

*Workflows v3.0*
