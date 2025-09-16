# 🚀 Claude Code Assistant - Ultimate Instructions Guide

## 📋 Table of Contents
- [Philosophy & Principles](#philosophy--principles)
- [Quick Start](#quick-start)
- [Core Tools](#core-tools)
- [Project Setup Standards](#project-setup-standards)
- [Essential Workflows](#essential-workflows)
- [Code Operations](#code-operations)
- [AI Pair Programming Guidelines](#ai-pair-programming-guidelines)
- [Testing & Quality](#testing--quality)
- [Documentation Standards](#documentation-standards)
- [Version Control](#version-control)
- [Performance & Optimization](#performance--optimization)
- [Troubleshooting](#troubleshooting)
- [Command Reference](#command-reference)

---

## 🎨 Philosophy & Principles

### Core Values
```markdown
1. **Clarity Over Cleverness** - Write code that's easy to understand
2. **Test-Driven Development** - Test first, code second
3. **Incremental Progress** - Small, verified changes
4. **Documentation as Code** - Keep docs in sync with implementation
5. **Fail Fast, Learn Faster** - Quick feedback loops
```

### Working with Claude Code
```markdown
✨ Claude Code is your pair programmer who:
- Understands context and intent
- Provides up-to-date solutions (via Context7)
- Navigates code semantically (via Serena)
- Maintains consistency across sessions (via memories)
- Executes and verifies changes
```

---

## 🎯 Quick Start

### Initial Setup (Run at Session Start)
```bash
# 1. Initialize Serena
/mcp__serena__initial_instructions

# 2. Activate your project
Activate the project /path/to/project

# 3. Get project overview
get_symbols_overview

# 4. Check project health
execute_shell_command "npm test"
execute_shell_command "git status"
```

### Essential Commands for Common Tasks
```markdown
📚 Need latest docs? → Add "use context7" to prompt
🔍 Exploring code? → Use find_symbol or search_for_pattern
✏️ Making changes? → Check find_referencing_symbols first
✅ Testing changes? → Run execute_shell_command for tests
💾 Saving progress? → Use write_memory for important info
```

---

## 🛠️ Core Tools

### Context7 MCP Server
**Purpose:** Real-time documentation and examples

**When to Use:**
```markdown
✅ External libraries (React, Vue, Next.js, etc.)
✅ Latest API changes and features
✅ Framework-specific best practices
✅ Security updates and deprecations
```

**Usage Examples:**
```
"Implement Auth0 in Next.js 15 with app router. use context7"
"Latest React 19 performance optimizations. use context7"
"Prisma ORM best practices 2025. use context7"
```

### Serena MCP Server
**Purpose:** Intelligent code analysis and editing

**Core Capabilities:**
```markdown
🔍 Semantic Search - Understanding code meaning
🎯 Symbol Navigation - Jump to definitions
🔗 Dependency Analysis - Track relationships
✂️ Smart Refactoring - Context-aware changes
📊 Project Intelligence - Holistic understanding
```

**Dashboard:** http://localhost:24282/dashboard

---

## 📁 Project Setup Standards

### Directory Structure Best Practices
```markdown
project/
├── src/               # Source code
│   ├── components/    # Reusable components
│   ├── features/      # Feature modules
│   ├── hooks/         # Custom hooks
│   ├── utils/         # Utilities
│   ├── types/         # TypeScript types
│   └── tests/         # Test files
├── docs/              # Documentation
├── scripts/           # Build/deploy scripts
└── CLAUDE.md          # This file - AI instructions
```

### Initial Project Analysis
```markdown
1. Structure Overview:
   - list_dir "." recursive
   - get_symbols_overview on src/

2. Identify Key Files:
   - find_symbol "App" or "main"
   - read_file "package.json"
   - read_file "tsconfig.json" or ".eslintrc"

3. Understand Architecture:
   - Check for design patterns
   - Identify state management
   - Review routing structure

4. Document Findings:
   - write_memory "project_architecture" "[summary]"
```

---

## 📝 Essential Workflows

### 1️⃣ Understanding Requirements
```markdown
Before coding:
1. Clarify requirements completely
2. Break down into smaller tasks
3. Identify affected components
4. Plan test scenarios
5. Consider edge cases
```

### 2️⃣ Feature Development Workflow
```markdown
1. Research Phase:
   - "How to implement [feature] in [tech]? use context7"
   - find_symbol for existing similar features
   - Review project conventions

2. Planning Phase:
   - switch_modes planning
   - Design component/module structure
   - Identify dependencies
   - write_memory "feature_plan" "[details]"

3. Implementation Phase:
   - switch_modes editing
   - Create test file first (TDD)
   - Implement feature incrementally
   - Use replace_symbol_body for updates

4. Integration Phase:
   - Update imports/exports
   - Wire up with existing code
   - Update documentation

5. Verification Phase:
   - Run all tests
   - Check linting
   - Review with git diff
   - Manual testing if needed
```

### 3️⃣ Bug Fixing Workflow
```markdown
1. Reproduce & Understand:
   - Get error details from user
   - search_for_pattern with error message
   - find_symbol for problematic code
   - find_referencing_code_snippets for context

2. Diagnose:
   - Read implementation
   - Check recent changes (git log)
   - Identify root cause
   - Consider side effects

3. Fix:
   - Write failing test first
   - Apply minimal fix
   - Verify test passes
   - Check for regressions

4. Document:
   - Add comments if complex
   - Update relevant docs
   - write_memory about the fix
```

### 4️⃣ Refactoring Workflow
```markdown
1. Preparation:
   - Ensure all tests pass
   - Create git branch
   - Document current behavior

2. Analysis:
   - find_referencing_symbols
   - Map all dependencies
   - Identify test coverage

3. Refactor:
   - Make incremental changes
   - Run tests after each change
   - Preserve public API

4. Cleanup:
   - Remove dead code
   - Update documentation
   - Optimize imports
```

### 5️⃣ Code Review Workflow
```markdown
1. Overview:
   - git diff for all changes
   - Check against requirements

2. Deep Review:
   - Logic correctness
   - Edge cases handled
   - Performance implications
   - Security considerations

3. Style & Standards:
   - Naming conventions
   - Code formatting
   - Documentation completeness

4. Testing:
   - Test coverage adequate
   - Tests are meaningful
   - All tests passing
```

---

## 💻 Code Operations

### Search Strategies
```markdown
🎯 Precision Search:
find_symbol "exactName"           # Exact symbol
find_symbol "partial" fuzzy       # Fuzzy matching

🔍 Pattern Search:
search_for_pattern "TODO|FIXME"   # Find tasks
search_for_pattern "console\."    # Find debug code
search_for_pattern "@deprecated"  # Find deprecated

🔗 Dependency Search:
find_referencing_symbols          # Who uses this?
find_referencing_code_snippets    # How is it used?
```

### Edit Strategies
```markdown
✏️ Smart Replacements:
replace_symbol_body               # Full function/class
replace_lines 10 20              # Specific lines

➕ Smart Insertions:
insert_before_symbol             # Add imports, decorators
insert_after_symbol              # Add related functions
insert_at_line                   # Precise placement

🗑️ Smart Deletions:
delete_lines 5 10                # Remove blocks
replace_symbol_body with ""      # Remove symbol
```

---

## 🤝 AI Pair Programming Guidelines

### Communication Patterns
```markdown
1. Be Specific:
   ❌ "Make it better"
   ✅ "Optimize this function for performance, focusing on reducing database calls"

2. Provide Context:
   ❌ "Fix the bug"
   ✅ "Users report the login fails when email contains '+'. Check validation in auth module"

3. Iterative Refinement:
   - Start with working solution
   - Refine incrementally
   - Test each iteration
```

### Effective Prompting
```markdown
Structure: [Action] + [Target] + [Context] + [Constraints]

Examples:
"Refactor UserService class to use dependency injection, maintaining backward compatibility"
"Add error handling to all API endpoints, following our existing ErrorHandler pattern"
"Create React component for data table with sorting, filtering, using our design system"
```

### Task Delegation
```markdown
✅ Good for Claude:
- Boilerplate generation
- Pattern implementation
- Test creation
- Documentation
- Refactoring
- Bug investigation

⚠️ Verify Claude's Work:
- Business logic
- Security implementations
- Performance optimizations
- Database migrations
- Third-party integrations

❌ Human Decision Required:
- Architecture decisions
- Business requirements
- UX/UI design choices
- Production deployments
```

---

## 🧪 Testing & Quality

### Test-Driven Development
```markdown
1. Write Test First:
   - Define expected behavior
   - Cover edge cases
   - Make it fail

2. Implement Minimum Code:
   - Just enough to pass
   - Keep it simple
   - Avoid over-engineering

3. Refactor:
   - Improve code quality
   - Maintain test passing
   - Optimize if needed
```

### Testing Checklist
```markdown
☐ Unit tests for functions/methods
☐ Integration tests for features
☐ Edge cases covered
☐ Error scenarios tested
☐ Performance tests for critical paths
☐ Accessibility tests for UI
☐ Security tests for sensitive operations
```

### Code Quality Standards
```markdown
execute_shell_command "npm run lint"      # Style check
execute_shell_command "npm run test"      # Test suite
execute_shell_command "npm run coverage"  # Coverage report
execute_shell_command "npm audit"         # Security check
```

---

## 📚 Documentation Standards

### Code Documentation
```markdown
1. Function/Method Docs:
   - Purpose and behavior
   - Parameters with types
   - Return value
   - Exceptions thrown
   - Usage examples

2. Complex Logic:
   - Why, not just what
   - Business rules
   - Algorithm explanation
   - Performance notes

3. Module/Component:
   - High-level purpose
   - Public API
   - Dependencies
   - Configuration
```

### Project Documentation
```markdown
README.md:
- Project overview
- Quick start guide
- Development setup
- Architecture overview
- Contributing guidelines

CLAUDE.md (this file):
- AI assistant instructions
- Project-specific patterns
- Tool configurations
- Workflow definitions
```

---

## 🔄 Version Control

### Git Workflow
```markdown
1. Before Starting:
   git status                    # Clean state
   git pull                      # Latest changes
   git checkout -b feature/name  # New branch

2. During Development:
   git add -p                    # Review changes
   git commit -m "type: message" # Clear commits
   git push                      # Backup work

3. After Completion:
   git diff main                 # Review all
   git rebase main              # Clean history
   Create pull request          # Code review
```

### Commit Message Format
```markdown
type(scope): subject

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- style: Formatting
- refactor: Code restructuring
- test: Test changes
- chore: Maintenance

Example:
"feat(auth): add OAuth2 integration with Google"
```

---

## ⚡ Performance & Optimization

### Code Performance
```markdown
1. Measure First:
   - Profile before optimizing
   - Identify bottlenecks
   - Set performance goals

2. Common Optimizations:
   - Reduce database queries
   - Implement caching
   - Lazy loading
   - Code splitting
   - Memoization

3. Verify Improvements:
   - Benchmark changes
   - Monitor metrics
   - Test under load
```

### Claude Performance
```markdown
🚀 Speed Tips:
- Index large projects first
- Use symbolic navigation
- Batch related operations
- Cache findings in memory
- Minimize file reading

💾 Memory Management:
- Write key findings regularly
- Summarize before context full
- Use prepare_for_new_conversation
- Maintain session continuity
```

---

## 🔧 Troubleshooting

### Common Issues Matrix

| Issue | Diagnosis | Solution |
|-------|-----------|----------|
| Symbol not found | Outdated index | restart_language_server |
| Tests failing | Dependencies changed | find_referencing_symbols |
| Slow performance | Large codebase | Index project first |
| Context full | Too much in memory | prepare_for_new_conversation |
| Outdated docs | Old training data | Add "use context7" |
| Git conflicts | Parallel changes | Rebase and resolve |
| Build errors | Missing dependencies | npm install / check imports |

### Debug Protocol
```markdown
1. Gather Information:
   - Error messages
   - Stack traces
   - Recent changes
   - Environment details

2. Isolate Problem:
   - Reproduce consistently
   - Narrow down scope
   - Check assumptions

3. Fix & Verify:
   - Apply minimal fix
   - Test thoroughly
   - Document solution
```

---

## 📊 Command Reference

### Quick Command Matrix

| Category | Command | Purpose |
|----------|---------|---------|
| **Setup** | /mcp__serena__initial_instructions | Initialize Serena |
| | Activate the project [path] | Start working on project |
| **Search** | find_symbol "name" | Find definitions |
| | search_for_pattern "regex" | Pattern search |
| | find_referencing_symbols | Find usages |
| **Read** | read_file "path" | View file content |
| | get_symbols_overview | Structure overview |
| | list_dir "path" | Browse files |
| **Edit** | replace_symbol_body | Replace entire symbol |
| | insert_at_line N | Insert at line |
| | replace_lines N M | Replace line range |
| **Execute** | execute_shell_command "cmd" | Run commands |
| **Memory** | write_memory "key" "value" | Save information |
| | read_memory "key" | Retrieve information |
| | list_memories | View all memories |
| **Context** | prepare_for_new_conversation | Clean context |
| | summarize_changes | Create summary |
| **Mode** | switch_modes [mode] | Change operation mode |

---

## 🎨 Serena Modes

| Mode | Characteristics | Best For |
|------|----------------|----------|
| **planning** | Thoughtful, analytical | Architecture, design |
| **editing** | Focused, precise | Code modifications |
| **interactive** | Responsive, flexible | Pair programming |
| **one-shot** | Complete, comprehensive | Single tasks |

---

## 💎 Golden Rules of Claude Code

### The 10 Commandments
```markdown
1. Always verify before modifying
2. Test continuously, not eventually  
3. Document why, not just what
4. Use Context7 for external, Serena for internal
5. Commit early, commit often
6. Refactor mercilessly, but safely
7. Memory is your friend, use it wisely
8. Clean code > clever code
9. Every error is a learning opportunity
10. When in doubt, ask for clarification
```

### Success Metrics
```markdown
✅ All tests passing
✅ No linting errors
✅ Documentation updated
✅ Code reviewed
✅ Performance acceptable
✅ Security checked
✅ Accessibility verified
✅ Knowledge transferred (memories)
```

---

## 🚀 Advanced Techniques

### Multi-Tool Workflows
```markdown
Example: Full-Stack Feature
1. Backend: "REST API best practices 2025. use context7"
2. Database: find_symbol for existing models
3. Frontend: "React Query v5 patterns. use context7"
4. Integration: Test end-to-end flow
5. Documentation: Update all relevant docs
```

### Complex Refactoring
```markdown
1. Create refactoring plan
2. Set up comprehensive tests
3. Use git branches for experiments
4. Refactor in small steps
5. Verify after each step
6. Merge when fully complete
```

### Performance Optimization
```markdown
1. Profile with appropriate tools
2. Identify bottlenecks
3. Research solutions with Context7
4. Implement optimizations
5. Measure improvements
6. Document changes
```

---

## 📈 Continuous Improvement

### Session Review
```markdown
After each session:
1. What worked well?
2. What could improve?
3. Update memories with learnings
4. Refine workflows
5. Update this document
```

### Knowledge Building
```markdown
- Save patterns that work
- Document project quirks
- Build command shortcuts
- Create code templates
- Maintain best practices
```

---

## 🔗 Quick Links & Resources

### Essential Resources
```markdown
- Serena Dashboard: http://localhost:24282/dashboard
- Context7 Docs: Available via "use context7"
- Project Repo: [Your repository URL]
- Team Guidelines: [Your team docs]
- Design System: [Your design docs]
```

### Emergency Procedures
```markdown
If things go wrong:
1. git stash - Save current work
2. git checkout main - Return to stable
3. restart_language_server - Reset Serena
4. prepare_for_new_conversation - Clear context
5. Start fresh with memories
```

---

## 🎯 Project-Specific Configuration

> 💡 Add your project-specific rules below:

```markdown
### Project: [Your Project Name]

Tech Stack:
- Frontend: [e.g., React, Vue, Angular]
- Backend: [e.g., Node.js, Python, Go]
- Database: [e.g., PostgreSQL, MongoDB]
- Testing: [e.g., Jest, Pytest]

Conventions:
- Code style: [Your style guide]
- Branch naming: [Your convention]
- PR process: [Your workflow]

Special Instructions:
- [Any project-specific rules]
- [Performance requirements]
- [Security considerations]
```

---

*Last Updated: 2025 | Claude Code Assistant*
*Powered by Context7 & Serena MCP Servers*