TickTock
========

TickTock is a timezone data management library for the JVM and Android targeting `java.time.*` APIs
in Java 8 or above. Use this library if you want to bundle timezone data directly with your
application rather than rely on the current device timezones (Android) or the default `<java.home>/lib`
version (JVM only).

## Usage

### Android

Simply add the android tzdb startup dependency:

```gradle
implementation 'dev.zacsweers.ticktock:ticktock-android-tzdb:<version>'
```

This will automatically initialize it appropriately without any configuration needed using `androidx.startup`.
If you don't want automatic initialization, you can disable it and do it manually.

```xml
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">
    <meta-data android:name="dev.zacsweers.ticktock.android.tzdb.startup.AndroidTzdbRulesInitializer"
        tools:node="remove"/>
</provider>
```

```java
AndroidTzdbZoneRules.init(<context>)
```

Note that Android usage assumes use of [core library desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring).
If you are not using it and/or are minSdk 26+, this library is of no use to you!

### JVM

Add the jvm tzdb dependency:

```gradle
implementation 'dev.zacsweers.ticktock:ticktock-jvm-tzdb:<version>'
```

Then call its initializer as early as possible in your application.

```java
JvmTzdbZoneRules.init()
```

This will make `ZoneRulesProvider` use TickTock's implementation with its bundled timezone data.

## Advanced

<details>
<summary>Eager caching</summary>

TickTock's default behavior is to lazily load timezone data on-demand. If you want to eagerly
load data (for instance - on a background thread), TickTock offers a convenience helper API:

```java
// Synchronously load and cache all timezone rules
EagerZoneRulesLoading.cacheZones();
```

</details>

<details>
<summary>Custom Data Loading</summary>

By default, TickTock will try to load timezone data from Java resources via `ResourcesZoneDataLoader`.
If you wish to customize this, you can provide your own loading mechanism via implementing a custom
`ZoneDataLoader` and/or `ZoneDataProvider` and registering them via `TickTockPlugins` _before_
using any time APIs that would cause the system `ZoneRulesProvider` to initialize.

Usually, you would only want to implement a custom `ZoneDataLoader` and instantiate one of the built-in
`ZoneRulesProvider` implementations with it. TickTock comes with two: `TzdbZoneDataProvider` (the common case)
 and `LazyZoneDataProvider`. You can also implement your own provider on top of any `ZoneDataLoader`
 type as you see fit.

```java
CustomZoneDataLoader loader = new CustomZoneDataLoader();
TzdbZoneDataProvider provider = new TzdbZoneDataProvider(loader);
TickTockPlugins.setZoneDataProvider(() -> provider);
```

The Android artifacts use a custom assets-based loader to avoid the cost of loading from Java resources.

</details>

<details>
<summary>Custom Regions</summary>

By default, TickTock's prepackaged timezone data supports all regions. You can define your own via
implementing a custom `ZoneIdsProvider` and registering it via `TickTockPlugins` _before_ using any
time APIs that would cause the system `ZoneRulesProvider` to initialize.

```java
TickTockPlugins.setZoneIdsProvider(CustomZoneIdsProvider::new);
```

If no provider is specified, TickTock will use `TzdbZoneProvider`.

</details>

<details>
<summary>Lazy Zone Rules</summary>

TickTock's default behavior is focused around using traditional `tzdb.dat` files for timezone data
implemented via `TzdbZoneDataProvider`. Early adopters can try a custom, lazy-loading solution
via `LazyZoneDataProvider` inspired by [LazyThreeTenBp](https://github.com/gabrielittner/lazythreetenbp).
In theory, this artifact would be lower overhead on startup for devices with slower IO and a lower
application-lifetime memory impact by only keeping used zones in memory. We're seeking feedback on
whether this is truly worth supporting though, so please let us know!

</details>

<details>
<summary>Compiler CLI</summary>

To manually compile lazy zone rules yourself, you can use the ticktock-compiler API.

```
Usage: ticktockc [OPTIONS]

Options:
  --version TEXT            Version of the time zone data, e.g. 2017b.
  --srcdir DIRECTORY        Directory containing the unpacked leapsecond and
                            tzdb files.
  --tzdbfiles TEXT          Names of the tzdb files to process.
  --leapfile TEXT           Name of the leapsecond file to process.
  --codeoutdir DIRECTORY    Output directory for the generated java code.
  --tzdboutdir DIRECTORY    Output directory for the generated tzdb files.
  --verbose                 Verbose output.
  --language [JAVA|KOTLIN]  Language output (java or kotlin).
  --packagename TEXT        Package name to output with.
  -h, --help                Show this message and exit
```

Gradle coordinates:

[![Maven Central](https://img.shields.io/maven-central/v/dev.zacsweers.ticktock/ticktock-compiler.svg)](https://mvnrepository.com/artifact/dev.zacsweers.ticktock/ticktock-compiler)
```kotlin
implementation("dev.zacsweers.ticktock:ticktock-compiler:<version>")
```

If you want a fat jar binary, you can clone and run `./gradlew :ticktock-compiler:installDist`. Binaries
will be generated to `ticktock-compiler/build/install/ticktock-compiler/bin`. If there is interest,
we may explore automatically uploading these as GitHub release artifacts.

</details>

<details>
<summary>Gradle Plugin</summary>

The Gradle plugin can be used to automatically download new TZ data, package it, and/or generate
lazy zone rules if you want to manage data yourself.

```kotlin
plugins {
  id("dev.zacsweers.ticktock")
}
```

To generate a standard `tzdb.dat`: run the `generateTzdbDat` task.

To generate lazy zone rules: run the `generateLazyZoneRules` task.

Extension and configuration:

```kotlin
ticktock {
 /** The IANA timezone data version */
 val tzVersion: Property<String> // default to '2020d'

 /** The output directory to generate tz data to. Defaults to src/main/resources.  */
 val tzOutputDir: DirectoryProperty // defaults to src/main/resources

 /** Output directory for generated code, if generating for lazy rules. */
 val codeOutputDir: DirectoryProperty

 /** The language to generate in if generating for lazy rules, either `java` or `kotlin`. */
 val language: Property<String> // defaults to java

 /** The package name to generate in if generating for lazy rules. */
 val packageName: Property<String> // defaults to 'ticktock'
}
```

</details>

## Download

[![Maven Central](https://img.shields.io/maven-central/v/dev.zacsweers.ticktock/ticktock-runtime.svg)](https://mvnrepository.com/artifact/dev.zacsweers.ticktock/ticktock-runtime)
```gradle
// Core runtime artifact
implementation 'dev.zacsweers.ticktock:ticktock-runtime:<version>'

// TZDB artifacts
implementation 'dev.zacsweers.ticktock:ticktock-jvm-tzdb:<version>'
implementation 'dev.zacsweers.ticktock:ticktock-android-tzdb:<version>'

// Lazy zone rules artifacts
implementation 'dev.zacsweers.ticktock:ticktock-jvm-lazyzonerules:<version>'
implementation 'dev.zacsweers.ticktock:ticktock-android-lazyzonerules:<version>'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snapshots].

### Versioning

Versions are semver + the current [IANA](https://www.iana.org/time-zones) TZ data version it's packaged with. 

Example: `1.0.0-2020d`

Note that while some artifacts don't contain TZ data, we use the same version for everything in the 
interest of simplicity.

## Why?

https://www.zacsweers.dev/ticktock-desugaring-timezones/

License
-------

    Copyright (C) 2020 Zac Sweers & Gabriel Ittner

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [snapshots]: https://oss.sonatype.org/content/repositories/snapshots/
