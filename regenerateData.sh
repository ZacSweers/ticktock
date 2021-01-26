#!/usr/bin/env bash

./gradlew clean
./gradlew :ticktock-jvm:lazyzonerules:generateLazyZoneRules
./gradlew :ticktock-jvm:tzdb:generateTzdbDat
./gradlew :ticktock-android:lazyzonerules:generateLazyZoneRules
./gradlew :ticktock-android:tzdb:generateTzdbDat
./gradlew spotlessApply
