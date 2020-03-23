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

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

data class MavenPluginDescriptor(
        @get:Input val groupId: String,
        @get:Input val artifactId: String,
        @get:Input val version: String,
        @get:Input val name: String,
        @get:Input val description: String,
        @get:Input @get:Optional val goalPrefix: String?
)
