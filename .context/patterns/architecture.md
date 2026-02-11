---
description: Blueprint da arquitetura do sistema com camadas, fluxo de request, responsabilidades e padroes aplicados.
tier: T1
triggers:
  - blueprint
  - arquitetura
  - fluxo
  - migracao
  - springboot
  - mvvm
  - zk
  - jpa
last_updated: 2026-02-11
---
# Blueprint de Arquitetura

## Estilo geral

Aplicacao monolitica web em arquitetura em camadas, orientada a casos de uso de cadastro e importacao/exportacao para Censo Superior.

Modulos ativos:

1. Aluno (Registro 41)
2. Curso (Registro 21)
3. CursoAluno (Registro 42)
4. Docente (Registro 31)
5. IES (Registro 11)

## Fluxo principal de request

```text
HTTP Request
  -> AuthFilter (quando rota /app/*)
  -> Pagina ZUL (*.zul)
  -> Composer MVC (ZK)
  -> Service (regra de negocio)
  -> DAO (JPA/Hibernate)
  -> AbstractJpaDao (EntityManager)
  -> H2 / tabelas
  -> Service
  -> Composer
HTTP Response (renderizacao ZK AU/HTML)
```

## Padroes adotados

1. DAO Pattern para isolamento de persistencia.
2. Service Layer para invariantes de negocio.
3. Template transacional de persistencia via JPA (`AbstractJpaDao`).
4. Filter para controle de autenticacao de sessao.
5. Builder Pattern para montagem de entidades extensas.
6. Password hardening com PBKDF2 e migracao progressiva de hashes legados.
7. MVC Composer Pattern no frontend ZK 3.6.2.
8. Shell Pattern para layout unico (`header/sidebar/center/footer`) com includes dinamicos.

## Fronteiras de responsabilidade

1. `web/zk`: navegacao e interacao de interface.
2. `service`: validacao, consistencia e orquestracao.
3. `dao`: persistencia JPA, query e mapeamento.
4. `model`: representacao de entidades de dominio.
5. `util`: hash, validacao e mapeamentos auxiliares.

## Pontos de atencao arquitetural

1. Fluxos de import/export devem manter consistencia entre modelo e metadados de layout.
2. Mudancas de schema impactam DAO, service e testes em cascata.
3. Dependencias devem ser unidirecionais para evitar acoplamento ciclico.
4. Validacao de UF/municipio em `DocenteService` e `IesService` depende da tabela `municipio`.
5. Pool interno do Hibernate e aceitavel para desenvolvimento, mas nao para producao.
6. Navegacao de modal depende dos parametros `view/sub/id` no shell.

## Evolucao planejada

1. Curto prazo: migracao Java 8 mantendo arquitetura funcional atual.
2. Medio prazo: upgrade ZK e frontend sem trocar o padrao MVC.
3. Proximo passo: Spring Boot + Spring Data com coexistencia temporaria DAO/Repository.
4. Etapa final: migracao de tela para MVVM.
