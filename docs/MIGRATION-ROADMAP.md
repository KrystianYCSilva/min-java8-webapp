# Migration Roadmap - Java 8, ZK 8.6.0.1, Spring Boot e MVVM

## 1. Objetivo

Migrar o projeto legado de forma incremental para uma stack moderna, com demonstracao clara de evolucao tecnica para o time:

1. Java 6 -> Java 8.
2. ZK 3.6.2 -> ZK 8.6.0.1 com melhoria visual.
3. Spring tradicional -> Spring Boot com Spring Data, Spring Security e Spring MVC.
4. MVC (Composer) -> MVVM (Binder/ViewModel) como etapa final.

## 2. Principios de execucao

1. Uma mudanca arquitetural dominante por branch.
2. Cada branch deve compilar, testar e estar demonstravel.
3. Sem Big Bang: migracao por modulo (Aluno, Curso, CursoAluno, Docente, IES).
4. Refatoracao protegida por testes de caracterizacao nas regras criticas.
5. Rollback simples por branch (sem dependencia oculta entre fases).

## 3. Estrategia de branches

| Ordem | Branch | Objetivo principal | Estado da UI |
| --- | --- | --- | --- |
| 1 | `main` | Subir para Java 8 com comportamento preservado | MVC |
| 2 | `feature/zk8-bootstrap-ui` | Migrar ZK para 8.6.0.1 + Bootstrap + melhoria frontend | MVC |
| 3 | `feature/springboot-modernization` | Migrar para Spring Boot e padronizar repositorios Spring Data | MVC |
| 4 | `feature/zk-mvvm-final` | Migrar telas/composers para MVVM e consolidar padrao final | MVVM |

## 4. Plano detalhado por fase

### Fase 1 - `main` (Java 8 baseline)

Escopo:
1. Atualizar compilacao para `source/target 1.8`.
2. Ajustar plugins Maven para suportar Java 8 com estabilidade.
3. Remover incompatibilidades de sintaxe/API legadas.
4. Manter ZK 3.6.2, Spring atual e padrao MVC sem mudancas funcionais.

Entregaveis:
1. Build verde em Java 8 (`mvn clean test`).
2. CRUDs e autenticacao funcionando sem regressao funcional.
3. Documentacao do baseline Java 8 atualizada.

Criterio de pronto:
1. Todos os testes unitarios/integracao existentes passando.
2. Fluxos de login, menu, CRUD e import/export validados manualmente.

### Fase 2 - `feature/zk8-bootstrap-ui` (upgrade visual e ZK)

Escopo:
1. Atualizar ZK para `8.6.0.1`.
2. Adaptar `zul` e composers para APIs/eventos/annotacoes compativeis.
3. Aplicar Bootstrap para layout responsivo e melhoria visual.
4. Preservar navegacao `view/sub/id` enquanto ainda estiver em MVC.

Entregaveis:
1. Interface modernizada com mesma cobertura funcional.
2. Padrao visual unico (tokens de estilo, grid, formularios, feedback).
3. Evidencia de compatibilidade de telas em desktop e resolucoes menores.

Criterio de pronto:
1. Nenhuma regressao nos fluxos existentes.
2. Performance de renderizacao e navegacao equivalente ou melhor.

### Fase 3 - `feature/springboot-modernization` (core Spring)

Escopo:
1. Migrar configuracao XML para configuracao Java/Spring Boot.
2. Introduzir `spring-boot-starter-web`, `spring-boot-starter-security` e Spring Data JPA.
3. Eliminar DAOs legados quando houver repositorio Spring Data equivalente.
4. Padronizar injecao de dependencias via Spring em servicos e integracao com ZK.
5. Manter aplicacao funcional em WAR (ou empacotamento definido para demonstracao).

Entregaveis:
1. Inicializacao pelo contexto Spring Boot.
2. Repositorios Spring Data ativos para entidades principais.
3. Cadeia de seguranca funcionando no novo setup.
4. Documentacao de configuracao e propriedades consolidada.

Criterio de pronto:
1. Todos os modulos funcionando sem camada DAO customizada obrigatoria.
2. Testes de service/repository cobrindo persistencia essencial.

### Fase 4 - `feature/zk-mvvm-final` (padrao alvo)

Escopo:
1. Migrar telas de MVC Composer para MVVM (`ViewModel`, `@Command`, `@NotifyChange`).
2. Organizar bindings de formulario/listagem com validacoes consistentes.
3. Remover composers substituidos e simplificar codigo de tela.
4. Consolidar guia de padrao MVVM para novas features.

Entregaveis:
1. Modulos chave em MVVM (Aluno, Curso, CursoAluno, Docente, IES).
2. Reducao de acoplamento entre UI e regra de negocio.
3. Material de demonstracao comparando MVC vs MVVM no projeto.

Criterio de pronto:
1. Fluxos equivalentes aos da fase 3, sem regressao funcional.
2. Time apto a evoluir novas telas no padrao MVVM.

## 5. Estrategia de migracao de codigo

1. Primeiro migrar modulo por modulo, nao tela por tela aleatoria.
2. Criar testes de caracterizacao em fluxos sensiveis antes de refatorar.
3. Aplicar parallel change quando trocar infraestrutura (DAO -> Repository, Composer -> ViewModel).
4. Evitar mistura de mudanca de regra de negocio com mudanca de framework.

## 6. Riscos e mitigacoes

| Risco | Impacto | Mitigacao |
| --- | --- | --- |
| Upgrade ZK quebrar componentes antigos | Medio/alto | Migrar por modulo, validar eventos e databinding por smoke test |
| Migracao Spring Boot alterar ciclo de inicializacao | Alto | Homologar contexto por etapas e manter fallback de configuracao durante transicao |
| Substituicao de DAO gerar regressao de consulta | Medio | Introduzir repositories com testes de integracao antes de remover DAO legado |
| Migracao para MVVM aumentar retrabalho de UI | Medio | Migrar modulo a modulo com contrato visual e funcional fixo |

## 7. Qualidade e gates por branch

1. Build Maven e testes unitarios/integracao obrigatorios.
2. Smoke test manual dos fluxos de login, menu e CRUD principal.
3. Atualizacao obrigatoria de `docs/MIGRATION-CHANGELOG.md` em cada merge.
4. Atualizacao obrigatoria de `.context/_meta/*` quando houver decisao arquitetural nova.

## 8. Narrativa para demonstracao ao time

1. Fase 1: prova de compatibilidade tecnica (Java 8 sem quebra funcional).
2. Fase 2: prova de ganho de UX e manutenibilidade de frontend (ZK 8 + Bootstrap).
3. Fase 3: prova de padronizacao enterprise (Spring Boot + Data + Security + MVC).
4. Fase 4: prova final de produtividade e organizacao de UI (MVVM).

## 9. Artefatos de controle

1. Planejamento: `docs/MIGRATION-ROADMAP.md`.
2. Evolucao por fase: `docs/MIGRATION-CHANGELOG.md`.
3. Contexto para agentes: `.context/_meta/migration-roadmap.md`.
