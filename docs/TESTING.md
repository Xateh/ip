# Testing and Debugging Guide

This project includes comprehensive unit tests for core logic and a practical manual plan for JavaFX UI and environment checks. It also documents how to collect coverage and troubleshoot common issues.

## Automated tests

Covered areas:
- Parser routing and normalization (interactive vs. quiet modes)
- Commands: list, add, mark/unmark/delete, save/load, find, check due, bye, help
- Task model: ToDo/Deadline/Event creation, validation, due checks, serialization
- Collections: TaskList and MessageList (iteration, indexing, bounds, clear)
- Storage: save/load success, missing file, directory creation, IO failures
- Console UI wrappers (`meep.ui.Ui`, `meep.ui.Meep` methods)

Run tests:

```bash
./gradlew clean test --console=plain
```

Generate coverage (HTML + XML) and enforce the minimum threshold:

```bash
./gradlew jacocoTestReport jacocoTestCoverageVerification
```

Open coverage report: `build/reports/jacoco/html/index.html`.

## Text UI transcript test

There is a simple end-to-end transcript under `text-ui-test/` that exercises common flows:

```bash
# Linux/macOS
text-ui-test/runtest.sh

# Windows
text-ui-test/runtest.bat
```

The expected output is in `text-ui-test/EXPECTED.TXT`.

## Manual tests (GUI and environment)

1) JavaFX UI basics
- Launch via `./gradlew run` and verify:
  - Greeting appears; messages are framed and styled consistently
  - Extra spaces in input are tolerated; error messages are visibly distinct
  - Chat bubbles align correctly; avatars render circular

2) High DPI / scaling
- Check at 100%, 150%, 200% scaling. Text should remain sharp; no clipped layouts.

3) OS matrix
- Windows 10/11, macOS 13+, Ubuntu 22.04/24.04/24.04.2
- Verify save/load succeeds with default permissions; try a path containing spaces.

4) Locale & fonts
- Keep input date format `yyyy-MM-dd` regardless of OS locale.
- Verify CJK characters render properly in the UI.

5) Window sizes
- Resize from compact to full screen; ensure no overlap or overflow.

6) Keyboard navigation
- Tab through inputs and buttons; Enter submits commands.

7) Long sessions
- Use for 20+ minutes; add/delete tasks repeatedly; watch for responsiveness.

## Debugging tips

- Enable assertions when running from the CLI:

```bash
./gradlew runCli -Dorg.gradle.jvmargs="-ea"
```

- Print test failures verbosely (already configured), or narrow to a single test class:

```bash
./gradlew test --tests "meep.tool.ParserTest"
```

- Investigate storage issues:
  - Meep creates parent directories on save; if save fails, it appends a generic error and returns false.
  - Check OS permissions for the project folder and `data/` path.

- Date parsing:
  - Input must be `yyyy-MM-dd`. Use `check due 2025-12-31` to validate.

## CI notes

- The `check` lifecycle runs Spotless formatting checks, Checkstyle, tests, coverage report, and coverage verification.
- Prefer deterministic assertions (avoid current time/date unless fixed strings are used).
- If you tighten style (Checkstyle) or coverage thresholds, ensure to update failing sources/tests in the same PR.
