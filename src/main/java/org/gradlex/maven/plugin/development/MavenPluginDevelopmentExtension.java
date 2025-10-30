// SPDX-License-Identifier: Apache-2.0
package org.gradlex.maven.plugin.development;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.provider.Property;

public interface MavenPluginDevelopmentExtension {

    String NAME = "mavenPlugin";

    Property<String> getGroupId();

    Property<String> getArtifactId();

    Property<String> getVersion();

    Property<String> getName();

    Property<String> getDescription();

    Property<String> getGoalPrefix();

    Property<String> getHelpMojoPackage();

    /**
     * The set of dependencies to add to the plugin descriptor.
     *
     * Defaults to the runtime classpath of this project.
     */
    Property<Configuration> getDependencies();
}
