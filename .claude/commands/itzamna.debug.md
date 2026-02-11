---
description: "Analise de stack traces e debug de erros com root cause analysis (5 Whys) e solucao definitiva. Use quando: tiver um stack trace, mensagem de erro ou descricao de bug."
---

# /itzamna.debug

Analise de erros para identificar Causa Raiz e propor correcao definitiva (nao apenas patch sintomatico).

**Input:** `$ARGUMENTS` (stack trace, mensagem de erro ou descricao do bug)

---

## Instructions

### 1. Triagem do Erro

- Analise a mensagem de erro e o stack trace.
- Identifique a linha exata e o tipo de excecao.
- Verifique o contexto fornecido (inputs, estado do sistema).

### 2. Formulacao de Hipoteses

- Liste 2-3 causas provaveis baseadas no comportamento.
- Considere: Input invalido? Estado nulo? Dependencia externa falhou? Concorrencia?

### 3. Analise de Causa Raiz (5 Whys)

- Nao pare no sintoma ("Variavel e nula").
- Pergunte por que ela e nula (ex: "O servico de user retornou null porque o ID nao existia").

### 4. Solucao e Prevencao

- Crie uma correcao robusta.
- Sugira como prevenir a reincidencia (testes, validacao, tipagem).

---

## Constraints

- SEMPRE explicar *por que* o erro ocorreu antes de dar a solucao.
- SEMPRE fornecer o codigo corrigido.
- SEMPRE sugerir um teste defensivo ou unitario para evitar regressao.
- NAO sugerir `try-catch` generico que engula o erro.
- NAO assumir dependencias que nao estao no contexto.

---

## Output Format

```markdown
# Analise de Debug

## O Problema
**Erro:** `TipoDoErro: Mensagem`
**Localizacao:** `Arquivo.ext:Linha`

## Analise de Causa Raiz
Explicacao detalhada do fluxo que levou ao erro.
1. O sistema recebeu X...
2. A funcao Y esperava Z...
3. A falha ocorreu porque...

## Solucao Proposta
```[lang]
// Codigo Corrigido
```

## Prevencao (Defensive Coding)
Sugestao de teste ou validacao para evitar recorrencia:
```[lang]
// Exemplo de teste ou assercao
```
```
