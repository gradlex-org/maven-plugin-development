// SPDX-License-Identifier: Apache-2.0
package org.gradlex.maven.plugin.development.task;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;

public final class MavenPluginDescriptor {

    private final GAV gav;
    private final String name;
    private final String description;
    private final String goalPrefix;

    public MavenPluginDescriptor(GAV gav, String name, String description, String goalPrefix) {
        this.gav = gav;
        this.name = name;
        this.description = description;
        this.goalPrefix = goalPrefix;
    }

    @Nested
    public GAV getGav() {
        return gav;
    }

    @Input
    public String getName() {
        return name;
    }

    @Input
    public String getDescription() {
        return description;
    }

    @Optional
    @Input
    public String getGoalPrefix() {
        return goalPrefix;
    }
}
