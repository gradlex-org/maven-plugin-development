/*
 * Copyright 2022 the GradleX team.
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

package org.gradlex.maven.plugin.development;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ArtifactView;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.result.ResolvedArtifactResult;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.VerificationType;
import org.gradle.api.file.Directory;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;
import org.gradle.util.GradleVersion;
import org.gradlex.maven.plugin.development.task.DependencyDescriptor;
import org.gradlex.maven.plugin.development.task.GAV;
import org.gradlex.maven.plugin.development.task.GenerateHelpMojoSourcesTask;
import org.gradlex.maven.plugin.development.task.GenerateMavenPluginDescriptorTask;
import org.gradlex.maven.plugin.development.task.MavenPluginDescriptor;
import org.gradlex.maven.plugin.development.task.UpstreamProjectDescriptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MavenPluginDevelopmentPlugin implements Plugin<Project> {

    public static final String TASK_GROUP_NAME = "Maven Plugin Development";

    /**
     * Minimum supported version is 7.5 because in this release
     * {@link org.gradle.api.artifacts.ArtifactView.ViewConfiguration#withVariantReselection} was introduced,
     * which is required to select the sources from the compile classpath.
     */
    private static final GradleVersion MIN_VERSION = GradleVersion.version("7.5");

    @Override
    public void apply(Project project) {
        if (GradleVersion.current().compareTo(MIN_VERSION) < 0) {
            throw new IllegalStateException("Plugin requires as least Gradle 7.5.");
        }

        project.getPluginManager().apply(JavaPlugin.class);

        Provider<Directory> pluginOutputDirectory = project.getLayout().getBuildDirectory().dir("mavenPlugin");
        Provider<Directory> descriptorDir = pluginOutputDirectory.map(it -> it.dir("descriptor"));
        Provider<Directory> helpMojoDir = pluginOutputDirectory.map(it -> it.dir("helpMojo"));

        MavenPluginDevelopmentExtension extension = project.getExtensions().create(MavenPluginDevelopmentExtension.NAME, MavenPluginDevelopmentExtension.class);
        extension.getGroupId().convention(project.provider(() -> project.getGroup().toString()));
        extension.getArtifactId().convention(project.provider(project::getName));
        extension.getVersion().convention(project.provider(() -> project.getVersion().toString()));
        extension.getName().convention(project.provider(project::getName));
        extension.getDescription().convention(project.provider(project::getDescription));
        extension.getDependencies().convention(project.getConfigurations().getByName("runtimeClasspath"));

        TaskProvider<GenerateHelpMojoSourcesTask> generateHelpMojoTask = project.getTasks().register("generateMavenPluginHelpMojoSources", GenerateHelpMojoSourcesTask.class, task -> {
            task.setGroup(TASK_GROUP_NAME);
            task.setDescription("Generates a Maven help mojo that documents the usage of the Maven plugin");

            // capture helpMojoPackage property here for configuration cache compatibility
            Property<String> helpMojoPkg = extension.getHelpMojoPackage();
            task.onlyIf(t -> helpMojoPkg.isPresent());

            task.getHelpMojoPackage().set(extension.getHelpMojoPackage());
            task.getOutputDirectory().set(helpMojoDir);
            task.getHelpPropertiesFile().set(pluginOutputDirectory.map(it -> it.file("maven-plugin-help.properties")));
            task.getPluginDescriptor().set(project.provider(() -> mavenPluginDescriptorOf(extension)));
            task.getRuntimeDependencies().set(
                    extension.getDependencies().map(it ->
                            it.getResolvedConfiguration().getResolvedArtifacts().stream()
                                    .map(artifact ->
                                            new DependencyDescriptor(
                                                    artifact.getModuleVersion().getId().getGroup(),
                                                    artifact.getModuleVersion().getId().getName(),
                                                    artifact.getModuleVersion().getId().getVersion(),
                                                    artifact.getExtension())
                                    ).collect(Collectors.toList())
                    )
            );
        });

        SourceSet main = project.getExtensions().getByType(SourceSetContainer.class).getByName("main");
        TaskProvider<GenerateMavenPluginDescriptorTask> generateTask = project.getTasks().register("generateMavenPluginDescriptor", GenerateMavenPluginDescriptorTask.class, task -> {
            task.setGroup(TASK_GROUP_NAME);
            task.setDescription("Generates the Maven plugin descriptor file");

            task.getClassesDirs().from(main.getOutput().getClassesDirs());
            task.getSourcesDirs().from(main.getJava().getSourceDirectories());
            task.getUpstreamProjects().convention(project.provider(() -> extractUpstreamProjects(project)));
            task.getOutputDirectory().set(descriptorDir);
            task.getPluginDescriptor().set(project.provider(() -> mavenPluginDescriptorOf(extension)));
            task.getRuntimeDependencies().set(extension.getDependencies().map(c ->
                    c.getResolvedConfiguration().getResolvedArtifacts().stream()
                            .map(artifact -> new DependencyDescriptor(
                                            artifact.getModuleVersion().getId().getGroup(),
                                            artifact.getModuleVersion().getId().getName(),
                                            artifact.getModuleVersion().getId().getVersion(),
                                            artifact.getExtension()
                                    )
                            )
                            .collect(Collectors.toList())
            ));

            task.dependsOn(main.getOutput(), generateHelpMojoTask);
        });

        project.afterEvaluate(p -> {
            Jar jarTask = (Jar) p.getTasks().findByName(main.getJarTaskName());
            jarTask.from(generateTask);
            main.getJava().srcDir(generateHelpMojoTask.map(GenerateHelpMojoSourcesTask::getOutputDirectory));
        });
    }

    private static MavenPluginDescriptor mavenPluginDescriptorOf(MavenPluginDevelopmentExtension extension) {
        return new MavenPluginDescriptor(
                GAV.of(extension.getGroupId().get(), extension.getArtifactId().get(), extension.getVersion().get()),
                extension.getName().get(),
                extension.getDescription().getOrElse(""),
                extension.getGoalPrefix().getOrNull()
        );
    }

    private static List<UpstreamProjectDescriptor> extractUpstreamProjects(Project project) {
        Configuration compileClasspath = project.getConfigurations().getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME);
        // It's not possible to access group, and version of project dependencies, see https://github.com/gradle/gradle/issues/31973
        String group = project.getGroup().toString();
        String version = project.getVersion().toString();

        Map<GAV, File> classDirectoriesByGAV = getDependencyProjectClassesDirectoriesMappedByGav(compileClasspath, group, version);
        Map<GAV, File> sourcesDirectoriesByGAV = getDependencyProjectSourceDirectoriesMappedByGav(project, compileClasspath, group, version);

        return classDirectoriesByGAV.entrySet().stream()
                .collect(associateClassesDirectoriesToSourcesDirectories(sourcesDirectoriesByGAV));
    }

    private static Map<GAV, File> getDependencyProjectClassesDirectoriesMappedByGav(Configuration compileClasspath, String group, String version) {
        return compileClasspath.getIncoming()
                .artifactView(projects())
                .getArtifacts().getArtifacts().stream()
                .collect(collectToClassDirectoriesMappedByGav(group, version));
    }

    private static Action<ArtifactView.ViewConfiguration> projects() {
        return vc -> {
            vc.componentFilter(projectDependencies());
        };
    }

    private static Collector<ResolvedArtifactResult, ?, Map<GAV, File>> collectToClassDirectoriesMappedByGav(String group, String version) {
        return Collectors.toMap(
                a -> {
                    ProjectComponentIdentifier m = (ProjectComponentIdentifier) a.getId().getComponentIdentifier();
                    return GAV.of(group, m.getProjectName(), version);
                },
                ResolvedArtifactResult::getFile
        );
    }

    private static Map<GAV, File> getDependencyProjectSourceDirectoriesMappedByGav(Project project, Configuration compileClasspath, String group, String version) {
        return compileClasspath.getIncoming()
                .artifactView(projectSources(project.getObjects()))
                .getArtifacts().getArtifacts().stream()
                .collect(collectSrcMainJavaMappedByGav(group, version));
    }

    private static Action<ArtifactView.ViewConfiguration> projectSources(ObjectFactory objectFactory) {
        return vc -> {
            vc.componentFilter(projectDependencies());
            vc.attributes(sourceAttributes(objectFactory));
            vc.withVariantReselection();
        };
    }

    private static Collector<ResolvedArtifactResult, ?, Map<GAV, File>> collectSrcMainJavaMappedByGav(String group, String version) {
        return Collectors.toMap(
                a -> {
                    ProjectComponentIdentifier m = (ProjectComponentIdentifier) a.getId().getComponentIdentifier();
                    return GAV.of(group, m.getProjectName(), version);
                },
                ResolvedArtifactResult::getFile,
                (l, r) -> l.getName().equals("java") ? l : r
        );
    }

    private static Spec<ComponentIdentifier> projectDependencies() {
        return ci -> ci instanceof ProjectComponentIdentifier;
    }

    /**
     * Inspired by <a href="https://github.com/gradle/gradle/blob/bf974bdd5b1a046611eb50a5ec863c07f7630bfd/platforms/jvm/jacoco/src/main/java/org/gradle/testing/jacoco/plugins/JacocoReportAggregationPlugin.java#L88-L92">...</a>
     * and <a href="https://github.com/gradle/gradle/blob/bf974bdd5b1a046611eb50a5ec863c07f7630bfd/platforms/jvm/platform-jvm/src/main/java/org/gradle/api/plugins/jvm/internal/DefaultJvmEcosystemAttributesDetails.java#L118-L121">...</a>
     */
    private static Action<AttributeContainer> sourceAttributes(ObjectFactory objectFactory) {
        return attributes -> {
            attributes.attribute(Category.CATEGORY_ATTRIBUTE, objectFactory.named(Category.class, Category.VERIFICATION));
            attributes.attribute(VerificationType.VERIFICATION_TYPE_ATTRIBUTE, objectFactory.named(VerificationType.class, VerificationType.MAIN_SOURCES));
        };
    }

    private static Collector<Map.Entry<GAV, File>, ArrayList<UpstreamProjectDescriptor>, ArrayList<UpstreamProjectDescriptor>> associateClassesDirectoriesToSourcesDirectories(Map<GAV, File> sourcesDirectoriesByGav) {
        return Collector.of(ArrayList::new, (acc, e) -> {
            acc.add(new UpstreamProjectDescriptor(
                    e.getKey(),
                    e.getValue(),
                    sourcesDirectoriesByGav.get(e.getKey())
            ));
        }, (l, r) -> {
            l.addAll(r);
            return l;
        }, Collector.Characteristics.UNORDERED);
    }
}