#!/bin/sh

set -e
scripts/compile.sh
scripts/run-tests.sh
scripts/assemble.sh
scripts/dockerize.sh