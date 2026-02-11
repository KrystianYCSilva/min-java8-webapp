---
description: "Gerencia a memoria do projeto: ler, atualizar, ou verificar MEMORY.md."
---

# /itzamna.memory

Gerencie a memoria do projeto (MEMORY.md).

## Uso

```
/itzamna.memory              <- Mostra estado atual da memoria
/itzamna.memory update       <- Atualiza com o estado da sessao atual
/itzamna.memory check        <- Verifica se memoria esta atualizada
```

## Subcomando: (sem argumento) - Ler

1. Leia `MEMORY.md` da raiz do projeto
2. Apresente um resumo:
   - Estado atual do projeto
   - Ultimas 3 decisoes
   - Proximos passos pendentes
   - Numero de sessoes registradas

## Subcomando: update

1. Leia `MEMORY.md` atual
2. Analise o que foi feito nesta sessao (baseado no historico da conversa)
3. Proponha as atualizacoes:
   - Nova entrada na tabela de Sessoes
   - Novas decisoes (se houve)
   - Atualizacao do estado atual
   - Atualizacao dos proximos passos
4. Mostre o diff proposto
5. **Peca aprovacao antes de escrever**

## Subcomando: check

1. Leia `MEMORY.md`
2. Verifique:
   - Ultima atualizacao e recente? (avise se > 3 sessoes sem update)
   - Proximos passos estao marcados? (itens [ ] vs [x])
   - Estado atual reflete a realidade do projeto?
3. Se desatualizada, sugira `/itzamna.memory update`

## Regras

- NUNCA modifique MEMORY.md sem aprovacao humana
- NUNCA apague entradas existentes (append-only)
- Formato de data: YYYY-MM-DD
- Mantenha resumos concisos (1-2 linhas por sessao)
