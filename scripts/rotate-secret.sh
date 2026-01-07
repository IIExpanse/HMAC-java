#!/bin/sh

secret=$(openssl rand -base64 12)
sed -i  "s/\"secret\": *\".*\",/\"secret\": \"$secret\",/" 'resources/ru/yandex/practicum/context/app/config.json'