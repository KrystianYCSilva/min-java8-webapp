# Migration Changelog

Este arquivo acompanha a evolucao real da migracao. Atualizar a cada merge de branch de fase.

## Status geral

| Fase | Branch | Objetivo | Status | Ultima atualizacao |
| --- | --- | --- | --- | --- |
| 1 | `main` | Java 8 com comportamento preservado | Concluida | 2026-02-11 |
| 2 | `feature/zk8-bootstrap-ui` | ZK 8.6.0.1 + Bootstrap + frontend | Concluida | 2026-02-20 |
| 3 | `feature/springboot-modernization` | Spring Boot + Data + Security + MVC | Concluida | 2026-02-20 |
| 4 | `feature/zk-mvvm-final` | Migracao final MVC -> MVVM | Concluida | 2026-02-21 |

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
1. Eliminar instanciacao direta `new XxxService()` nos Composers ZK.
2. Migrar XML de configuracao Spring para classes `@Configuration` Java.
3. Introduzir `@Service` + `@Autowired` em todos os services.
4. Introduzir `@Component` + `@PersistenceContext` nos repositories custom.
5. Reativar testes de integracao de `service/*` com Spring Context + H2 in-memory.
6. Manter WAR deployavel sem regressao funcional.

### Mudancas realizadas

#### Services — injecao de dependencia completa
Todos os services refatorados para `@Service` + `@Autowired` (construtor), sem `new` legado e sem `SpringBridge`:

| Service | Mudancas |
|---|---|
| `CatalogoService` | `@Service`, construtor `@Autowired(OpcaoDominioRepository, LayoutCampoRepository)` |
| `AuthService` | `@Service`, construtor `@Autowired(UsuarioRepository)`, `@Transactional` em `autenticarComRepository` |
| `AlunoService` | `@Service`, construtor `@Autowired(LayoutCampoValueRepository, AlunoRepository, OpcaoVinculoRepository, CatalogoService)`, `@Transactional` em CRUD |
| `CursoService` | `@Service`, construtor `@Autowired` com `CatalogoService`, `@Transactional` em CRUD |
| `DocenteService` | `@Service`, construtor `@Autowired(LayoutCampoValueRepository, DocenteRepository, MunicipioRepository)`, `@Transactional` |
| `IesService` | `@Service`, construtor `@Autowired`, `@Transactional` |
| `CursoAlunoService` | `@Service`, construtor `@Autowired(CursoAlunoRepository, OpcaoVinculoRepository, LayoutCampoValueRepository)` + `@PersistenceContext EntityManager` para `getReference()`, `@Transactional` |

Removidos: construtores no-arg, campos `PlatformTransactionManager`, `EntityManagerFactory` e chamadas a `SpringBridge.getBean()`.

#### Repositories custom — @Component + @PersistenceContext
| Repository | Mudancas |
|---|---|
| `OpcaoVinculoRepository` | `@Component`, `@PersistenceContext EntityManager entityManager` (campo de instancia), sem `EntityManagerFactory`, sem parametro EM nos metodos |
| `LayoutCampoValueRepository` | Mesmo padrao |

#### Composers ZK — SpringUtil.getBean() lazy
Todos os Composers substituiram campos `private final XxxService = new XxxService()` por metodos privados lazy que delegam ao contexto Spring via `SpringUtil.getBean("beanName")`:

| Composer | Metodos de servico adicionados |
|---|---|
| `LoginComposer` | `authService()` |
| `AlunoComposer` | `alunoService()`, `catalogoService()` |
| `CursoComposer` | `cursoService()`, `catalogoService()` |
| `DocenteComposer` | `docenteService()`, `catalogoService()` |
| `IesComposer` | `iesService()`, `catalogoService()` |
| `CursoAlunoComposer` | `cursoAlunoService()`, `alunoService()`, `cursoService()`, `catalogoService()` |

Padrao adotado (ZK 8 aceita apenas `String` no `SpringUtil.getBean()`):
```java
private AlunoService alunoService() {
    return (AlunoService) SpringUtil.getBean("alunoService");
}
```

#### Configuracao Spring — XML substituido por @Configuration Java
Quatro classes `@Configuration` criadas em `br.gov.inep.censo.config`:

