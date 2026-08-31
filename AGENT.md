# AI Coding Agent — Mandatory Project Operating Instructions

> Read this file completely before doing anything else in a new session. It 
> guides you for every new session by mentioning what state you must check before  
> acting, and how to use the harness correctly. Do not skip steps to save time — 
> skipping the state check is the #1 cause of duplicated or conflicting work in this 
> repo.

---


## 1. Directory Guide

Two directories live at this root and they are not the same thing. Below each directory is explained:

```
.cursor/     → the harness: agents, skills, hooks, commands, MCP configs.
            Maintained/updated by ECC. Treat as read-mostly infrastructure.
            Do not hand-edit unless you are deliberately customizing the harness.
 
.state/   → this project's memory: what's planned, what's done, what's true
            right now. THIS is what makes the project resumable across
            sessions. It is never overwritten by an ECC update/sync.
```
 
**Rule of thumb:** `.cursor/` tells an agent *how* to act. `.state/` tells an agent
*what has already happened*. Never let harness config drift into `.state/`,
and never let project state drift into `.cursor/`.

## 2. Mandatory session startup sequence

Every session — cold start or resumed — follows this order. Do not pick a
task before completing steps 1–3.
 
1. **Read `.state/DONE.md`** — append-only log of verified-complete tasks.
   This is ground truth for "is X already built." If it's not in here, it's
   not done, regardless of what the code looks like.
2. **Read `.state/PLAN.md`** — the milestone/task backlog. Cross-reference
   against `DONE.md` to find the next unblocked, not-yet-done task. 
3. Only now: pick **one** task from `PLAN.md` to work on this session. Do not
   silently pick up a second task after finishing the first — end the
   session and let the next session re-run this sequence (keeps state honest
   and context windows clean).

 ## 3. Doing the work — invoke the ECC harness correctly
 
Once a task is selected from `PLAN.md`, hand it to the ECC harness in
`.cursor/` — do not attempt the task ad hoc in the main session.

Once a task is selected from `PLAN.md`, use the appropriate
agents/commands/skills from `.cursor/` to execute the task. 

- Invoke the **correct subagent(s)** for the task type (planning, TDD,
  architecture, build-error resolution, language-specific implementation,
  etc.) so the right context and conventions load for that work. Check 
  `.ecc/` for the right entrypoint (agent, skill, or command) before starting.
- Use existing **skills** for known patterns instead of re-deriving them.
- Respect **hooks** — they exist to block dangerous operations and enforce
  conventions. Do not disable or bypass a hook to get a task to pass.
- If a task appears to require a skill/agent that doesn't exist yet, say so
  explicitly rather than improvising a one-off workaround silently.
Do all actual code changes inside the project source tree, not inside
`.ecc/` or `.state/`.
 
---
 
## 4. After ECC completes the task — update `.state/DONE.md`
 
Once the ECC harness reports the task complete and reviewed:
 
1. Append a new entry to `.state/DONE.md` — do not edit or delete prior
   entries. Each entry should include:
   - the task name/ID as it appears in `PLAN.md`
   - timestamp
   - a short note on what was verified (tests run, review outcome, etc.)
---
 
## 5. File reference
 
```
.cursor/                    ECC harness — do not hand-edit casually
.state/
  DONE.md                 append-only verified-complete task log
  PLAN.md                 master task/milestone backlog (append, don't delete)

```
 
---
 
## 6. Hard rules
 
- Never mark a task done without a real, separately-verified check.
- Never delete or rewrite history in `PLAN.md`/`DONE.md` — append only.
- Never let an ECC install/sync modify anything under `.state/`.
- Never skip the session-startup sequence in Section 2, even for "quick"
  tasks — quick tasks are exactly how state drifts out of sync.
- If something in this file conflicts with an instruction given mid-session,
  prefer this file unless the person explicitly overrides it.
 