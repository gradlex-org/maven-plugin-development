// SPDX-License-Identifier: Apache-2.0
package org.gradlex.maven.plugin.development.task;

import org.codehaus.plexus.component.repository.ComponentDependency;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;

public final class DependencyDescriptor {

    private final String type;
    private final GAV gav;

    public DependencyDescriptor(GAV gav, String type) {
        this.gav = gav;
        this.type = type;
    }

    @Nested
    public GAV getGav() {
        return gav;
    }

    @Optional
    @Input
    public String getType() {
        return type;
    }

    public ComponentDependency toComponentDependency() {
        ComponentDependency dep = new ComponentDependency();
        dep.setGroupId(gav.getGroup());
        dep.setArtifactId(gav.getArtifactId());
        dep.setVersion(gav.getVersion());
        dep.setType(type);
        return dep;
    }
}
