# Meep User Guide

Meep is a friendly, case-sensitive task and message manager. It supports todos, deadlines, events; listing, marking/unmarking, deleting; finding tasks; checking what’s due; and saving/loading to disk.

<!-- Optional screenshot placeholder
![Meep GUI](./images/screenshot.png)
-->

## Prerequisites

- Java Development Kit (JDK) 17 or newer
- Internet access for Gradle to download dependencies on first run

## Quick start

Build the project:

```bash
./gradlew build
```

Run the GUI (JavaFX):

```bash
./gradlew run
```

Run the CLI (headless-friendly):

```bash
./gradlew runCli
```

Build a fat JAR and run it manually:

```bash
./gradlew shadowJar
java -jar build/libs/meep-all.jar
```

Tip: In IDEs, run `meep.gui.Launcher` for the GUI or `meep.ui.Meep` for the CLI.

## How to use Meep (commands)

Commands are case-sensitive. Extra whitespace is tolerated. Date input format is `yyyy-MM-dd`.

- hello
	- Example → `hello`
	- Output → `Hello there!`

- how are you?
	- Example → `how are you?`

- list messages
	- Shows the raw inputs Meep has received this session.

- list
	- Lists all tasks with 1-based indices and the total count.

- todo <description>
	- Example → `todo buy milk`

- deadline <description> /by <date>
	- Example → `deadline submit report /by 2025-12-31`

- event <description> /from <start-date> /to <end-date>
	- Example → `event conference /from 2025-04-21 /to 2025-04-23`

- mark <n>
	- Marks task number n as done (1-based index). If the number cannot be parsed, Meep prints `Invalid task number.`

- unmark <n>
	- Marks task number n as not done.

- delete <n>
	- Deletes task number n.

- find <substring>
	- Case-sensitive search within task descriptions.

- check due <date>
	- Shows tasks due before the given date.
	- Example → `check due 2025-12-31`

- save
	- Saves tasks to `data/meep.txt`. Parent directories are created automatically if missing.

- load
	- Loads tasks from `data/meep.txt`. If the file is missing or corrupted, Meep reports an error and keeps running.

- help
	- Prints command reference with the expected date format.

- bye
	- Exits the CLI (GUI closes via window controls).

### Example session (CLI)

```
hello
todo buy milk
deadline submit report /by 2025-12-31
event conference /from 2025-04-21 /to 2025-04-23
list
mark 2
find report
check due 2025-12-31
save
bye
```

## Persistence

- Default file: `data/meep.txt`
- The file is created on save if it doesn’t exist.
- Save/Load report success or a generic error. If a save fails, check file permissions or disk space.

## Troubleshooting

- JavaFX doesn’t launch:
	- Ensure a desktop environment is available, or use the CLI via `./gradlew runCli`.
- Date parsing fails:
	- Use `yyyy-MM-dd` (e.g., `2025-12-31`).
- “Invalid task number.” printed:
	- The value after `mark`/`unmark`/`delete` wasn’t a number. Use a 1-based integer.
- Storage issues:
	- Verify write permissions for the project folder. Meep writes to `data/meep.txt`.

## Developer docs

Generate JavaDocs:

```bash
./gradlew javadoc
```

Then open `build/docs/javadoc/index.html` in a browser.

For testing and debugging guidance, see `docs/TESTING.md`.