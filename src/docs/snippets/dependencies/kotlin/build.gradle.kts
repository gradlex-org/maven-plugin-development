plugins {
    id("org.gradlex.maven-plugin-development") version "1.0.3"
}

// tag::dependencies[]
val deps by configurations.creating

dependencies {
    deps("org.apache.commons:commons-lang3:3.19.0")
    implementation("com.google.guava:guava:33.5.0-jre")
}

mavenPlugin {
    dependencies.set(deps)
}
// end::dependencies[]
