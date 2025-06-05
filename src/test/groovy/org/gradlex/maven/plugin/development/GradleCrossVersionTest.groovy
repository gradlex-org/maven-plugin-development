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

package org.gradlex.maven.plugin.development

class GradleCrossVersionTest extends AbstractPluginFuncTest {

    def "works on Gradle #gradleVersion"() {
        given:
        javaMojo()

        expect:
        runner("build")
          .withGradleVersion(gradleVersion)
          .build()

        where:
        gradleVersion << ["7.5", "7.6.5", "8.0.2"]
    }

    def "fails on version < 7.5"() {
        given:
        javaMojo()

        expect:
        def result = runner("build")
          .withGradleVersion("7.4")
          .buildAndFail()

        and:
        result.output.contains("Plugin requires as least Gradle 7.5.")
    }
}
