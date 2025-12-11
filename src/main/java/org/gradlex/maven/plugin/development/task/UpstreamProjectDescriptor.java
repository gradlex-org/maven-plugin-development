// SPDX-License-Identifier: Apache-2.0
package org.gradlex.maven.plugin.development.task;

import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.CompileClasspath;
import org.gradle.api.tasks.IgnoreEmptyDirectories;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.work.NormalizeLineEndings;

public final class UpstreamProjectDescriptor {

    private final GAV gav;
    private final FileCollection classesDir;
    private final FileCollection sourceDirectories;

    public UpstreamProjectDescriptor(GAV gav, FileCollection classesDir, FileCollection sourcesDir) {
        this.gav = gav;
        this.classesDir = classesDir;
        this.sourceDirectories = sourcesDir;
    }

    @Nested
    public GAV getGav() {
        return gav;
    }

    @CompileClasspath
    @InputFiles
    public FileCollection getClassesDirs() {
        return classesDir;
    }

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    @IgnoreEmptyDirectories
    @NormalizeLineEndings
    public FileCollection getSourceDirectories() {
        return sourceDirectories;
    }
}
