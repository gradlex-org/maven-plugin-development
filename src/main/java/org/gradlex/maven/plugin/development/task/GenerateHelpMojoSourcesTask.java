// SPDX-License-Identifier: Apache-2.0
package org.gradlex.maven.plugin.development.task;

import java.io.File;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.ProjectArtifact;
import org.apache.maven.tools.plugin.generator.GeneratorException;
import org.apache.maven.tools.plugin.generator.PluginHelpGenerator;
import org.codehaus.plexus.velocity.VelocityComponent;
import org.codehaus.plexus.velocity.internal.DefaultVelocityComponent;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradlex.maven.plugin.development.internal.MavenLoggerAdapter;

@CacheableTask
public abstract class GenerateHelpMojoSourcesTask extends AbstractMavenPluginDevelopmentTask {

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    // Implicit output of PluginHelpGenerator#execute
    @OutputFile
    public abstract RegularFileProperty getHelpPropertiesFile();

    @Input
    public abstract Property<String> getHelpMojoPackage();

    private final MavenLoggerAdapter loggerAdapter = new MavenLoggerAdapter(getLogger());

    @TaskAction
    public void generateHelpMojo() throws GeneratorException {
        getFs().delete(spec -> spec.delete(getOutputDirectory()));

        PluginHelpGenerator generator = new PluginHelpGenerator();
        generator.enableLogging(loggerAdapter);
        generator.setVelocityComponent(createVelocityComponent());
        generator.setHelpPackageName(getHelpMojoPackage().get());
        generator.setMavenProject(mavenProject());
        generator.execute(getOutputDirectory().get().getAsFile());
    }

    private MavenProject mavenProject() {
        File propertiesDirectory = getHelpPropertiesFile().get().getAsFile().getParentFile();
        propertiesDirectory.mkdirs();

        MavenProject project = new MavenProject();
        project.setGroupId(getPluginDescriptor().get().getGav().getGroup());
        project.setArtifactId(getPluginDescriptor().get().getGav().getArtifactId());
        project.setVersion(getPluginDescriptor().get().getGav().getVersion());
        project.setArtifact(new ProjectArtifact(project));
        Build build = new Build();
        build.setDirectory(propertiesDirectory.getAbsolutePath());
        project.setBuild(build);

        return project;
    }

    private VelocityComponent createVelocityComponent() {
        // fix for broken debugging due to reflection happening inside Velocity
        // (RuntimeInstance#initializeResourceManager())
        ClassLoader currentContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            return new DefaultVelocityComponent(null);
        } finally {
            Thread.currentThread().setContextClassLoader(currentContextClassLoader);
        }
    }
}
