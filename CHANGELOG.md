Changelog
=========

1.0.0-2020d
-----------

_2020-10-30_

TickTock is now stable! We're adopting a combination of semantic versioning and suffixing the IANA version.

This means that the first three digits (`1.0.0`) is the semver and `-2020d` is the IANA version.

* Update to `androidx.startup` stable. 
* The Android `-base` artifacts have been merged into the core android artifacts.
* Fixed: JVM lazy zone rules are now properly updated and no longer contain a `tzdb.dat` file. We fixed our regeneration setup.
* New-ish: `ticktock-compiler` artifact, a simple CLI if you want to compile your own lazy zone rules.
* New-ish: `ticktock-gradle-plugin` artifact, a simple Gradle plugin for fetching, compiling, and packaging your own TZ data.

Dependency updates

```
androidx.startup 1.0.0
kotlinpoet 1.7.2
```

0.2.1
-----

_2020-10-23_

* Update to IANA tzdata to `2020d`

0.2.0
-----

_2020-10-07_

* Update to IANA tzdata to `2020b` 
* Fix: don't package in `tzdb.dat` in Android `lazyzonerules-base`
* Update to Kotlin 1.4.10
* Update to `androidx.startup` 1.0.0-beta01

0.1.1
-----

_2020-07-25_

Update `androidx.startup` dependency to 1.0.0-alpha02 to pick up an important fix for proguard rules.

0.1.0
-----

_2020-07-08_

Initial release!
