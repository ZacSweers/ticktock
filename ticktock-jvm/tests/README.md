ticktock-jvm tests
==================

Note that these _must_ run via Gradle (and not via IDE runner) as the IDE runner does not respect
our `forkEvery` configuration and does not correctly populate the classpath.