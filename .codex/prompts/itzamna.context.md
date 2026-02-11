---
description: "Gerencia o diretorio .context/: upgrade lean->enterprise, atualizar, validar."
---

# /itzamna.context

Gerencie o diretorio `.context/` do projeto.

## Uso

```
/itzamna.context              <- Mostra status e estrutura atual
/itzamna.context check        <- Valida completude dos arquivos
/itzamna.context upgrade      <- Upgrade: Lean -> Enterprise (completo)
/itzamna.context update       <- Atualiza arquivos existentes com info do projeto
```

---

## Subcomando: (sem argumento) - Status

1. Detecte a estrutura atual:
   - **Lean**: `.context/` com 3 arquivos (project.md, tech.md, rules.md)
   - **Enterprise**: `.context/` com subdiretorios (_meta/, standards/, patterns/, knowledge/, workflows/)
   - **Ausente**: `.context/` nao existe

2. Liste todos os arquivos `.context/` e seu status:
   - **Template**: Contem placeholders `*(preencha)*` ou `*(ex:`
   - **Preenchido**: Conteudo customizado
   - **Vazio**: Arquivo existe mas esta vazio

3. Apresente:
   ```
   .context/ Status
   ===============
   Estrutura: Lean | Enterprise | Ausente
   Arquivos: X total, Y preenchidos, Z templates

   [Lista de arquivos com status]
   ```

---

## Subcomando: check

Valida a qualidade e completude dos arquivos `.context/`.

### Criterios de validacao

| Arquivo | Validacao |
|---------|-----------|
| **project.md** | Campos "What", "Why", "Scope", "Status" preenchidos? |
| **tech.md** | Stack table preenchida? Arquitetura descrita? |
| **rules.md** | Pelo menos 1 ADR registrada? |

### Processo

1. Leia cada arquivo `.context/*.md`
2. Para cada um, verifique:
   - Tem frontmatter YAML valido?
   - Tem `description` no frontmatter?
   - Conteudo preenchido (nao e template)?
   - Campos obrigatorios presentes?

3. Apresente relatorio:
   ```
   .context/ Validation Report
   ===========================
   project.md: OK | INCOMPLETE (missing: Scope)
   tech.md:    OK | TEMPLATE (not filled)
   rules.md:   OK | NO ADRs

   Overall: PASS | NEEDS ATTENTION
   ```

4. Se houver problemas, sugira:
   - Arquivos que precisam ser preenchidos
   - Campos faltantes
   - `/itzamna.context update` para preencher automaticamente

---

## Subcomando: upgrade

Converte a estrutura **Lean** para **Enterprise** (completa).

### Pre-requisitos

1. Verifique que `.context/` existe e e Lean
2. Se ja e Enterprise, informe e saia
3. Se nao existe, sugira `itzamna init` primeiro

### Processo de upgrade

1. **Backup**: Crie `.context/.backup/` com os arquivos Lean originais

2. **Crie a estrutura Enterprise**:
   ```
   .context/
   ├── README.md                 # Navigation hub
   ├── ai-assistant-guide.md     # System prompt (migra de AGENTS.md da CLI)
   ├── _meta/
   │   ├── project-overview.md   # Migra de project.md
   │   ├── tech-stack.md         # Migra de tech.md
   │   └── key-decisions.md      # Migra de rules.md (ADRs)
   ├── standards/
   │   ├── architectural-rules.md
   │   ├── code-quality.md
   │   └── testing-strategy.md
   ├── patterns/
   │   └── architecture.md
   ├── knowledge/
   │   └── domain-concepts.md
   └── workflows/
       └── deployment.md
   ```

3. **Migre conteudo**:
   - `project.md` -> `_meta/project-overview.md`
   - `tech.md` -> `_meta/tech-stack.md`
   - `rules.md` -> `_meta/key-decisions.md`
   - Preencha templates para standards/, patterns/, knowledge/, workflows/

