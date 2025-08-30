# Meep

Meep is a simple CLI task manager built with Java 17 and Gradle. It supports adding todo/deadline/event tasks, marking/unmarking, deleting, listing, searching (find), saving/loading, and due checks.

## Setting up in IntelliJ IDEA

Prerequisites: JDK 17, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 17** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/meep/ui/Meep.java` file, right-click it, and choose `Run Meep.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see the Meep greeting.
   ```
   Hello from
    ____        _        
   |  _ \ _   _| | _____ 
   | | | | | | | |/ / _ \
   | |_| | |_| |   <  __/
   |____/ \__,_|_|\_\___|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location tools (e.g., Gradle) expect to find Java files.

## Build, run, and test

- Build and run:
   - Use your IDE run configuration for `meep.ui.Meep`, or build a fat jar with `./gradlew shadowJar` and run it from `build/libs`.
- Run unit tests: `./gradlew test`
- Apply formatting: `./gradlew spotlessApply` (checks run on `./gradlew check`)

## Text UI test

Use the scripts under `text-ui-test/`:
- Linux/macOS: `text-ui-test/runtest.sh`
- Windows: `text-ui-test/runtest.bat`
