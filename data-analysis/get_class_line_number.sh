#!/usr/bin/env bash
grep -nrE "^((public|protected|private) )?class " --include=*.java . \
  | while read -r line ; do
    fileName=$(echo $line | grep -ohP ".+:" | cut -d : -f 1)
    lineNumber=$(echo $line | grep -ohP ":(\d+):" | cut -d : -f 2)
    numberOfLinesInFile=$(cat $fileName | wc -l)
    relativePosition=$(bc <<<"scale=2; $lineNumber / $numberOfLinesInFile")
    printf $relativePosition,
done
# Force newline for easier copy-pasting into Python histogram computation
echo
