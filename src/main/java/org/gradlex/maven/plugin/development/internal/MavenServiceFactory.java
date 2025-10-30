// SPDX-License-Identifier: Apache-2.0
package org.gradlex.maven.plugin.development.internal;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.tools.plugin.extractor.MojoDescriptorExtractor;
import org.apache.maven.tools.plugin.extractor.annotations.JavaAnnotationsMojoDescriptorExtractor;
import org.apache.maven.tools.plugin.extractor.annotations.converter.JavadocBlockTagsToXhtmlConverter;
import org.apache.maven.tools.plugin.extractor.annotations.converter.JavadocInlineTagsToXhtmlConverter;
import org.apache.maven.tools.plugin.extractor.annotations.scanner.DefaultMojoAnnotationsScanner;
import org.apache.maven.tools.plugin.extractor.javadoc.JavaJavadocMojoDescriptorExtractor;
import org.apache.maven.tools.plugin.scanner.DefaultMojoScanner;
import org.apache.maven.tools.plugin.scanner.MojoScanner;

public final class MavenServiceFactory {

    public static MojoScanner createMojoScanner(MavenLoggerAdapter loggerAdapter) {
        Map<String, MojoDescriptorExtractor> extractors = new HashMap<>(2);
        extractors.put("java-annotations", annotationExtractor(loggerAdapter));
        extractors.put("java-javadoc", javadocExtractor(loggerAdapter));

        DefaultMojoScanner scanner = new DefaultMojoScanner(extractors);
        scanner.enableLogging(loggerAdapter);

        return scanner;
    }

    private static MojoDescriptorExtractor annotationExtractor(MavenLoggerAdapter loggerAdapter) {
        JavaAnnotationsMojoDescriptorExtractor extractor = new JavaAnnotationsMojoDescriptorExtractor();

        DefaultMojoAnnotationsScanner annotationsScanner = new DefaultMojoAnnotationsScanner();
        annotationsScanner.enableLogging(loggerAdapter);

        setField(extractor, "mojoAnnotationsScanner", annotationsScanner);

        // TODO this needs a map of tag converts, see the
        // org.apache.maven.tools.plugin.extractor.annotations.converter.tag
        //  in org.apache.maven.plugin-tools:maven-plugin-tools-annotations
        JavadocInlineTagsToXhtmlConverter javadocInlineTagsToHtmlConverter =
                new JavadocInlineTagsToXhtmlConverter(Collections.emptyMap());
        setField(extractor, "javadocInlineTagsToHtmlConverter", javadocInlineTagsToHtmlConverter);

        JavadocBlockTagsToXhtmlConverter javadocBlockTagsToHtmlConverter =
                new JavadocBlockTagsToXhtmlConverter(javadocInlineTagsToHtmlConverter, Collections.emptyMap());
        setField(extractor, "javadocBlockTagsToHtmlConverter", javadocBlockTagsToHtmlConverter);

        return extractor;
    }

    private static MojoDescriptorExtractor javadocExtractor(MavenLoggerAdapter loggerAdapter) {
        JavaJavadocMojoDescriptorExtractor javadocExtractor = new JavaJavadocMojoDescriptorExtractor();
        javadocExtractor.enableLogging(loggerAdapter);
        return javadocExtractor;
    }

    private static void setField(Object instance, String fieldName, Object value) {
        Field field = getField(instance, fieldName);
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(false);
        }
    }

    private static Field getField(Object instance, String fieldName) {
        try {
            return instance.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
