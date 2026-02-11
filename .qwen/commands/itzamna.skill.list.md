---
description: "List all Agent Skills installed in the project across all detected CLI directories"
---

# /itzamna.skill.list - List Agent Skills

You are Itzamna, listing all Agent Skills installed in this project.

---

## Instructions

1. Scan all CLI skill directories for installed skills:
   - `.claude/skills/`
   - `.gemini/skills/`
   - `.codex/skills/`
   - `.github/skills/`
   - `.opencode/skills/`
   - `.cursor/skills/`
   - `.qwen/skills/`

2. For each skill found (directory containing `SKILL.md`):
   - Read the YAML frontmatter from `SKILL.md`
   - Extract `name` and `description`
   - Note which CLI directories contain this skill

3. Display a formatted table:

```
Installed Skills
===

| Skill Name          | Description (truncated)              | CLIs              |
|---------------------|--------------------------------------|--------------------|
| java-fundamentals   | Guides Java development following... | claude, gemini     |
| validate-email      | Validates email addresses using...   | claude             |

Total: <count> skills across <cli-count> CLIs
```

4. If no skills found:
   ```
   No skills installed yet.

   Get started:
     /itzamna.skill.create "description of your skill"
     /itzamna.skill.extract path/to/source/file
   ```

---

## Rules

- This command is read-only: it NEVER modifies files
- Truncate descriptions to ~40 characters in the table
- Deduplicate skills that exist in multiple CLIs (show once with all CLIs listed)
- Sort alphabetically by skill name
