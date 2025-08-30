#!/usr/bin/env bash

JAVAC="javac"
JAVA="java"

# create bin directory if it doesn't exist
if [ ! -d "../bin" ]
then
    mkdir ../bin
fi

# delete output from previous run
if [ -e "./ACTUAL.TXT" ]
then
    rm ACTUAL.TXT
fi

# take flag for prefix
if [ "$1" == "--prefix" ]; then
    shift
    PREFIX=""
    while [ "$1" != "" ] && [[ "$1" != --* ]]; do
        PREFIX+="$1 "
        shift
    done
    PREFIX=${PREFIX%% } # remove trailing space
    JAVAC="$PREFIX $JAVAC"
    JAVA="$PREFIX $JAVA"
fi

cd $(dirname $0)

# compile the code into the bin folder, terminates if error occurred
if ! $JAVAC -cp ../src/main/java -Xlint:none -d ../bin $(find ../src/main/java -name "*.java")
then
    echo "********** BUILD FAILURE **********"
    exit 1
fi

# run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
$JAVA -classpath ../bin meep.ui.Meep < input.txt > ACTUAL.TXT

# convert to UNIX format
cp EXPECTED.TXT EXPECTED-UNIX.TXT
dos2unix ACTUAL.TXT EXPECTED-UNIX.TXT

# compare the output to the expected output
diff ACTUAL.TXT EXPECTED-UNIX.TXT
if [ $? -eq 0 ]
then
    echo "Test result: PASSED"
    exit 0
else
    echo "Test result: FAILED"
    exit 1
fi
