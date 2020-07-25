#!/usr/bin/env sh

./gradlew clean uploadArchives -Dorg.gradle.parallel=false
cd ticktock-gradle-plugin
./gradlew uploadArchives
cd ..