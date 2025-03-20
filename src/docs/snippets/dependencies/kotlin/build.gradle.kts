plugins {
    id("org.gradlex.maven-plugin-development") version "1.0.3"
}

// tag::dependencies[]
val deps by configurations.creating

dependencies {
    deps("org.apache.commons:commons-lang3:3.17.0")
    implementation("com.google.guava:guava:33.4.5-jre")
}

mavenPlugin {
    dependencies.set(deps)
}
// end::dependencies[]
