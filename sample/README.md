# Usage sample

This folder contains a sample for how to build a Maven plugin using the `org.gradlex.maven-plugin-development` Gradle plugin and how to publish the Maven plugin in a way that it can be consumed from a Maven build.

## Introduction

The producing Gradle build is contained in the `gradle-producer-build` folder.
It's configured to build and publish a `hello-maven-plugin` like the one described in the [Maven Guides](https://maven.apache.org/guides/plugin/guide-java-plugin-development.html).
The `maven-consumer-build` contains a Maven build that applies the plugin created from the Gradle build.
In order to run the Maven build you first need to publish the plugin to your local Maven repository.

## Publishing the plugin to the local Maven repository

First you need to publish the plugin from the Gradle build in `gradle-producer-build` to your local Maven repository.
Inside the `gradle-producer-build` folder run:

```shell
❯ gradle publishToMavenLocal
Starting a Gradle Daemon (subsequent builds will be faster)

BUILD SUCCESSFUL in 13s
6 actionable tasks: 6 executed
```

Then you can run the Maven build in `maven-consumer-build` folder:

```shell
❯ mvn hello:sayhi
[INFO] Scanning for projects...
[INFO]
[INFO] -------------< sample.plugin:hello-maven-plugin-consumer >--------------
[INFO] Building hello-maven-plugin-consumer 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- hello-maven-plugin:1.0-SNAPSHOT:sayhi (default-cli) @ hello-maven-plugin-consumer ---
[INFO] Hello, world.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.259 s
[INFO] Finished at: 2020-08-02T17:28:19+02:00
[INFO] ------------------------------------------------------------------------
```

## Publishing the plugin to a remote repository

The `gradle-producer-build` also contains some configuration to publish to another repository.
In this case the repository URI of the `buildFolder` repository points into the build folder of the project (see [`gradle-producer-build/build.gradle.kts`](https://github.com/gradlex-org/maven-plugin-development/blob/master/example/gradle-producer-build/build.gradle.kts)).
By running `gradle publishMavenPluginPublicationToBuildFolderRepository` you can publish the plugin to the `buildFolder` repository.
This should result in the following file tree (timestamps in filenames will be different):

```
build
`-- repository
    `-- sample
        `-- plugin
            `-- hello-maven-plugin
                |-- 1.0-SNAPSHOT
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.jar
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.jar.md5
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.jar.sha1
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.jar.sha256
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.jar.sha512
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.module
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.module.md5
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.module.sha1
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.module.sha256
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.module.sha512
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.pom
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.pom.md5
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.pom.sha1
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.pom.sha256
                |   |-- hello-maven-plugin-1.0-20200802.154018-1.pom.sha512
                |   |-- maven-metadata.xml
                |   |-- maven-metadata.xml.md5
                |   |-- maven-metadata.xml.sha1
                |   |-- maven-metadata.xml.sha256
                |   `-- maven-metadata.xml.sha512
                |-- maven-metadata.xml
                |-- maven-metadata.xml.md5
                |-- maven-metadata.xml.sha1
                |-- maven-metadata.xml.sha256
                `-- maven-metadata.xml.sha512
```

This is what Gradle will publish to Maven local when running `gradle pTML` or to a remote repository if you configure the URL to point at your remote artifact repository.

If you want to publish to Maven Central you also need to configure the publication of JavaDocs and sources as well as GPG signing.
Please refer to the [maven-publish plugin documentation](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:complete_example) for more information on that.
