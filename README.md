# Maven Plugin Development Gradle Plugin

[![GitHub Actions](https://github.com/gradlex-org/maven-plugin-development/actions/workflows/ci-build.yml/badge.svg)](https://github.com/gradlex-org/maven-plugin-development/actions/workflows/ci-build.yml)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v?label=Plugin%20Portal&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Forg%2Fgradlex%2Fmaven-plugin-development%2Forg.gradlex.maven-plugin-development.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/org.gradlex.maven-plugin-development)
[![Revved up by Develocity](https://img.shields.io/badge/Revved%20up%20by-Develocity-06A0CE?logo=Gradle&labelColor=02303A)](https://community.develocity.cloud/scans?search.names=Git%20repository&search.values=*gradlex-org%2F*)

This plugin aims to fill the tiny gap for people who need to create an [Apache Maven](https://maven.apache.org) plugin from a Gradle build.
To do this the plugin wraps around the [Maven Plugin Tools API](https://maven.apache.org/plugin-tools/) and feeds it with the right inputs from the Gradle build.

Compatible with Gradle 7.5 or later.

## Features

- Automatic generation of a maven plugin descriptor containing all mojos in the selected source set (by default the plugin looks for mojo implementations in the main source set)
- Support for annotation and JavaDoc tag based mojo implementations
- Optional generation of a help mojo implementation

## Usage

Please see the [plugin documentation](https://gradlex.org/maven-plugin-development).
For a self-contained example please the [example folder](https://github.com/gradlex-org/maven-plugin-development/tree/main/sample).

## Contribution policy

Contributions via GitHub pull requests are gladly accepted from their original author. Along with any pull requests, please state that the contribution is your original work and that you license the work to the project under the project's open source license. Whether or not you state this explicitly, by submitting any copyrighted material via pull request, email, or other means you agree to license the material under the project's open source license and warrant that you have the legal authority to do so.

## License

This code is open source software licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.html).

## Disclaimer

Gradle and the Gradle logo are trademarks of Gradle, Inc.
The GradleX project is not endorsed by, affiliated with, or associated with Gradle or Gradle, Inc. in any way.

