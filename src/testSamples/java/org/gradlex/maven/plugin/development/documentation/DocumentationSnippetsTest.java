// SPDX-License-Identifier: Apache-2.0
package org.gradlex.maven.plugin.development.documentation;

import org.gradle.exemplar.test.runner.SampleModifiers;
import org.gradle.exemplar.test.runner.SamplesRoot;
import org.gradle.exemplar.test.runner.SamplesRunner;
import org.junit.runner.RunWith;

@RunWith(SamplesRunner.class)
@SamplesRoot("src/docs/snippets")
@SampleModifiers(DocumentationSnippetsTestModifier.class)
public class DocumentationSnippetsTest {}