| Classe | Substitui | Responsabilidade |
|---|---|---|
| `AppConfig` | `applicationContext.xml` (parcial) | `@ComponentScan(service, repository)`, `DataSource` bean, `@EnableTransactionManagement` |
| `JpaConfig` | `applicationContext.xml` (JPA) | `@EnableJpaRepositories`, `LocalContainerEntityManagerFactoryBean`, `JpaTransactionManager` |
| `SecurityConfig` | `security-context.xml` | `WebSecurityConfigurerAdapter`, `SessionUsuarioAuthenticationFilter`, `UsuarioAuthenticationProvider` |
| `MvcConfig` | `mvc-context.xml` | `@EnableWebMvc`, `@ComponentScan("br.gov.inep.censo.web.spring")` |

#### web.xml — AnnotationConfigWebApplicationContext
Trocado o mecanismo de boot do contexto Spring:
- `contextClass` = `AnnotationConfigWebApplicationContext`
- `contextConfigLocation` = `AppConfig JpaConfig SecurityConfig` (espaco-separados)
- DispatcherServlet: `contextClass` = `AnnotationConfigWebApplicationContext`, `contextConfigLocation` = `MvcConfig`

Os XMLs originais (`applicationContext.xml`, `security-context.xml`, `mvc-context.xml`) foram mantidos no repositorio como referencia historica.

#### Testes — reativados com Spring Context
Testes de integracao reativados (removido `@Ignore`):

| Classe | Abordagem | Testes |
|---|---|---|
| `AuthServiceTest` | `@RunWith(SpringJUnit4ClassRunner.class)` + `@ContextConfiguration(TestDatabaseConfig.class)` + `@Autowired AuthService` | 2 |
| `CatalogoServiceTest` | Mesmo padrao + `@Autowired CatalogoService` | 1 |
| `CursoAlunoServiceTest` | Unit test com Mockito, construtor atualizado para 3 params (sem TM/EMF) | 2 |

`TestDatabaseConfig` criado em `src/test/java`: H2 in-memory + EmbeddedDatabaseBuilder + todos os scripts SQL + JPA + `@ComponentScan(service, repository)`.

#### pom.xml
- `junit` atualizado de `4.11` para `4.12` (exigido por `SpringJUnit4ClassRunner`).
- `spring-test` `4.3.30.RELEASE` adicionado como dependencia de teste.

### Evidencias
- `mvn clean test` em 2026-02-20: **BUILD SUCCESS**.
- Resultado: `Tests run: 59, Failures: 0, Errors: 0, Skipped: 0`.
- JaCoCo check `util/*`: cobertura de linhas >= 30% (threshold atendido).
- Zero instancias de `new XxxService()` nos Composers.
- Zero referencias a `SpringBridge.getBean()` nos Services e Repositories.

### Pendencias para Fase 4
- Migracao MVC -> MVVM (ViewModels ZK 8).
- Elevar threshold JaCoCo para `service/*` e `repository/*`.
- Considerar remocao dos XMLs legados (`applicationContext.xml`, `security-context.xml`, `mvc-context.xml`).

## Fase 4 - `feature/zk-mvvm-final`

### Escopo
1. Migracao completa de todos os Composers ZK para ViewModels MVVM (ZK 8 BindComposer).
2. Upgrade `zk.version` de `8.6.0.1` para `8.6.0.2` e adicao do artefato `zkbind`.
3. Adocao uniforme do padrao `@Init`, `@Command`, `@NotifyChange` em todos os modulos.
4. Eliminacao total de `SelectorComposer` e `AbstractBaseComposer` do codigo de producao.

### Mudancas realizadas

#### pom.xml
- `zk.version` atualizado de `8.6.0.1` para `8.6.0.2`.
- Dependencia `org.zkoss.zk:zkbind:8.6.0.2` adicionada (exigida pelo mecanismo de data-binding MVVM do ZK 8).

#### ViewModels criados em `br.gov.inep.censo.web.zk`

