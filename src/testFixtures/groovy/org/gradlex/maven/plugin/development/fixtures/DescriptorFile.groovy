/*
 * Copyright the GradleX team.
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

package org.gradlex.maven.plugin.development.fixtures

import groovy.transform.Immutable
import groovy.xml.XmlSlurper
import org.apache.commons.lang3.ClassUtils
import org.apache.commons.lang3.StringUtils

@Immutable
class DescriptorFile {

    String path
    String name
    String description
    String groupId
    String artifactId
    String version
    String goalPrefix
    Set<MojoDeclaration> mojos
    Set<DependencyDeclaration> dependencies

    static DescriptorFile parse(File file) {
        assert file.exists(): "Descriptor $file does not exist"
        def xml = new XmlSlurper().parse(file)
        def mojos = [] as Set
        xml.mojos.mojo.each {
            def params = [] as Set
            it.parameters.parameter.each {
                def param = new ParameterDeclaration(
                        it.name.text(),
                        ClassUtils.getClass(it.type.text()),
                        it.alias.text(),
                        it.required.text().toBoolean(),
                        it.editable.text().toBoolean(),
                        it.description.text()
                )
                assert params << param: "Duplicate parameter declaration $param for mojo $mojo"
            }
            def mojo = new MojoDeclaration(
                    it.goal.text(),
                    it.description.text(),
                    it.requiresDependencyResolution.text(),
                    it.requiresDependencyCollection.text(),
                    it.requiresDirectInvocation.text().toBoolean(),
                    it.requiresProject.text().toBoolean(),
                    it.requiresReports.text().toBoolean(),
                    it.aggregator.text().toBoolean(),
                    it.requiresOnline.text().toBoolean(),
                    it.inheritedByDefault.text().toBoolean(),
                    it.phase.text(),
                    it.implementation.text(),
                    it.language.text(),
                    it.instantiationStrategy.text(),
                    it.executionStrategy.text(),
                    it.configurator.text(),
                    it.threadSafe.text().toBoolean(),
                    params
            )
            assert mojos << mojo: "Duplicate mojo declaration $mojo"
        }
        def dependencies = [] as Set
        xml.dependencies.dependency.each {
            def dependency = new DependencyDeclaration(it.groupId.text(), it.artifactId.text(), it.version.text(), it.type.text())
            assert dependencies << dependency: "Duplicate dependency declaration $dependency"
        }
        def path = "META-INF" + StringUtils.substringAfter(file.absolutePath, "META-INF")
        return new DescriptorFile(
                path,
                xml.name.text(),
                xml.description.text(),
                xml.groupId.text(),
                xml.artifactId.text(),
                xml.version.text(),
                xml.goalPrefix.text(),
                mojos,
                dependencies
        )
    }

    boolean hasGoal(String expectedGoal) {
        mojos.any { it.goal == expectedGoal }
    }

    MojoDeclaration getMojo(String goalName) {
        mojos.find { it.goal == goalName }
    }

    boolean hasDependency(String dependencyNotation, String type = "jar") {
        def coords = dependencyNotation.split(":")
        dependencies.any {
            it.groupId == coords[0] &&
            it.artifactId == coords[1] &&
            it.version == coords[2] &&
            it.type == type
        }
    }

    boolean hasNoDependencies() {
        return dependencies.isEmpty()
    }

    @Immutable
    static class DependencyDeclaration {
        String groupId
        String artifactId
        String version
        String type
    }

    @Immutable
    static class MojoDeclaration {
        String goal
        String description
        String requiresDependencyResolution
        String requiresDependencyCollection
        boolean requiresDirectInvocation
        boolean requiresProject
        boolean requiresReports
        boolean aggregator
        boolean requiresOnline
        boolean inheritedByDefault
        String phase
        String implementation
        String language
        String instantiationStrategy
        String executionStrategy
        String configurator
        boolean threadSafe
        Set<ParameterDeclaration> parameters

        ParameterDeclaration getParameter(String parameterName) {
            parameters.find { it.name == parameterName }
        }
    }

    @Immutable
    static class ParameterDeclaration {
        String name
        Class type
        String alias
        boolean required
        boolean editable
        String description
    }
}
