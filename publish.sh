#!/usr/bin/env sh

./gradlew clean uploadArchives --no-parallel
cd ticktock-gradle-plugin || exit 1
./gradlew uploadArchives
cd ..