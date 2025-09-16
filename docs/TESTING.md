# Testing Guide

This project includes extensive automated unit tests for the non-GUI logic and a manual test plan for the JavaFX UI and environment-specific scenarios.

## Automated tests

Automated tests cover:
- Command parsing and routing (normal and quiet mode)
- Task creation, validation, due checks, and serialization round-trips
- TaskList and MessageList behaviors (iteration, indexing, clearing, bounds)
- Storage save/load, missing file, directory creation, and IO failure messaging
- Console UI wrappers (non-GUI, text-only)

Run the suite:

```bash
./gradlew clean test --console=plain
```

## Manual tests (GUI and environment)

These scenarios are difficult to automate reliably and should be verified manually:

1) JavaFX UI basics
- Launch the app and verify:
  - Greeting appears, and responses are wrapped in the styled dialog boxes
  - Typing commands in varying cases and with extra spaces still work as expected
  - Error messages are displayed with the error styling
  - Chat bubbles render asymmetrically and avatars are circular

2) High DPI / Scaling
- On Windows/macOS/Linux, verify the UI scales correctly at 100%, 150%, 200%.
- Ensure text remains sharp and layout spacing doesn’t clip.

3) Different OSes
- Windows 10/11, macOS 13+, Ubuntu 22.04/24.04
- Verify saving and loading works with default user permissions.
- Confirm file paths without special characters work by default; try a path with spaces.

4) Locale and language
- Set OS locale to English and Chinese.
- Confirm date formatting still uses the fixed patterns in the app (yyyy-MM-dd input, MMM dd yyyy output) and does not drift with locale.
- Verify messages render correctly with non-Latin fonts.

5) Screen sizes and resolutions
- Test common resolutions: 1366x768, 1920x1080, 2560x1440, 3840x2160.
- Resize window from small to large; no layouts should overflow or overlap.

6) Keyboard accessibility
- Tab through UI controls; focus order should be logical.
- Press Enter to submit commands.

7) Long sessions
- Use the app for 20+ minutes, adding and deleting tasks.
- Ensure memory usage remains stable and the UI remains responsive.

## Troubleshooting
- If storage fails:
  - Check write permissions for the configured path (see Settings or default `data/meep.txt`).
  - If directories don’t exist, the app will create them; verify parent folder permissions.
- If date parsing fails:
  - Ensure input matches `yyyy-MM-dd`. Example: `check due 2025-12-31`.
- For UI glitches:
  - Try clearing the app cache or restarting.
  - Verify JavaFX is properly initialized in your environment.

## CI notes
- Tests are written to avoid OS-specific assumptions; paths use the build folder for temp files.
- When adding new tests, prefer deterministic outputs and avoid current time/date unless explicitly fixed.
