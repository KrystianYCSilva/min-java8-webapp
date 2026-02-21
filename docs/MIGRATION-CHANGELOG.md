# Migration Changelog

Este arquivo acompanha a evolucao real da migracao. Atualizar a cada merge de branch de fase.

## Status geral

| Fase | Branch | Objetivo | Status | Ultima atualizacao |
| --- | --- | --- | --- | --- |
| 1 | `main` | Java 8 com comportamento preservado | Concluida | 2026-02-11 |
| 2 | `feature/zk8-bootstrap-ui` | ZK 8.6.0.1 + Bootstrap + frontend | Concluida | 2026-02-20 |
| 3 | `feature/springboot-modernization` | Spring Boot + Data + Security + MVC | Planejada | - |
| 4 | `feature/zk-mvvm-final` | Migracao final MVC -> MVVM | Planejada | - |

## Fase 1 - `main`

### Escopo
1. Migracao Java 6 -> 8.
2. Manter ZK 3.6.2 + Spring atual + MVC.

### Mudancas realizadas
1. Em aberto.

### Evidencias
1. Em aberto.

### Pendencias
1. Em aberto.

## Fase 2 - `feature/zk8-bootstrap-ui`

### Escopo
1. Upgrade ZK 3.6.2 -> 8.6.0.1 (Community Edition).
2. Tema IceBlue Compact (Bootstrap-style integrado ao ZK 8).
3. Migracao de todos os Composers de `GenericForwardComposer` para `SelectorComposer<Window>`.
4. Adocao do padrao `@Wire` (injecao de componentes) + `@Listen` (handlers de evento).
5. Preservacao completa da navegacao MVC existente.
6. Registro do `CsrfFilter` no `web.xml`.
7. Implementacao de testes unitarios para camada `util/*`.
8. Threshold JaCoCo elevado de 5% para 30% (escopo `util/*`).

### Mudancas realizadas

#### ZK Framework
- `pom.xml`: `zk.version` alterado de `3.6.2` para `8.6.0.1`.
- `pom.xml`: adicionada dependencia `org.zkoss.theme:iceblue_c` (tema Bootstrap-style).
- `pom.xml`: atualizado `maven-war-plugin` para `3.3.2`.
- `pom.xml`: `<name>` corrigido de "WebApp Java 6" para "WebApp Java 8".
- `zk.xml`: configurado para ZK 8 (desktop-timeout, charset, etc.).

#### Composers migrados para SelectorComposer + @Wire/@Listen
Todos os 7 composers do frontend foram migrados do padrao legado `GenericForwardComposer`
(convencao `onCreate$winXxx`, `onClick$btnXxx`) para o padrao moderno ZK 8:

| Composer | Views cobertas |
|---|---|
| `AbstractBaseComposer` | Base para todos |
| `HomeComposer` | `home.zul` |
| `DashboardComposer` | `dashboard.zul` |
| `LoginComposer` | `login.zul` |
| `MenuComposer` | `menu.zul` |
| `AlunoComposer` | `aluno-list.zul`, `aluno-form.zul`, `aluno-view.zul` |
| `CursoComposer` | `curso-list.zul`, `curso-form.zul`, `curso-view.zul` |
| `DocenteComposer` | `docente-list.zul`, `docente-form.zul`, `docente-view.zul` |
| `IesComposer` | `ies-list.zul`, `ies-form.zul`, `ies-view.zul` |
| `CursoAlunoComposer` | `curso-aluno-list.zul`, `curso-aluno-form.zul` |

Padrao adotado:
- Campos injetados via `@Wire` (matching por ID do componente ZUL).
- Handlers via `@Listen("eventName = #componentId")`.
- String de versao em `MenuComposer` atualizada: "ZK 3.6.2 MVC" -> "ZK 8.6.0.1 MVC".

#### Seguranca
- `web.xml`: `CsrfFilter` registrado no mapping `/app/*` (apos `AuthFilter`).

#### Spring e Persistencia
- `applicationContext.xml`: DAOs legados substituidos por Spring Data JPA Repositories.
- `web.xml`: schema atualizado para Servlet 3.1.

#### Testes
Novos testes unitarios implementados (sem Spring Context):

| Classe de teste | Testes | Cobertura |
|---|---|---|
| `PasswordUtilTest` | 12 | PBKDF2 + SHA-256 legado, verify, needsRehash |
| `ValidationUtilsTest` | 21 | isNumeric, hasExactLength, isCpfFormatoValido, isPeriodoReferenciaValido, isSemestreValido |
| `EnumMappingTest` | 10 | CorRacaEnum, EstadoEnum, NacionalidadeEnum, NivelAcademicoEnum, FormatoOfertaEnum |
| `AuthFilterTest` | 4 | sem sessao, sem usuario, com usuario, loginPath padrao |

Testes existentes mantidos e passando:
- `CsrfFilterTest` (3), `CursoAlunoServiceTest` (2), `UsuarioAuthenticationProviderTest` (2), `RelatorioTxtControllerTest` (2).

### Evidencias
- `mvn clean test` em 2026-02-20: **BUILD SUCCESS**.
- Resultado: `Tests run: 59, Failures: 0, Errors: 0, Skipped: 3`.
- JaCoCo check `util/*`: cobertura de linhas >= 30% (threshold atendido).
- Todos os Composers compilam sem erros; nenhuma regressao de API de servico.

### Pendencias para Fase 3
- Testes de integracao para `service/*` (requerem Spring Context / H2 in-memory).
- Elevar threshold JaCoCo para `dao/*` e `service/*` apos migracao Spring Boot.
- Substituir instanciacao direta de services (`new XxxService()`) nos Composers por `@Autowired`.

## Fase 3 - `feature/springboot-modernization`

### Escopo
1. Migracao para Spring Boot.
2. Introducao de Spring Data, Spring Security e Spring MVC.
3. Eliminacao gradual da camada DAO legada.

### Mudancas realizadas
1. Em aberto.

### Evidencias
1. Em aberto.

### Pendencias
1. Em aberto.

## Fase 4 - `feature/zk-mvvm-final`

### Escopo
1. Migracao final MVC -> MVVM.
2. Consolidacao de padrao de ViewModel/Binder.

### Mudancas realizadas
1. Em aberto.

### Evidencias
1. Em aberto.

### Pendencias
1. Em aberto.

