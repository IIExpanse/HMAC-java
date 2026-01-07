#!/bin/sh

set -e

content='Class-Path: lib/gson-2.8.7.jar .
Main-Class: ru.yandex.practicum.ServerHMAC'
touch MANIFEST.MF
mkdir -p out/production/META-INF
mv MANIFEST.MF out/production/META-INF/MANIFEST.MF
echo "$content" > out/production/META-INF/MANIFEST.MF

mkdir -p app/lib
cp  lib/gson-2.8.7.jar app/lib

jar cfm app/HMAC-app.jar out/production/META-INF/MANIFEST.MF -C out/production/ .