4. **Crie README.md**:
   ```markdown
   # .context/ Navigation Hub

   ## T2: Project Identity
   - [Project Overview](_meta/project-overview.md)
   - [Tech Stack](_meta/tech-stack.md)
   - [Key Decisions](_meta/key-decisions.md) (ADRs)

   ## T1: Standards
   - [Architectural Rules](standards/architectural-rules.md)
   - [Code Quality](standards/code-quality.md)
   - [Testing Strategy](standards/testing-strategy.md)

   ## T1: Patterns
   - [Architecture](patterns/architecture.md)

   ## T3: Knowledge
   - [Domain Concepts](knowledge/domain-concepts.md)

   ## T2: Workflows
   - [Deployment](workflows/deployment.md)
   ```

5. **Apresente diff** antes de executar:
   ```
   Upgrade: Lean -> Enterprise
   ===========================
   Backup: .context/.backup/
   
   New files to create: 10
   Files to migrate: 3
   
   Approve? [yes/no]
   ```

6. **Execute apenas se aprovado pelo humano**

---

## Subcomando: update

Atualiza arquivos `.context/` com informacoes detectadas do projeto.

### Deteccao automatica

1. **Tech stack**: Analise arquivos de build/config:
   - `package.json` -> Node.js + versao
   - `pom.xml` / `build.gradle` -> Java/Kotlin + versao
   - `requirements.txt` / `pyproject.toml` -> Python + versao
   - `Cargo.toml` -> Rust + versao
   - `go.mod` -> Go + versao

2. **Estrutura do projeto**: Analise diretorios:
   - `src/` -> estrutura de pastas
   - Padroes comuns: `controllers/`, `services/`, `components/`

3. **Dependencias principais**: Leia arquivos de dependencia
   - Frameworks detectados (Spring, React, Django, etc.)
   - Databases (conexoes em configs)

### Processo

1. Escaneie o projeto e detecte:
   - Linguagem principal
   - Frameworks
   - Arquitetura (analisando estrutura de pastas)
   - Build tool

2. Leia `.context/tech.md` atual

3. Apresente diff:
   ```
   Detected vs Current
   ===================
   Language:   Java 21 (current: not filled)
   Framework:  Spring Boot 3.2 (current: not filled)
   Build:      Gradle 8.5 (current: not filled)
   
   Update tech.md with detected values? [yes/no]
   ```

4. Se aprovado, atualize `.context/tech.md` com os valores detectados

5. Mantenha valores customizados existentes (nao sobrescreva)

---

## Regras

- **NUNCA** modifique arquivos sem aprovacao humana
- **SEMPRE** crie backup antes de upgrade
- **SEMPRE** apresente diff antes de mudancas
- Upgrade e **irreversivel** (exceto via backup)
- Update e **nao-destrutivo** (preserva conteudo customizado)

---

## Exemplos

### Exemplo 1: Upgrade Lean -> Enterprise

```
Usuario: /itzamna.context upgrade

Agente:
.context/ Upgrade: Lean -> Enterprise
=====================================
Current: Lean (3 files)
Target:  Enterprise (10+ files)

Backup will be created at: .context/.backup/

New structure:
  _meta/project-overview.md    <- migrated from project.md
  _meta/tech-stack.md          <- migrated from tech.md
  _meta/key-decisions.md       <- migrated from rules.md
  standards/architectural-rules.md  <- new template
  standards/code-quality.md         <- new template
  standards/testing-strategy.md     <- new template
  patterns/architecture.md          <- new template
  knowledge/domain-concepts.md      <- new template
  workflows/deployment.md           <- new template
  README.md                         <- navigation hub

Approve? [yes/no]
```

### Exemplo 2: Update com deteccao

```
Usuario: /itzamna.context update

Agente:
Scanning project...

Detected:
- Language: Java 21 (from pom.xml)
- Framework: Spring Boot 3.2.1
- Build: Maven 3.9
- Database: PostgreSQL (from application.yml)
- Architecture: Layered (controllers/, services/, repositories/)

Current .context/tech.md is template. Update with detected values? [yes]

[Apos aprovacao]
Updated .context/tech.md with:
- Stack table filled
- Architecture: Layered (Spring)
- Dependencies: Spring Boot, PostgreSQL driver
```

---

## Referencias

- Estrutura completa: `.opencode/skills/context-engineering-basics/references/context-structure.md`
- Token optimization: `.opencode/skills/context-engineering-basics/references/optimization-techniques.md`
