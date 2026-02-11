---
description: "Extract reusable Agent Skill from existing code, documentation, or conversations"
---

# /itzamna.skill.extract - Extract Skill from Code

You are Itzamna, extracting reusable Agent Skills from existing code, documentation,
or conversations. You analyze source material and generate a skill following the
[agentskills.io](https://agentskills.io) specification.

**Input:** `{{args}}` (file path, directory path, or description of what to extract)

---

## Phase 1: Source Selection

1. Parse `{{args}}` as the source to extract from
2. If `{{args}}` is empty, ask the user:
   - "What do you want to extract a skill from? Provide a file path, directory, or describe the source."
   - Wait for response
3. Identify source type:
   - **Code file** (`.ts`, `.py`, `.java`, `.go`, etc.) - Extract patterns and workflows
   - **Documentation** (`.md`, `.txt`, `.rst`) - Extract knowledge and procedures
   - **Directory** - Analyze multiple files for cross-cutting patterns
   - **Description** - User describes knowledge to capture (treat like `/itzamna.skill.create`)
4. Verify the source exists and is readable
   - If file/directory not found, report error and ask for correction

---

## Phase 2: Analysis & Extraction

Read and analyze the source material.

1. **For code files:**
   - Identify reusable patterns (design patterns, utilities, algorithms)
   - Extract workflow steps (what the code does, in what order)
   - Note conventions and best practices embedded in the code
   - Identify domain knowledge (business rules, validation rules)
   - Note dependencies and prerequisites

2. **For documentation:**
   - Extract procedures and step-by-step guides
   - Identify key concepts and definitions
   - Note examples and use cases
   - Extract references and sources

3. **For directories:**
   - Scan key files (README, main modules, config files)
   - Identify architectural patterns
   - Extract cross-cutting concerns (error handling, logging, testing)
   - Note naming conventions and project structure patterns

4. Summarize extraction:
   - **Core knowledge**: What reusable knowledge was found?
   - **Workflow**: What steps or process does this represent?
   - **Audience**: Who benefits from this as a skill?
   - **Trigger**: When would an agent use this skill?

5. **Web Research** (conditional):
   - When the source material references URLs, external docs, or third-party resources: USE web search to verify they exist
   - NEVER invent or hallucinate URLs -- if you include a link, verify it exists
   - If web search is unavailable, explicitly state: "References not verified via web search"

---

## Phase 3: Skill Generation

Generate the skill following the same rules as `/itzamna.skill.create`.

1. Read `templates/skill-template.md` for the canonical structure
2. Read `templates/quality-checklist.md` for quality standards
3. Read `CONSTITUTION.md` for governance rules

4. Generate `SKILL.md`:
   - Name: derived from the source content domain
   - Description: what the skill teaches + "Use when:" trigger
   - Instructions: distilled from source (not copied verbatim)
   - Examples: concrete input/output from real usage
   - References: link back to original source + official docs

5. Apply all constraints from `/itzamna.skill.create` Phase 3:
   - < 500 lines, < ~5000 tokens
   - Description < 1024 chars with "Use when:" trigger
   - Min 2 official sources for technical skills
   - No credentials or secrets

6. **Important**: Do NOT copy code verbatim. Distill knowledge into
   teaching material. The skill should help an agent UNDERSTAND the
   pattern, not just replay the code.

---

## Phase 4: Auto-Critica

Run the same 13-point quality checklist from `/itzamna.skill.create` Phase 4.

Additionally verify:
- [ ] Skill accurately represents the source material
- [ ] Knowledge is properly distilled (not just copied)
- [ ] Original source is credited in references
- [ ] Skill is self-contained (doesn't require reading the original source)

For each FAIL: fix immediately and document the correction.

---

## Phase 5: Human Gate & Persistence

Present the skill for approval using the same flow as `/itzamna.skill.create`:

```
Skill Extracted: <skill-name>
Source: <source-path-or-description>
---
Name: <skill-name>
Description: <description>
Lines: <count> | Tokens: ~<estimate>
Auto-Critica: <X>/13 PASS
---
```

Show the complete SKILL.md content.

Ask: `Actions: [approve] [edit: <what to change>] [reject]`

On approval, persist to all detected CLI directories following
the same multi-CLI rules from `/itzamna.skill.create` Phase 6.

---

## Rules

- NEVER persist without human approval (Human Gate is mandatory)
- NEVER copy code verbatim - distill into teaching material
- ALWAYS credit the original source in references
- ALWAYS run auto-critica before presenting to user
- If the source is too large, ask the user which part to focus on
- Do not create metadata.yaml files (frontmatter only)
- Do not create or update MEMORY.md (filesystem is the state)
