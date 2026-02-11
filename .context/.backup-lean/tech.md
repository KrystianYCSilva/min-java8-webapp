---
description: |
  Tech stack and architecture decisions.
  Use when: you need to understand the technologies, patterns, and architectural choices.
---

# Tech Stack & Architecture

## Stack

| Camada | Tecnologia | Versao |
|--------|------------|--------|
| Linguagem | Java | 1.6 → 1.8 (em migração) |
| Servlet Container | Servlet API | 2.5 |
| Framework Web | ZK Framework | 3.6.2 |
| ORM | Hibernate + JPA | 4.2.21.Final |
| Database | H2 (embarcado) | 1.3.176 |
| Build | Maven | 3.x |
| Testes Unitários | JUnit + Mockito + DBUnit | 4.11, 1.10.19, 2.5.4 |
| Testes E2E | Selenium WebDriver | 2.53.1 |
| Cobertura | JaCoCo | 0.8.8 (mínimo 80%) |

**Versões Spring (preparação para fase 3):**
- Spring Framework: 4.3.30.RELEASE
- Spring Security: 4.2.13.RELEASE
- Spring Data JPA: 1.11.23.RELEASE

## Arquitetura

**Padrão:** Aplicação web em camadas (Layered Architecture)

```
┌─────────────────────────────────┐
│   ZK Composers (MVC)            │  ← web layer (presentation)
├─────────────────────────────────┤
│   Service Layer                 │  ← business logic
├─────────────────────────────────┤
│   DAO Layer (JPA)               │  ← persistence
├─────────────────────────────────┤
│   Model (JPA Entities)          │  ← domain
└─────────────────────────────────┘
```

**Padrões de design utilizados:**
- **DAO Pattern:** `AbstractJpaDao` com template transacional JPA
- **Service Layer:** Encapsulamento de lógica de negócio e validações
- **Builder Pattern:** Construção de entidades complexas (Aluno, Curso, Docente, IES)
- **MVC Composer (ZK):** Separação view (.zul) / controller (Composer)
- **Repository Pattern (preparação):** Interfaces Spring Data JPA para fase 3

## Estrutura de diretorios

```
src/
├── main/
│   ├── java/br/gov/inep/censo/
│   │   ├── config/          # Bootstrap, ConnectionFactory, Hibernate
│   │   ├── dao/             # Camada de persistência JPA (DAO Pattern)
│   │   ├── domain/          # Enums de negócio e categorias
│   │   ├── model/           # Entidades JPA (@Entity)
│   │   │   └── enums/       # Enums de domínio (CorRaca, Nacionalidade, etc.)
│   │   ├── repository/      # Interfaces Spring Data (preparação fase 3)
│   │   ├── service/         # Regras de negócio e validações
│   │   ├── spring/          # Configurações Spring (preparação fase 3)
│   │   │   ├── datasource/  # DataSource Spring
│   │   │   └── security/    # Spring Security
│   │   ├── util/            # Utilitários (validação, CSRF, password)
│   │   └── web/
│   │       ├── filter/      # AuthFilter, CsrfFilter (Servlet 2.5)
│   │       ├── spring/      # Controllers Spring MVC (preparação fase 3)
│   │       └── zk/          # Composers ZK MVC (atual)
│   │           ├── auth/    # LoginComposer
│   │           ├── home/    # HomeComposer, DashboardComposer
│   │           ├── menu/    # MenuComposer (shell autenticado)
│   │           └── modulo/  # CRUD Composers (Aluno, Curso, etc.)
│   ├── resources/
│   │   └── META-INF/
│   │       └── persistence.xml  # Configuração JPA/Hibernate
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── web.xml              # Servlet 2.5 descriptor
│       │   ├── zk.xml               # ZK configuration
│       │   └── applicationContext.xml  # Spring context (preparação)
│       ├── app/             # Telas autenticadas (.zul)
│       │   ├── menu.zul     # Shell principal (header+sidebar+center+footer)
│       │   ├── *-list.zul   # Listagens (aluno, curso, docente, ies)
│       │   ├── *-form.zul   # Formulários de cadastro (sub-window modal)
│       │   └── *-view.zul   # Visualização detalhada (sub-window modal)
│       ├── home.zul         # Página inicial pública
│       └── login.zul        # Tela de autenticação
└── test/
    └── java/br/gov/inep/censo/
        ├── dao/             # Testes de persistência (DBUnit)
        ├── service/         # Testes de negócio (Mockito)
        ├── support/         # Helpers de teste (TestDatabaseSupport)
        ├── util/            # Testes de utilitários
        └── web/filter/      # Testes de filtros (CsrfFilter)
```

## Dependencias criticas

**Produção:**
- **zk:3.6.2** - Framework web MVC (migração futura: 8.6.0.1)
- **hibernate-core:4.2.21** - ORM JPA
- **h2:1.3.176** - Database embarcado (in-memory)
- **servlet-api:2.5** - Servlet container (provided)
- **spring-*:4.3.30** - Preparação para Spring Boot (fase 3)

**Desenvolvimento:**
- **junit:4.11** - Framework de testes unitários
- **mockito-all:1.10.19** - Mocks para testes de service
- **dbunit:2.5.4** - Testes de database com datasets XML
- **selenium:2.53.1** - Testes E2E de interface web

**Build:**
- Maven compiler: source/target 1.6 (migração para 1.8 na fase 1)
- Maven WAR plugin: 2.6
- Maven Surefire: 2.19.1
- JaCoCo: 0.8.8 com 80% cobertura mínima em dao/service/util

## Convenções de código

**Nomenclatura:**
- **Classes:** PascalCase (`AlunoService`, `CursoDAO`, `IesComposer`)
- **Métodos:** camelCase (`buscarPorCpf()`, `salvar()`, `listarTodos()`)
- **Constantes:** UPPER_SNAKE_CASE (`MAX_RESULTS`, `DEFAULT_PAGE_SIZE`)
- **Pacotes:** lowercase (`br.gov.inep.censo.service`)
- **Variáveis:** camelCase (`nomeAluno`, `codigoCurso`)

**JPA/Hibernate:**
- Entidades anotadas com `javax.persistence.*`
- Nomenclatura de tabelas: snake_case (`aluno`, `curso_aluno`)
- Transações gerenciadas manualmente no DAO via `EntityTransaction`
- `AbstractJpaDao` como template base para DAOs

**ZK Framework:**
- Arquivos `.zul` para views (XML-based)
- Composers estendem `AbstractBaseComposer`
- Convenção: `*Composer.java` para controllers
- Navegação via querystring: `?view=` (conteúdo) e `?sub=` (modal)
