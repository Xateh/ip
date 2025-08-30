@ECHO OFF

SET JAVAC="pixi run javac"
SET JAVA="pixi run java"

REM create bin directory if it doesn't exist
if not exist ..\bin mkdir ..\bin

REM delete output from previous run
if exist ACTUAL.TXT del ACTUAL.TXT

REM take flag for prefix
if "%1" == "--prefix" (
    set JAVAC="%~2 javac"
    set JAVA="%~2 java"
)

REM compile the code into the bin folder (all Java files recursively)
for /r ..\src\main\java %%f in (*.java) do (
    set "SOURCES=!SOURCES! %%f"
)
call %JAVAC% -cp ..\src\main\java -Xlint:none -d ..\bin %SOURCES%
IF ERRORLEVEL 1 (
    echo ********** BUILD FAILURE **********
    exit /b 1
)
REM no error here, errorlevel == 0

REM run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
call %JAVA% -classpath ..\bin meep.ui.Meep < input.txt > ACTUAL.TXT

REM compare the output to the expected output
FC ACTUAL.TXT EXPECTED.TXT
