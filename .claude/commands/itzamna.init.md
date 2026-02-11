---
description: "Inicializa Itzamna num projeto: verifica arquivos, cria o que falta."
---

# /itzamna.init

Inicialize ou verifique a instalacao do Itzamna neste projeto.

## Passos

1. Verifique se os arquivos essenciais existem:
   - `kernel.md` neste diretorio CLI
   - `CONSTITUTION.md` na raiz do projeto
   - `MEMORY.md` na raiz do projeto

2. Para cada arquivo faltando:
   - Informe o usuario
   - Sugira rodar `itzamna init` no terminal para reinstalar

3. Verifique integracoes:
   - Hefesto: procure `/hefesto.*` nos comandos
   - spec-kit: procure `.specify/` na raiz

4. Se tudo OK, confirme:
   ```
   Itzamna OK. Kernel ativo. Memoria disponivel.
   Hefesto: [status]. spec-kit: [status].
   ```

5. Se `MEMORY.md` existe, leia e apresente o estado atual do projeto

## Regras

- Este comando NUNCA cria arquivos por conta propria
- Apenas diagnostica e sugere acoes ao usuario
- Se o kernel estiver faltando, alerte que o sistema nao esta funcional
