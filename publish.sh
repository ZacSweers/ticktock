#!/usr/bin/env sh

./gradlew clean uploadArchives --no-parallel
cd ticktock-gradle-plugin
./gradlew uploadArchives
cd ..