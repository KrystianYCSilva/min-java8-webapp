---
description: Inventario tecnico com stack atual, stack alvo de migracao, dependencias e comandos de build/teste.
tier: T2
triggers:
  - stack
  - java
  - maven
  - dependencia
  - versao
  - migracao
  - java8
  - springboot
  - mvvm
  - zk
  - jpa
last_updated: 2026-02-11
---
# Stack Tecnica

## Runtime e linguagem (estado atual - baseline)

| Item | Valor |
| --- | --- |
| Linguagem principal | Java 6 (`source/target 1.6`) |
| Web | Servlet 2.5 + ZK 3.6.2 (MVC Composer) |
| Container alvo | Tomcat 6/7 |
| Banco | H2 (`com.h2database:h2:1.3.176`) |
| Persistencia | Hibernate 4.2 + JPA (`EntityManager`) |
| Empacotamento | WAR (`censo-superior-2025.war`) |

## Stack alvo por fase

| Fase | Stack alvo |
| --- | --- |
| Fase 1 (`main`) | Java 8 mantendo ZK 3.6.2 + Spring atual + MVC |
| Fase 2 (`feature/zk8-bootstrap-ui`) | Java 8 + ZK 8.6.0.1 + Bootstrap + MVC |
| Fase 3 (`feature/springboot-modernization`) | Java 8 + Spring Boot + Spring Data + Spring Security + Spring MVC |
| Fase 4 (`feature/zk-mvvm-final`) | Java 8 + ZK 8.6.0.1 + Spring Boot + MVVM |

## Dependencias principais (pom.xml)

### Producao

1. `javax.servlet:servlet-api:2.5` (provided)
2. `org.zkoss.zk:zk:3.6.2`
3. `org.zkoss.zk:zul:3.6.2`
4. `org.zkoss.zk:zhtml:3.6.2`
5. `org.zkoss.zk:zkplus:3.6.2`
6. `com.h2database:h2:1.3.176` (runtime)
7. `org.hibernate:hibernate-core:4.2.21.Final`
8. `org.hibernate:hibernate-entitymanager:4.2.21.Final`

### Teste

1. `junit:junit:4.11`
2. `org.dbunit:dbunit:2.5.4`
3. `org.mockito:mockito-all:1.10.19`
4. `org.seleniumhq.selenium:selenium-java:2.53.1`

## Plugins Maven relevantes

1. `maven-compiler-plugin:2.5.1`
2. `maven-war-plugin:2.6`
3. `maven-surefire-plugin:2.19.1`
4. `jacoco-maven-plugin:0.8.8`

## Observacoes de persistencia

1. DAOs usam `AbstractJpaDao` com `EntityManagerFactory`.
2. Entidades usam anotacoes `javax.persistence` (`@Entity`, `@Table`, etc.).
3. SQL nativo fica restrito a tabelas auxiliares (`*_opcao`, `*_layout_valor`) quando necessario.
4. Dialect e driver sao resolvidos por URL JDBC (H2/PostgreSQL/MySQL/DB2).

## Comandos usuais

### Build

```bash
mvn clean package
```

### Testes recomendados no baseline atual

```bash
mvn '-Dmaven.repo.local=.m2/repository' '-Dmaven.compiler.source=1.7' '-Dmaven.compiler.target=1.7' test
```

### Testes esperados apos Fase 1 (Java 8)

```bash
mvn clean test
```

## Gate de qualidade

`JaCoCo` exige cobertura minima de linha `0.80` para os pacotes:

1. `br/gov/inep/censo/dao/*`
2. `br/gov/inep/censo/service/*`
3. `br/gov/inep/censo/util/*`
