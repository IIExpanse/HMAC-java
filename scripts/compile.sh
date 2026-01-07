#!/bin/sh

set -e

rm -rf out

libs=$(find lib -name "*.jar" -printf "%p;")

javac -d 'out/production' -cp "$libs" $(find src -name "*.java")
cp -r resources/* out/production
javac -d 'out/test' -cp "out/production;$libs" $(find test -name "*.java")
cp -r test-resources/* out/test