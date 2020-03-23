/*
 * Copyright 2020 Benedikt Ritter
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

package de.benediktritter.maven.plugin.development

import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.project.MavenProject
import org.apache.maven.tools.plugin.DefaultPluginToolsRequest
import org.apache.maven.tools.plugin.PluginToolsRequest
import org.codehaus.plexus.component.repository.ComponentDependency
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

abstract class AbstractMavenPluginDevelopmentTask : DefaultTask() {

    @get:Nested
    abstract val pluginDescriptor: Property<MavenPluginDescriptor>

    @get:Nested
    abstract val runtimeDependencies: ListProperty<RuntimeDependencyDescriptor>

    protected fun createPluginDescriptor(): PluginDescriptor {
        val pluginDescriptor = pluginDescriptor.get()
        return PluginDescriptor().also {
            it.groupId = pluginDescriptor.groupId
            it.version = pluginDescriptor.version
            val artifactId = pluginDescriptor.artifactId
            it.artifactId = artifactId
            it.goalPrefix = pluginDescriptor.goalPrefix ?: PluginDescriptor.getGoalPrefixFromArtifactId(artifactId)
            it.name = pluginDescriptor.name
            it.description = pluginDescriptor.description
            it.dependencies = getComponentDependencies()
        }
    }

    private fun getComponentDependencies(): List<ComponentDependency> {
        return runtimeDependencies.get().map { dependency ->
            ComponentDependency().also {
                it.groupId = dependency.groupId
                it.artifactId = dependency.artifactId
                it.version = dependency.version
                it.type = dependency.type
            }
        }
    }

    protected fun createPluginToolsRequest(mavenProject: MavenProject, pluginDescriptor: PluginDescriptor): PluginToolsRequest {
        return DefaultPluginToolsRequest(mavenProject, pluginDescriptor).also {
            it.isSkipErrorNoDescriptorsFound = true
        }
    }
}
