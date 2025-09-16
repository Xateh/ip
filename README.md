# Meep — Developer Guide

This document covers the technical details needed to work on Meep: architecture, project structure, build/test/format tasks, code style, and packaging. For user-facing instructions, see the user guide at `docs/README.md`.

## Tech stack

- Java 17 (toolchain) with Gradle 8
- JavaFX (GUI) and a minimal CLI
- JUnit 5 for tests and JaCoCo for coverage
- Spotless (formatting) and Checkstyle (style checks)

## Project structure

```
src/
  main/java/
    meep/ui/        # CLI launcher & console helpers
    meep/gui/       # JavaFX GUI entrypoint and UI classes
    meep/tool/      # Core domain: Parser, Command, Task*, Storage, Message*
  test/java/        # JUnit 5 tests
data/               # Default storage path (meep.txt)
docs/               # User & testing guides
text-ui-test/       # Transcript-based CLI test
```

Key classes:
- `meep.tool.Parser` — normalizes input and routes to commands
- `meep.tool.Command` — command pattern implementation
- `meep.tool.Task` (+ `ToDoTask`, `DeadlineTask`, `EventTask`) — task model & serialization
- `meep.tool.Storage` — save/load tasks, creates parent dirs
- `meep.ui.Meep` — CLI entry; `meep.gui.Launcher` — JavaFX entry

## Build and run

```bash
# build everything
./gradlew build

# run GUI
./gradlew run

# run CLI
./gradlew runCli

# fat jar
./gradlew shadowJar
java -jar build/libs/meep-all.jar
```

## Tests and coverage

```bash
# run tests
./gradlew test --console=plain

# coverage report + verification
./gradlew jacocoTestReport jacocoTestCoverageVerification

# open HTML report
# build/reports/jacoco/html/index.html
```

Scope highlights:
- Parser/Command flows, Task creation/validation/due checks
- Storage behaviors incl. missing file and IO failures
- Collections and console UI helpers

## Code style and formatting

Spotless enforces formatting (4-space indentation) and Checkstyle reports style warnings.

```bash
# check
./gradlew check

# auto-format
./gradlew spotlessApply
```

Notes:
- Checkstyle is configured not to fail the build on warnings (tune in `build.gradle`).
- Keep user-visible strings stable when they’re validated by tests.

## Debugging tips

- Run CLI with assertions enabled (helpful for dev):

```bash
./gradlew runCli -Dorg.gradle.jvmargs="-ea"
```

- Run a single test class or method:

```bash
./gradlew test --tests "meep.tool.ParserTest"
```

- Storage issues usually stem from permissions; Meep creates parent directories on save.
- Date input format is fixed to `yyyy-MM-dd` regardless of OS locale.

## Packaging and distribution

- The fat JAR is produced at `build/libs/meep-all.jar` via the Shadow plugin.
- Consider attaching version metadata (e.g., Git commit) for releases.

## CI and gates

- The `check` lifecycle runs: tests, coverage report + verification, Spotless, and Checkstyle.
- If you raise coverage or style strictness, update sources/tests within the same PR to keep CI green.

## Contributing

1) Create a feature branch.
2) Implement and add or update tests.
3) Run `./gradlew check` and ensure it’s green locally.
4) Open a PR with a concise description and screenshots for UI changes.

See also:
- User Guide: `docs/README.md`
- Testing & Debugging: `docs/TESTING.md`
