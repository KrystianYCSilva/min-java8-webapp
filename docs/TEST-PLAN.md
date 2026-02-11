# Plano de Testes - Censo Superior 2025

## 1. Setup de ambiente

Dependencias de teste no `pom.xml`:
- `junit:junit:4.11`
- `org.dbunit:dbunit:2.5.4`
- `org.mockito:mockito-all:1.10.19`
- `org.seleniumhq.selenium:selenium-java:2.53.1`

Persistencia em producao/testes:
- `org.hibernate:hibernate-core:4.2.21.Final`
- `org.hibernate:hibernate-entitymanager:4.2.21.Final`

Frontend em producao:
- `org.zkoss.zk:zk:3.6.2`
- `org.zkoss.zk:zul:3.6.2`
- `org.zkoss.zk:zhtml:3.6.2`
- `org.zkoss.zk:zkplus:3.6.2`

Cobertura:
- `org.jacoco:jacoco-maven-plugin:0.8.8`
- Gate minimo de linha: `80%`
- Escopo: `dao`, `service`, `util`

Infra de apoio:
- `src/test/java/br/gov/inep/censo/support/TestDatabaseSupport.java`
- scripts `schema.sql`, `seed.sql`, `seed_layout.sql`, `seed_layout_ies_docente.sql`, `seed_municipio.sql`

## 2. Camada 1 - Unitarios

Classes principais:
- `ValidationUtilsTest`
- `PasswordUtilTest`
- `RequestFieldMapperTest`
- `EnumMappingTest`
- `CursoAlunoServiceTest`

Cobertura:
- validacoes de CPF/periodo/semestre;
- hash e compatibilidade de senha;
- mapeamento de campos dinamicos;
- validacoes de negocio do Registro 42.

## 3. Camada 2 - Integracao

### 3.1 DAO + JPA

- `AlunoDAOTest`
- `CursoDAOTest`
- `DocenteDAOTest`
- `IesDAOTest`
- `MunicipioDAOTest`

Cobertura:
- CRUD, paginacao, contagem;
- relacionamento e tabelas auxiliares 1..N;
- campos complementares por layout;
- consistencia UF/municipio.

### 3.2 Service + banco

- `AlunoServiceTest`
- `CursoServiceTest`
- `DocenteServiceTest`
- `IesServiceTest`
- `CursoAlunoServiceTest`
- `AuthServiceTest`
- `CatalogoServiceTest`

Cobertura:
- importacao/exportacao TXT pipe;
- validacoes de cadastro;
- autenticacao e catalogos.

### 3.3 Filtros web

- `AuthFilterTest`
- `CsrfFilterTest`

## 4. Camada 3 - E2E

Classe:
- `CensoE2ETest` (mantida com `@Ignore`)

Fluxo alvo da UI ZK (shell + modal):
1. abrir `/login.zul`;
2. autenticar (`admin/admin123`);
3. validar redirecionamento para `/app/menu.zul?view=dashboard`;
4. navegar para `Aluno` pelo menu lateral;
5. abrir sub-window de cadastro (`sub=aluno-form`);
6. preencher e salvar;
7. validar feedback na listagem (`Aluno incluido com sucesso.`).

## 5. Roteiro de execucao

Build + testes:
```bash
mvn '-Dmaven.compiler.source=1.7' '-Dmaven.compiler.target=1.7' test
```

Para executar apenas E2E (quando habilitado):
```bash
mvn -De2e.baseUrl=http://localhost:8080/censo-superior-2025 -Dtest=CensoE2ETest test
```

## 6. Estrutura de testes

```text
src/test/java/
└─ br/gov/inep/censo/
   ├─ support/
   ├─ util/
   ├─ model/enums/
   ├─ dao/
   ├─ service/
   ├─ web/filter/
   └─ e2e/
```

## 7. Gates de migracao por fase

1. Fase 1 (Java 8): suite de testes atual + smoke manual de login/menu/CRUD sem regressao.
2. Fase 2 (ZK 8 + Bootstrap): smoke de navegacao e renderizacao em todas as telas `.zul` criticas.
3. Fase 3 (Spring Boot): testes de service/repository + validacao da cadeia de seguranca.
4. Fase 4 (MVVM): regressao funcional dos modulos migrados (com foco em comandos/bindings).
