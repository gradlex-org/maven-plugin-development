/*
 * Copyright the GradleX team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("org.gradlex.maven-plugin-development") version "1.0.3"
    // necessary for publishing the plugin to maven local or remote repositories
    // see https://docs.gradle.org/current/userguide/publishing_maven.html for more information
    `maven-publish`
}

// this sets the group of the Maven plugin
group = "sample.plugin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // required dependencies for building a Maven Plugin
    implementation("org.apache.maven:maven-plugin-api:3.9.11")
    // annotations are only needed at compile time
    compileOnly("org.apache.maven.plugin-tools:maven-plugin-annotations:3.15.1")
}

publishing {
    publications {
        // create a publication so that the plugin can be published to the local Maven repository
        create<MavenPublication>("mavenPlugin") {
            from(components["java"])
        }
    }
    // setup an additional repository for publishing
    // this repository is located in the build folder
    // change name and URL to a real remote repository in order to publish the plugin
    repositories {
        maven {
            name = "buildFolder"
            url = uri(layout.buildDirectory.dir("repository"))
        }
    }
}
