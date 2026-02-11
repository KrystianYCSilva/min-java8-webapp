---
description: Registro de decisoes arquiteturais e tecnicas do projeto, com racional e impacto para manutencao futura.
tier: T2
triggers:
  - adr
  - decisao
  - tradeoff
  - historico
  - arquitetura
  - zk
  - jpa
  - migracao
  - springboot
  - mvvm
last_updated: 2026-02-11
---
# Decisoes Chave

| ID | Decisao | Status | Racional | Impacto |
| --- | --- | --- | --- | --- |
| ADR-001 | Arquitetura em camadas (`web/zk`, `service`, `dao`, `model`, `util`) | Aceita | Facilita manutencao e separa responsabilidades | Evita acoplamento entre HTTP, regra e persistencia |
| ADR-002 | Persistencia JPA com Hibernate 4.2 (`EntityManager`) | Aceita | Padronizar API de persistencia mantendo Java 6 | DAOs seguem `AbstractJpaDao` e transacao explicita |
| ADR-003 | H2 embarcado para ambiente local e testes | Aceita | Rapidez de setup e execucao de suite automatizada | Aproxima testes de persistencia sem dependencia externa |
| ADR-004 | Campos de layout modelados com metadados (`layout_campo` + `_layout_valor`) | Aceita | Cobrir variacao de leiautes sem inflar schema principal | Import/export permanece extensivel por configuracao |
| ADR-005 | Autenticacao por sessao HTTP + `AuthFilter` | Aceita | Simplicidade operacional no stack servlet legado | Requer cuidado com protecao de rotas `/app/*` |
| ADR-006 | Gate de cobertura JaCoCo em `dao/service/util` com minimo 80% | Aceita | Protege camadas com maior risco de regressao | Mudancas nessas camadas devem incluir testes |
| ADR-007 | E2E Selenium mantido com `@Ignore` por padrao | Aceita | Evita fragilidade em ambiente sem navegador/driver | Execucao E2E depende de setup explicito |
| ADR-008 | Inclusao dos modulos `Docente` (31) e `IES` (11) no mesmo padrao de Aluno/Curso | Aceita | Manter consistencia operacional de CRUD, importacao e exportacao TXT pipe | Menor custo de manutencao entre modulos |
| ADR-009 | Validacao de municipio por tabela de apoio pre-carregada (`municipio`) | Aceita | Evitar inconsistencias entre UF e codigo de municipio em importacao/cadastro | Exige seed dedicado e cobertura de testes para a tabela |
| ADR-010 | `CursoAluno` mantido em telas separadas (`list` e `form`) | Aceita | Melhorar legibilidade do Registro 42 | Reduz complexidade visual do modulo |
| ADR-011 | Builder Pattern para entidades com formulario extenso | Aceita | Reduzir setter-sprawl e padronizar construcao de objetos no web layer | Melhora legibilidade e reduz omissao de campos |
| ADR-012 | Web layer sem dependencia direta de DAO para dados de tela | Aceita | Preservar isolamento de camadas (`web -> service -> dao`) | Facilita testes e evolucao de regras |
| ADR-013 | Hash de senha PBKDF2 com migracao transparente de SHA-256 legado | Aceita | Elevar seguranca sem quebrar base existente de usuarios | Login com hash legado dispara rehash para PBKDF2 |
| ADR-014 | Frontend ZK 3.6.2 MVC substitui JSP/servlets de tela | Aceita | Unificar stack de interface e reduzir codigo legado duplicado | Navegacao e eventos passam para composers ZK |
| ADR-015 | Shell unico em `menu.zul` com `view/sub` e sub-window modal | Aceita | UX mais consistente e roteamento centralizado | Menor acoplamento entre telas e maior reuso do layout |
| ADR-016 | Modernizacao em fases por branch (sem Big Bang) | Aceita | Reduzir risco de regressao e facilitar demonstracao incremental para o time | Cada fase possui gate tecnico, rollback e evidencia propria |
| ADR-017 | Ordem de migracao: Java 8 -> ZK 8 + UI -> Spring Boot -> MVVM | Aceita | Separar mudancas de plataforma das mudancas de padrao de UI | Facilita diagnostico de regressao e aprovacao por etapa |
| ADR-018 | Migracao para MVVM como fase final de convencimento tecnico | Aceita | Mostrar ganhos de produtividade/manutencao apos stack estabilizada | Evita atribuir problemas de infraestrutura ao novo padrao MVVM |
| ADR-019 | Eliminacao gradual de DAOs legados em favor de Spring Data | Aceita | Padronizar persistencia no ecossistema Spring com menor codigo boilerplate | Exige parallel change com testes de integracao antes de remover DAO |

## Quando atualizar este arquivo

1. Introducao de nova regra estrutural.
2. Troca de tecnologia de persistencia, build ou runtime.
3. Mudanca de estrategia de testes e gates.
4. Mudanca de formato de importacao/exportacao.
5. Inclusao de novo modulo de registro no menu principal.
