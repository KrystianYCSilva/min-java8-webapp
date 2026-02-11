# Censo Superior 2025 - WebApp Java 6

Prototipo funcional com:
- Java 6 (codigo-fonte compativel)
- Servlet 2.5
- ZK Framework 3.6.2 (frontend MVC com Composer)
- Hibernate ORM 4.2 + JPA (`EntityManager`)
- Entidades anotadas com `javax.persistence`
- Tomcat 6/7
- Maven 3.x
- H2 embarcado

## Versao 2.0.0

A versao `2.0.0-SNAPSHOT` consolida o frontend ZK 3.6.2 em MVC e remove o frontend legado JSP/Servlet.

Principais pontos:
- shell autenticado em `app/menu.zul` com `header + sidebar + center + footer`;
- navegacao centralizada por querystring (`view` para conteudo principal, `sub` para sub-window modal);
- telas de cadastro/visualizacao abertas em sub-window modal para melhorar UX;
- camada `service/dao/model` JPA preservada.

## Roadmap de migracao 2026

A evolucao do projeto sera conduzida em fases para demonstrar ganho tecnico incremental e reduzir risco:

1. `main`: migracao para Java 8 mantendo comportamento funcional atual.
2. `feature/zk8-bootstrap-ui`: upgrade para ZK 8.6.0.1 + Bootstrap + melhoria de frontend (ainda MVC).
3. `feature/springboot-modernization`: migracao para Spring Boot com Spring Data, Spring Security e Spring MVC.
4. `feature/zk-mvvm-final`: migracao final de MVC para MVVM.

Artefatos de acompanhamento:
1. `docs/MIGRATION-ROADMAP.md`
2. `docs/MIGRATION-CHANGELOG.md`

## Arquitetura em camadas

- `web/zk`: composers MVC de navegacao e interacao de tela.
- `web/filter`: autenticacao de acesso a `/app/*`.
- `service`: regras de negocio e validacoes.
- `dao`: persistencia JPA (`EntityManager`/`EntityTransaction`).
- `model`: entidades de dominio.
- `util`: utilitarios de seguranca e validacao.

Padroes utilizados:
- DAO Pattern (`AlunoDAO`, `CursoDAO`, `CursoAlunoDAO`, `DocenteDAO`, `IesDAO`).
- Service Layer (`AlunoService`, `CursoService`, `CursoAlunoService`, `DocenteService`, `IesService`, `AuthService`).
- Template transacional JPA (`AbstractJpaDao`).
- Builder Pattern para entidades extensas (`Aluno`, `Curso`, `CursoAluno`, `Docente`, `Ies`).
- MVC Composer (ZK 3.6.2) para web.

## Rotas principais

Publicas:
- `/home.zul`
- `/login.zul`

Autenticadas (`AuthFilter`):
- `/app/menu.zul?view=dashboard`
- `/app/menu.zul?view=aluno-list`
- `/app/menu.zul?view=curso-list`
- `/app/menu.zul?view=curso-aluno-list`
- `/app/menu.zul?view=docente-list`
- `/app/menu.zul?view=ies-list`

Sub-window modal (parametro `sub`):
- `sub=aluno-form`, `sub=aluno-view`
- `sub=curso-form`, `sub=curso-view`
- `sub=curso-aluno-form`
- `sub=docente-form`, `sub=docente-view`
- `sub=ies-form`, `sub=ies-view`

## Modelagem de banco

Tabelas principais:
- `usuario`
- `aluno` (Registro 41)
- `curso` (Registro 21)
- `curso_aluno` (Registro 42)
- `docente` (Registro 31)
- `ies` (Registro 11)
- `municipio`

Tabelas auxiliares:
- `dominio_opcao`
- `aluno_opcao`, `curso_opcao`, `curso_aluno_opcao`
- `layout_campo`
- `aluno_layout_valor`, `curso_layout_valor`, `curso_aluno_layout_valor`, `docente_layout_valor`, `ies_layout_valor`

## Build e testes

Build:
```bash
mvn clean package
```

Em JDK moderno (sem toolchain Java 6/7), use:
```bash
mvn '-Dmaven.compiler.source=1.7' '-Dmaven.compiler.target=1.7' clean package
```

Testes:
```bash
mvn '-Dmaven.compiler.source=1.7' '-Dmaven.compiler.target=1.7' test
```

## Credencial inicial

- Login: `admin`
- Senha: `admin123`

## Referencias

- `docs/ARCHITECTURE.md`
- `docs/TEST-PLAN.md`
- `docs/MIGRATION-ROADMAP.md`
- `docs/MIGRATION-CHANGELOG.md`
