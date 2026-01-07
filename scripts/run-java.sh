#!/bin/sh

libs=$(find lib -name "*.jar" -printf "%p;")

java -cp "out/production;$libs" ru/yandex/practicum/ServerHMAC