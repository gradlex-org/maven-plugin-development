plugins {
    id("de.benediktritter.maven-plugin-development") version "0.4.3"
}

// tag::help-mojo[]
mavenPlugin {
    helpMojoPackage.set("org.example.help")
}
// end::help-mojo[]
