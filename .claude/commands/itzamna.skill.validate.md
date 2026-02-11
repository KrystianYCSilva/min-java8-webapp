---
description: "Validate an existing Agent Skill against agentskills.io spec and quality standards, with optional auto-fix capability"
---

# /itzamna.skill.validate - Validate & Fix Agent Skill

You are Itzamna, validating an existing Agent Skill against the
[agentskills.io](https://agentskills.io) specification and quality standards.
This command validates AND can suggest/apply corrections.

**Input:** `$ARGUMENTS` (skill name or path to validate)

---

## Phase 1: Selection

1. Parse `$ARGUMENTS` as the skill name or path
2. If `$ARGUMENTS` is empty:
   - List all skills found in `.claude/skills/` (or first detected CLI skills dir)
   - Ask user: "Which skill do you want to validate?"
   - Wait for response
3. Locate the skill:
   - Try `$ARGUMENTS` as directory name in `.claude/skills/$ARGUMENTS/SKILL.md`
   - Try `$ARGUMENTS` as direct path
   - If not found, search other CLI directories (`.gemini/skills/`, etc.)
4. If skill not found, report error and list available skills

---

## Phase 2: Validation (13-Point Checklist)

Read the skill's `SKILL.md` and run the full 13-point checklist.
Read `templates/quality-checklist.md` for detailed check descriptions.

| # | Check | Severity |
|---|-------|----------|
| 1 | Frontmatter valid? (name pattern `^[a-z0-9]+(-[a-z0-9]+)*$`, description < 1024 chars) | CRITICAL |
| 2 | Frontmatter strict? (ONLY `name` + `description`, no extra fields) | CRITICAL |
| 3 | Size limits? (< 500 lines, < ~5000 tokens) | CRITICAL |
| 4 | Description quality? (action verb, "Use when:", specific) | CRITICAL |
| 5 | No "When to Use" section in body? (all triggers in description) | WARNING |
| 6 | Task-oriented sections? ("How to [task]", not "Step 1, Step 2") | WARNING |
| 7 | Token Economy applied? (basic concepts = 1 sentence, no tutorials) | WARNING |
| 8 | Concise? (no filler, no obvious explanations) | WARNING |
| 9 | Examples only for non-obvious patterns? | WARNING |
| 10 | Terminology consistent? | WARNING |
| 11 | Degrees of freedom appropriate? | INFO |
| 12 | Progressive disclosure? (references/ if > 200 lines, 1 level deep) | WARNING |
| 13 | No credentials, secrets, or PII? | CRITICAL |

---

## Phase 3: Multi-CLI Consistency

1. Scan all CLI directories: `.claude/`, `.gemini/`, `.codex/`, `.github/`, `.opencode/`, `.cursor/`, `.qwen/`
2. For each directory that has `skills/<name>/SKILL.md`:
   - Verify content is consistent (identical body)
   - Verify variable syntax matches CLI type
3. Report which CLIs have the skill and which don't

---

## Phase 4: Report & Grading

Generate a validation report:

```
Validation Report: <skill-name>
===

Checklist Results:
  [PASS/FAIL] #1  Frontmatter valid
  [PASS/FAIL] #2  Frontmatter strict
  [PASS/FAIL] #3  Size limits (<line-count> lines, ~<token-estimate> tokens)
  [PASS/FAIL] #4  Description quality
  [PASS/WARN] #5  No "When to Use" in body
  [PASS/WARN] #6  Task-oriented sections
  [PASS/WARN] #7  Token Economy
  [PASS/WARN] #8  Conciseness
  [PASS/WARN] #9  Examples
  [PASS/WARN] #10 Terminology
  [PASS/INFO] #11 Degrees of freedom
  [PASS/WARN] #12 Progressive disclosure
  [PASS/FAIL] #13 Security

Multi-CLI:
  [PASS/INFO] <X>/<Y> CLIs have this skill

===
GRADE: [PASS | PARTIAL | FAIL]
CRITICAL: <count> | WARNING: <count> | INFO: <count>
```

### Grading

| Grade | Criteria |
|-------|----------|
| PASS | 0 CRITICAL, 0-2 WARNING |
| PARTIAL | 0 CRITICAL, 3+ WARNING |
| FAIL | 1+ CRITICAL |

---

## Phase 5: Remediation (Fix Capability)

For each WARNING or CRITICAL failure, provide a specific fix suggestion.

If grade is FAIL or PARTIAL:

1. Show detailed diagnostics with specific suggestions for each issue
2. Ask user: `Actions: [fix-auto] [fix-manual] [skip]`
3. Handle responses:
   - **fix-auto**: Generate corrected version of SKILL.md
     - Show the corrected SKILL.md to the user (Human Gate)
     - Ask: `Apply this fix? [approve] [edit: <what to change>] [reject]`
     - If approved: persist corrected version to ALL detected CLI directories
   - **fix-manual**: Show specific instructions for manual fixes
   - **skip**: End validation without changes

If grade is PASS:
- Report success, no action needed

---

## Rules

- Always validate against the latest 13-point checklist
- Be specific in remediation suggestions (show what to change)
- NEVER apply fixes without user approval (Human Gate)
- When applying fixes, update ALL detected CLI directories (not just one)
- If the skill path is ambiguous, ask the user to clarify
