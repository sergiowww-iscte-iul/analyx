package pt.iscteiul.analyx.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for creating test files and ZIP archives.
 * Provides helper methods for generating Java source files and ZIP files for testing.
 */
public class FileTestUtils {

    /**
     * Creates a temporary Java file with the given content.
     *
     * @param className the name of the class (without .java extension)
     * @param content   the Java source code content
     * @param directory the directory where the file should be created
     * @return Path to the created file
     * @throws IOException if file creation fails
     */
    public static Path createTempJavaFile(String className, String content, Path directory) throws IOException {
        Path file = directory.resolve(className + ".java");
        Files.writeString(file, content);
        return file;
    }

    /**
     * Creates a simple Java class with the given name.
     *
     * @param className the name of the class
     * @return Java source code as a String
     */
    public static String createSimpleJavaClass(String className) {
        return """
                package com.example;

                public class %s {
                    private int count;
                    private String name;

                    public void simpleMethod() {
                        count++;
                    }

                    public int getCount() {
                        return count;
                    }
                }
                """.formatted(className);
    }

    /**
     * Creates a Java class with high cyclomatic complexity.
     *
     * @param className the name of the class
     * @return Java source code as a String with complex logic
     */
    public static String createComplexJavaClass(String className) {
        return """
                package com.example;

                public class %s {
                    private int value;

                    public int complexMethod(int input) {
                        if (input > 0) {
                            for (int i = 0; i < input; i++) {
                                while (value < 10) {
                                    value++;
                                    if (value %% 2 == 0) {
                                        break;
                                    }
                                }
                            }
                        } else {
                            try {
                                value = input;
                            } catch (Exception e) {
                                return -1;
                            }
                        }
                        return value > 5 ? 1 : 0;
                    }

                    public void anotherMethod() {
                        value = 0;
                    }
                }
                """.formatted(className);
    }

    /**
     * Creates a Java class with inheritance.
     *
     * @param className the name of the class
     * @param superclassName the name of the superclass
     * @return Java source code as a String
     */
    public static String createInheritanceJavaClass(String className, String superclassName) {
        return """
                package com.example;

                public class %s extends %s {
                    private String field;

                    public void method() {
                        field = "test";
                    }

                    public String getField() {
                        return field;
                    }
                }
                """.formatted(className, superclassName);
    }

    /**
     * Creates a Java interface.
     *
     * @param interfaceName the name of the interface
     * @return Java source code as a String
     */
    public static String createJavaInterface(String interfaceName) {
        return """
                package com.example;

                public interface %s {
                    void doSomething();
                    int calculate(int value);
                }
                """.formatted(interfaceName);
    }

    /**
     * Creates a Java class with multiple methods for testing method counting.
     *
     * @param className the name of the class
     * @param numMethods the number of methods to create
     * @return Java source code as a String
     */
    public static String createClassWithMultipleMethods(String className, int numMethods) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example;\n\n");
        sb.append("public class ").append(className).append(" {\n");
        sb.append("    private int value;\n\n");

        for (int i = 1; i <= numMethods; i++) {
            sb.append("    public void method").append(i).append("() {\n");
            sb.append("        value = ").append(i).append(";\n");
            sb.append("    }\n\n");
        }

        sb.append("}\n");
        return sb.toString();
    }

    /**
     * Creates a Java class with multiple if statements for complexity testing.
     *
     * @param className the name of the class
     * @param numIfs the number of if statements
     * @return Java source code as a String
     */
    public static String createClassWithMultipleIfs(String className, int numIfs) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example;\n\n");
        sb.append("public class ").append(className).append(" {\n");
        sb.append("    public int complexityMethod(int value) {\n");

        for (int i = 0; i < numIfs; i++) {
            sb.append("        if (value > ").append(i).append(") {\n");
            sb.append("            value++;\n");
            sb.append("        }\n");
        }

        sb.append("        return value;\n");
        sb.append("    }\n");
        sb.append("}\n");
        return sb.toString();
    }

    /**
     * Creates a ZIP file containing the provided Java files.
     *
     * @param javaFiles   list of Java file paths to include in the ZIP
     * @param destination the path where the ZIP file should be created
     * @return Path to the created ZIP file
     * @throws IOException if ZIP creation fails
     */
    public static Path createTestZip(List<Path> javaFiles, Path destination) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destination.toFile()))) {
            for (Path javaFile : javaFiles) {
                ZipEntry entry = new ZipEntry(javaFile.getFileName().toString());
                zos.putNextEntry(entry);
                Files.copy(javaFile, zos);
                zos.closeEntry();
            }
        }
        return destination;
    }

    /**
     * Creates a ZIP file with a nested directory structure.
     *
     * @param javaFiles   list of Java file paths to include
     * @param destination the path where the ZIP file should be created
     * @param packagePath the package path (e.g., "com/example/model")
     * @return Path to the created ZIP file
     * @throws IOException if ZIP creation fails
     */
    public static Path createTestZipWithStructure(List<Path> javaFiles, Path destination, String packagePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destination.toFile()))) {
            // Create directory entries
            if (packagePath != null && !packagePath.isEmpty()) {
                String[] dirs = packagePath.split("/");
                String currentPath = "";
                for (String dir : dirs) {
                    currentPath += dir + "/";
                    ZipEntry dirEntry = new ZipEntry(currentPath);
                    zos.putNextEntry(dirEntry);
                    zos.closeEntry();
                }
            }

            // Add files
            for (Path javaFile : javaFiles) {
                String entryName = packagePath != null && !packagePath.isEmpty()
                    ? packagePath + "/" + javaFile.getFileName().toString()
                    : javaFile.getFileName().toString();
                ZipEntry entry = new ZipEntry(entryName);
                zos.putNextEntry(entry);
                Files.copy(javaFile, zos);
                zos.closeEntry();
            }
        }
        return destination;
    }

    /**
     * Creates an empty ZIP file for testing.
     *
     * @param destination the path where the ZIP file should be created
     * @return Path to the created ZIP file
     * @throws IOException if ZIP creation fails
     */
    public static Path createEmptyZip(Path destination) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destination.toFile()))) {
            // Empty ZIP - no entries
        }
        return destination;
    }

    /**
     * Creates a ZIP file containing non-Java files for testing filtering.
     *
     * @param destination the path where the ZIP file should be created
     * @return Path to the created ZIP file
     * @throws IOException if ZIP creation fails
     */
    public static Path createZipWithNonJavaFiles(Path destination) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destination.toFile()))) {
            // Add .txt file
            ZipEntry txtEntry = new ZipEntry("readme.txt");
            zos.putNextEntry(txtEntry);
            zos.write("This is a text file".getBytes());
            zos.closeEntry();

            // Add .class file
            ZipEntry classEntry = new ZipEntry("Test.class");
            zos.putNextEntry(classEntry);
            zos.write(new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE}); // Mock .class file
            zos.closeEntry();
        }
        return destination;
    }
}
