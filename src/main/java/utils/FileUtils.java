package utils;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;

public class FileUtils {
    public static Path getResourcePath(String resourcePath) {
        var classLoader = FileUtils.class.getClassLoader();
        File inputFile = new File(Objects.requireNonNull(classLoader.getResource(resourcePath)).getFile());
        return inputFile.toPath();
    }
}
