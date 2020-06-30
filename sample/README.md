TickTock Example
================

This project demonstrates both compiler options:
- `TzdbDemo.kt`: Demonstrates using `tzdb.dat`
- `LazyRules.kt`: Demonstrates using lazy zone rules

To update

...`tzdb.dat` -> `./gradlew syncTzDatToResources`

...granular zone rules -> `./gradlew generateLazyZoneRules`