| Classe | Substitui | ZULs cobertos |
|---|---|---|
| `AbstractBaseViewModel` | `AbstractBaseComposer` | Base para todos os ViewModels |
| `auth/LoginViewModel` | `LoginComposer` | `login.zul` |
| `home/HomeViewModel` | `HomeComposer` | `home.zul` |
| `home/DashboardViewModel` | `DashboardComposer` | `home-content.zul` |
| `menu/MenuViewModel` | `MenuComposer` | `menu.zul` |
| `modulo/AlunoViewModel` | `AlunoComposer` | `aluno-list.zul`, `aluno-form.zul`, `aluno-view.zul` |
| `modulo/CursoViewModel` | `CursoComposer` | `curso-list.zul`, `curso-form.zul`, `curso-view.zul` |
| `modulo/CursoAlunoViewModel` | `CursoAlunoComposer` | `curso-aluno-list.zul`, `curso-aluno-form.zul` |
| `modulo/DocenteViewModel` | `DocenteComposer` | `docente-list.zul`, `docente-form.zul`, `docente-view.zul` |
| `modulo/IesViewModel` | `IesComposer` | `ies-list.zul`, `ies-form.zul`, `ies-view.zul` |

Padrao adotado em todos os ViewModels:
- `@Init` para inicializacao de estado; detecta ZUL atual via `Executions.getCurrent().getServletPath()` em modulos com UI dinamica.
- `@Command` + `@NotifyChange` para acoes (salvar, excluir, navegar, buscar).
- Injecao de services via `SpringUtil.getBean("beanName")` (compativel com ZK 8 CE + Spring 4).
- Campos dinamicos de catalogo (checkboxes/textboxes) construidos em `@Init` — padrao correto MVVM para ZK 8.

#### ZULs migrados para MVVM (19 no total)

Todos os ZULs substituiram `apply="br.gov.inep.censo.web.zk.XxxComposer"` por:
```xml
apply="org.zkoss.bind.BindComposer"
viewModel="@id('vm') @init('br.gov.inep.censo.web.zk.modulo.XxxViewModel')"
```

| ZUL | ViewModel |
|---|---|
| `login.zul` | `LoginViewModel` |
| `home.zul` | `HomeViewModel` |
| `app/home-content.zul` | `DashboardViewModel` |
| `app/menu.zul` | `MenuViewModel` |
| `app/aluno-list.zul` | `AlunoViewModel` |
| `app/aluno-form.zul` | `AlunoViewModel` |
| `app/aluno-view.zul` | `AlunoViewModel` |
| `app/curso-list.zul` | `CursoViewModel` |
| `app/curso-form.zul` | `CursoViewModel` |
| `app/curso-view.zul` | `CursoViewModel` |
| `app/curso-aluno-list.zul` | `CursoAlunoViewModel` |
| `app/curso-aluno-form.zul` | `CursoAlunoViewModel` |
| `app/docente-list.zul` | `DocenteViewModel` |
| `app/docente-form.zul` | `DocenteViewModel` |
| `app/docente-view.zul` | `DocenteViewModel` |
| `app/ies-list.zul` | `IesViewModel` |
| `app/ies-form.zul` | `IesViewModel` |
| `app/ies-view.zul` | `IesViewModel` |

Todos os ZULs utilizam `@bind` para campos de formulario, `@load` para dados read-only e listas,
`@command` para acoes de botao, e `<template name="model">` para `<combobox>`.

### Evidencias
- `mvn clean test` em 2026-02-21: **BUILD SUCCESS**.
- Resultado: `Tests run: 59, Failures: 0, Errors: 0, Skipped: 0`.
- JaCoCo check `util/*`: cobertura de linhas >= 30% (threshold atendido).
- Zero referencias a `SelectorComposer` ou `AbstractBaseComposer` no codigo de producao.
- Todos os 10 ViewModels compilam sem erros; nenhuma regressao de API de servico ou repositorio.

### Pendencias
- Elevar threshold JaCoCo para `service/*` e `repository/*` (postergado para manutencao continua).
- Considerar remocao dos XMLs legados (`applicationContext.xml`, `security-context.xml`, `mvc-context.xml`) em refatoracao futura.
- Testes de integracao end-to-end para os ViewModels (requerem ZK Test Framework ou Selenium).

