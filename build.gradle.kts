plugins { id("java-test-fixtures") }

version = "1.0.3"

dependencies {
    api(platform(libs.mavenPluginTools.bom)) {
        because("the version for other dependencies in api would be missing otherwise")
    }
    implementation(libs.mavenPluginTools.api)
    implementation(libs.mavenPluginTools.annotations)
    implementation(libs.mavenPluginTools.java)
    implementation(libs.mavenPluginTools.generators)

    api(libs.mavenPlugin.annotations) { because("MavenMojo references types from this artifact") }
    implementation(libs.mavenPlugin.api)

    implementation(libs.sisu.injectPlexus) { because("it is needed to implement the plexus logging adapter") }
    implementation(libs.plexus.velocity) { because("it is needed to generate the help mojo") }
    constraints {
        implementation(libs.qdox) { because("we need the fix for https://github.com/paul-hammant/qdox/issues/43") }
        implementation(libs.jsoup) {
            because(
                "fixes https://github.com/advisories/GHSA-65r4-943x-97jj and https://github.com/advisories/GHSA-pmhh-3w7g-xqp8"
            )
        }
        implementation(libs.plexus.utils) { because("fixes https://github.com/advisories/GHSA-6fmv-xxpf-w3cw") }
        implementation(libs.guava) { because("fixes https://github.com/advisories/GHSA-xxph-c9ww-hj94") }
        implementation(libs.plexus.archiver) {
            because(
                "versions >= 4.11.0 no longer bring io.airlift:aircompressor < 2.0.3, fixes https://github.com/advisories/GHSA-vx9q-rhv9-3jvg"
            )
        }
        implementation(libs.commonsLang) {
            because(
                "fixes https://github.com/advisories/GHSA-j288-q9x7-2f5v for users resolving commons-lang3 < 3.18, e.g. through velocity-engine-core 2.4"
            )
        }
    }

    testFixturesImplementation(libs.junit4)
    testFixturesImplementation(libs.commonsLang)
}

publishingConventions {
    pluginPortal("${project.group}.${project.name}") {
        implementationClass("org.gradlex.maven.plugin.development.MavenPluginDevelopmentPlugin")
        displayName("Maven Plugin Development Gradle Plugin")
        description("Gradle plugin for developing Apache Maven plugins.")
        tags("gradlex", "maven", "mojo", "maven plugin")
    }
    gitHub("https://github.com/gradlex-org/maven-plugin-development")
    website("https://gradlex.org/maven-plugin-development")
    developer {
        id = "britter"
        name = "Benedikt Ritter"
        email = "benedikt@gradlex.org"
    }
}

// Required to write localRepository property to src/test/resources/test.properties for takari-plugin-testing used in
// MavenEndToEndFuncTest
tasks.processTestResources { expand("localRepository" to project.layout.buildDirectory.dir("mavenLocal").get().asFile) }

// === the following custom configuration should be removed once tests are migrated to Java
apply(plugin = "groovy")

dependencies {
    testImplementation(libs.spock.core)
    testImplementation(libs.spock.junit4)
    testImplementation(libs.takariPluginTesting)
    testImplementation(project.dependencies.testFixtures(project))
    testRuntimeOnly(libs.junitVintageEngine)
} //
// ====================================================================================
