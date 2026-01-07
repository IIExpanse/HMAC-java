#!/bin/sh

set -e
docker build . -t hmac-app
docker run -p 8080:8080 hmac-app