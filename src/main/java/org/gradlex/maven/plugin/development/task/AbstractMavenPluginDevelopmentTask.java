// SPDX-License-Identifier: Apache-2.0
package org.gradlex.maven.plugin.development.task;

import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.apache.maven.tools.plugin.DefaultPluginToolsRequest;
import org.apache.maven.tools.plugin.PluginToolsRequest;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileSystemOperations;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;

public abstract class AbstractMavenPluginDevelopmentTask extends DefaultTask {

    @Nested
    public abstract Property<MavenPluginDescriptor> getPluginDescriptor();

    @Nested
    public abstract ListProperty<DependencyDescriptor> getRuntimeDependencies();

    @Inject
    public abstract FileSystemOperations getFs();

    protected PluginDescriptor createPluginDescriptor() {
        MavenPluginDescriptor pluginDescriptor = getPluginDescriptor().get();

        PluginDescriptor result = new PluginDescriptor();
        result.setGroupId(pluginDescriptor.getGav().getGroup());
        result.setArtifactId(pluginDescriptor.getGav().getArtifactId());
        result.setVersion(pluginDescriptor.getGav().getVersion());
        result.setGoalPrefix(
                pluginDescriptor.getGoalPrefix() != null
                        ? pluginDescriptor.getGoalPrefix()
                        : PluginDescriptor.getGoalPrefixFromArtifactId(
                                pluginDescriptor.getGav().getArtifactId()));
        result.setName(pluginDescriptor.getName());
        result.setDescription(pluginDescriptor.getDescription());
        result.setDependencies(getRuntimeDependencies().get().stream()
                .map(DependencyDescriptor::toComponentDependency)
                .collect(Collectors.toList()));
        return result;
    }

    protected PluginToolsRequest createPluginToolsRequest(
            MavenProject mavenProject, PluginDescriptor pluginDescriptor) {
        DefaultPluginToolsRequest request = new DefaultPluginToolsRequest(mavenProject, pluginDescriptor);
        request.setSkipErrorNoDescriptorsFound(true);
        return request;
    }
}
