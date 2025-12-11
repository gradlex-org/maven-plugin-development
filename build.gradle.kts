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
