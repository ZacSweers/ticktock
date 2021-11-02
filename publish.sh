#!/usr/bin/env sh

./gradlew clean publish --no-parallel
cd ticktock-gradle-plugin || exit 1
./gradlew publish
cd ..