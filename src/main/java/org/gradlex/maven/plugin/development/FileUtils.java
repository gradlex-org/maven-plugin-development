// SPDX-License-Identifier: Apache-2.0
package org.gradlex.maven.plugin.development;

import java.io.File;
import java.util.Optional;

final class FileUtils {

    static Optional<String> getExtension(File file) {
        if (!file.isFile()) {
            return Optional.empty();
        }
        String[] fileNameSegments = file.getName().split("\\.");
        if (fileNameSegments.length == 1) {
            return Optional.empty();
        }
        return Optional.of(fileNameSegments[fileNameSegments.length - 1]);
    }

    private FileUtils() {}
}
