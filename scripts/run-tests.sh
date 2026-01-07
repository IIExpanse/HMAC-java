#!/bin/sh

libs=$(find lib -name "*.jar" -printf "%p;")
jar_path='junit-console/junit-platform-console-standalone-6.0.2.jar'

java -jar "$jar_path" execute --class-path "resources;test-resources;out/test;out/production;$libs" --select-package=ru.yandex.practicum