---
description: "Mostra estado do sistema Itzamna: kernel, memoria, integracoes."
---

# /itzamna.status

Mostre o estado atual do sistema Itzamna para o usuario.

## Passos

1. Verifique se `kernel.md` existe neste diretorio CLI
2. Verifique se `MEMORY.md` existe na raiz do projeto
3. Verifique se `CONSTITUTION.md` existe na raiz do projeto
4. Verifique se `skills/` existe e liste quantas skills ha
5. Detecte integracoes:
   - Hefesto: procure comandos `/hefesto.*` nos diretorios de comando
   - spec-kit: procure `.specify/` na raiz do projeto
6. Mostre o resultado em formato tabular

## Formato de saida

```
Itzamna Status
==============
Kernel:       OK | MISSING
MEMORY.md:    OK (X bytes) | MISSING
CONSTITUTION: OK | MISSING
Skills:       X skills instaladas
Hefesto:      linked | not found
spec-kit:     linked | not found
```

## Regras

- Este comando e somente leitura (nao modifica nada)
- Se algo estiver faltando, sugira `itzamna init` para corrigir
