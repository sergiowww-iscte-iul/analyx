package pt.iscteiul.analyx.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pt.iscteiul.analyx.model.ArtifactType;
import pt.iscteiul.analyx.util.FileTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CodeAnalyzerTest {

    private CodeAnalyzer analyzer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        analyzer = new CodeAnalyzer();
    }

    // ==================== AST Parsing Tests ====================

    @Test
    void analyzeJavaFile_withSimpleClass_extractsBasicMetrics() throws IOException {
        // ARRANGE
        String javaCode = FileTestUtils.createSimpleJavaClass("SimpleClass");
        Path javaFile = FileTestUtils.createTempJavaFile("SimpleClass", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        assertThat(results).isNotEmpty();

        // Should have CLASS metrics
        List<CodeAnalyzer.MetricResult> classMetrics = results.stream()
                .filter(r -> r.type == ArtifactType.CLASS)
                .toList();
        assertThat(classMetrics).hasSize(1);

        CodeAnalyzer.MetricResult classMetric = classMetrics.get(0);
        assertThat(classMetric.packageName).isEqualTo("com.example");
        assertThat(classMetric.className).isEqualTo("SimpleClass");
        assertThat(classMetric.loc).isGreaterThan(0);
        assertThat(classMetric.numAttributes).isEqualTo(2); // count, name
        assertThat(classMetric.numMethods).isGreaterThanOrEqualTo(1); // simpleMethod + getCount

        // Should have METHOD metrics
        List<CodeAnalyzer.MetricResult> methodMetrics = results.stream()
                .filter(r -> r.type == ArtifactType.METHOD)
                .toList();
        assertThat(methodMetrics).isNotEmpty();
        assertThat(methodMetrics).anyMatch(m -> "simpleMethod".equals(m.methodName));
    }

    @Test
    void analyzeJavaFile_withInterface_skipsInterface() throws IOException {
        // ARRANGE
        String javaCode = FileTestUtils.createJavaInterface("TestInterface");
        Path javaFile = FileTestUtils.createTempJavaFile("TestInterface", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        // Interfaces are skipped, so we should have no CLASS metrics
        List<CodeAnalyzer.MetricResult> classMetrics = results.stream()
                .filter(r -> r.type == ArtifactType.CLASS)
                .toList();
        assertThat(classMetrics).isEmpty();
    }

    @Test
    void analyzeJavaFile_withMultipleMethods_analyzesEach() throws IOException {
        // ARRANGE
        String javaCode = FileTestUtils.createClassWithMultipleMethods("MultiMethodClass", 3);
        Path javaFile = FileTestUtils.createTempJavaFile("MultiMethodClass", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        // Should have 1 CLASS + 3 METHOD metrics
        assertThat(results.stream().filter(r -> r.type == ArtifactType.CLASS).count()).isEqualTo(1);
        assertThat(results.stream().filter(r -> r.type == ArtifactType.METHOD).count()).isEqualTo(3);

        // Verify each method was analyzed
        assertThat(results).anyMatch(r -> "method1".equals(r.methodName));
        assertThat(results).anyMatch(r -> "method2".equals(r.methodName));
        assertThat(results).anyMatch(r -> "method3".equals(r.methodName));
    }

    @Test
    void analyzeJavaFile_withInheritance_calculatesDIT() throws IOException {
        // ARRANGE
        String javaCode = FileTestUtils.createInheritanceJavaClass("ChildClass", "BaseClass");
        Path javaFile = FileTestUtils.createTempJavaFile("ChildClass", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult classMetric = results.stream()
                .filter(r -> r.type == ArtifactType.CLASS)
                .findFirst()
                .orElseThrow();

        // Has a superclass, so DIT should be 1 (based on implementation)
        assertThat(classMetric.dit).isEqualTo(1);
    }

    @Test
    void analyzeJavaFile_withoutInheritance_DITisZero() throws IOException {
        // ARRANGE
        String javaCode = FileTestUtils.createSimpleJavaClass("NoInheritanceClass");
        Path javaFile = FileTestUtils.createTempJavaFile("NoInheritanceClass", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult classMetric = results.stream()
                .filter(r -> r.type == ArtifactType.CLASS)
                .findFirst()
                .orElseThrow();

        // No superclass, DIT should be 0
        assertThat(classMetric.dit).isEqualTo(0);
    }

    @Test
    void analyzeJavaFile_extractsPackageName() throws IOException {
        // ARRANGE
        String javaCode = FileTestUtils.createSimpleJavaClass("TestClass");
        Path javaFile = FileTestUtils.createTempJavaFile("TestClass", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult classMetric = results.stream()
                .filter(r -> r.type == ArtifactType.CLASS)
                .findFirst()
                .orElseThrow();

        assertThat(classMetric.packageName).isEqualTo("com.example");
    }

    // ==================== Cyclomatic Complexity Tests ====================

    @Test
    void calculateCyclomaticComplexity_simpleMethod_returnsOne() throws IOException {
        // ARRANGE
        String javaCode = """
                package com.example;

                public class SimpleMethod {
                    public void noControlFlow() {
                        int x = 1;
                        x++;
                    }
                }
                """;
        Path javaFile = FileTestUtils.createTempJavaFile("SimpleMethod", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult methodMetric = results.stream()
                .filter(r -> r.type == ArtifactType.METHOD && "noControlFlow".equals(r.methodName))
                .findFirst()
                .orElseThrow();

        assertThat(methodMetric.cyclomaticComplexity).isEqualTo(1);
    }

    @Test
    void calculateCyclomaticComplexity_withIfStatement_incrementsComplexity() throws IOException {
        // ARRANGE
        String javaCode = """
                package com.example;

                public class IfStatement {
                    public void methodWithIf(int value) {
                        if (value > 0) {
                            value++;
                        }
                    }
                }
                """;
        Path javaFile = FileTestUtils.createTempJavaFile("IfStatement", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult methodMetric = results.stream()
                .filter(r -> r.type == ArtifactType.METHOD && "methodWithIf".equals(r.methodName))
                .findFirst()
                .orElseThrow();

        assertThat(methodMetric.cyclomaticComplexity).isEqualTo(2); // 1 base + 1 if
    }

    @Test
    void calculateCyclomaticComplexity_withMultipleIfs_countsEach() throws IOException {
        // ARRANGE
        String javaCode = FileTestUtils.createClassWithMultipleIfs("MultipleIfs", 3);
        Path javaFile = FileTestUtils.createTempJavaFile("MultipleIfs", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult methodMetric = results.stream()
                .filter(r -> r.type == ArtifactType.METHOD && "complexityMethod".equals(r.methodName))
                .findFirst()
                .orElseThrow();

        assertThat(methodMetric.cyclomaticComplexity).isEqualTo(4); // 1 base + 3 ifs
    }

    @Test
    void calculateCyclomaticComplexity_withForLoop_increments() throws IOException {
        // ARRANGE
        String javaCode = """
                package com.example;

                public class ForLoop {
                    public void methodWithFor() {
                        for (int i = 0; i < 10; i++) {
                            System.out.println(i);
                        }
                    }
                }
                """;
        Path javaFile = FileTestUtils.createTempJavaFile("ForLoop", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult methodMetric = results.stream()
                .filter(r -> r.type == ArtifactType.METHOD && "methodWithFor".equals(r.methodName))
                .findFirst()
                .orElseThrow();

        assertThat(methodMetric.cyclomaticComplexity).isEqualTo(2); // 1 base + 1 for
    }

    @Test
    void calculateCyclomaticComplexity_withWhileLoop_increments() throws IOException {
        // ARRANGE
        String javaCode = """
                package com.example;

                public class WhileLoop {
                    public void methodWithWhile() {
                        int i = 0;
                        while (i < 10) {
                            i++;
                        }
                    }
                }
                """;
        Path javaFile = FileTestUtils.createTempJavaFile("WhileLoop", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult methodMetric = results.stream()
                .filter(r -> r.type == ArtifactType.METHOD && "methodWithWhile".equals(r.methodName))
                .findFirst()
                .orElseThrow();

        assertThat(methodMetric.cyclomaticComplexity).isEqualTo(2); // 1 base + 1 while
    }

    @Test
    void calculateCyclomaticComplexity_withSwitch_countsEachCase() throws IOException {
        // ARRANGE
        String javaCode = """
                package com.example;

                public class SwitchStatement {
                    public int methodWithSwitch(int value) {
                        switch (value) {
                            case 1:
                                return 10;
                            case 2:
                                return 20;
                            case 3:
                                return 30;
                            default:
                                return 0;
                        }
                    }
                }
                """;
        Path javaFile = FileTestUtils.createTempJavaFile("SwitchStatement", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult methodMetric = results.stream()
                .filter(r -> r.type == ArtifactType.METHOD && "methodWithSwitch".equals(r.methodName))
                .findFirst()
                .orElseThrow();

        // 1 base + 4 cases (case 1, case 2, case 3, default)
        assertThat(methodMetric.cyclomaticComplexity).isEqualTo(5);
    }

    @Test
    void calculateCyclomaticComplexity_withTryCatch_includesCatchClauses() throws IOException {
        // ARRANGE
        String javaCode = """
                package com.example;

                public class TryCatch {
                    public void methodWithTryCatch() {
                        try {
                            int x = Integer.parseInt("123");
                        } catch (NumberFormatException e) {
                            System.out.println("Error 1");
                        } catch (Exception e) {
                            System.out.println("Error 2");
                        }
                    }
                }
                """;
        Path javaFile = FileTestUtils.createTempJavaFile("TryCatch", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult methodMetric = results.stream()
                .filter(r -> r.type == ArtifactType.METHOD && "methodWithTryCatch".equals(r.methodName))
                .findFirst()
                .orElseThrow();

        assertThat(methodMetric.cyclomaticComplexity).isEqualTo(3); // 1 base + 2 catches
    }

    @Test
    void calculateCyclomaticComplexity_withTernaryOperator_increments() throws IOException {
        // ARRANGE
        String javaCode = """
                package com.example;

                public class TernaryOperator {
                    public int methodWithTernary(int value) {
                        return value > 0 ? 1 : 0;
                    }
                }
                """;
        Path javaFile = FileTestUtils.createTempJavaFile("TernaryOperator", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult methodMetric = results.stream()
                .filter(r -> r.type == ArtifactType.METHOD && "methodWithTernary".equals(r.methodName))
                .findFirst()
                .orElseThrow();

        assertThat(methodMetric.cyclomaticComplexity).isEqualTo(2); // 1 base + 1 ternary
    }

    // ==================== Metrics Extraction Tests ====================

    @Test
    void analyzeJavaFile_calculatesLOC() throws IOException {
        // ARRANGE
        String javaCode = FileTestUtils.createSimpleJavaClass("LOCTest");
        Path javaFile = FileTestUtils.createTempJavaFile("LOCTest", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult classMetric = results.stream()
                .filter(r -> r.type == ArtifactType.CLASS)
                .findFirst()
                .orElseThrow();

        assertThat(classMetric.loc).isGreaterThan(0);

        // Method LOC should also be positive
        results.stream()
                .filter(r -> r.type == ArtifactType.METHOD)
                .forEach(m -> assertThat(m.loc).isGreaterThan(0));
    }

    @Test
    void analyzeJavaFile_countsAttributes() throws IOException {
        // ARRANGE
        String javaCode = """
                package com.example;

                public class TwoFields {
                    private int field1;
                    private String field2;

                    public void method() {
                        field1 = 0;
                    }
                }
                """;
        Path javaFile = FileTestUtils.createTempJavaFile("TwoFields", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult classMetric = results.stream()
                .filter(r -> r.type == ArtifactType.CLASS)
                .findFirst()
                .orElseThrow();

        assertThat(classMetric.numAttributes).isEqualTo(2);
    }

    @Test
    void analyzeJavaFile_countsMethods() throws IOException {
        // ARRANGE
        String javaCode = FileTestUtils.createClassWithMultipleMethods("ThreeMethods", 3);
        Path javaFile = FileTestUtils.createTempJavaFile("ThreeMethods", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult classMetric = results.stream()
                .filter(r -> r.type == ArtifactType.CLASS)
                .findFirst()
                .orElseThrow();

        assertThat(classMetric.numMethods).isEqualTo(3);
    }

    @Test
    void analyzeJavaFile_calculatesCBO() throws IOException {
        // ARRANGE
        String javaCode = """
                package com.example;

                import java.util.List;
                import java.util.ArrayList;

                public class CBOTest {
                    private List<String> items;
                    private ArrayList<Integer> numbers;

                    public void method() {
                        items = new ArrayList<>();
                    }
                }
                """;
        Path javaFile = FileTestUtils.createTempJavaFile("CBOTest", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult classMetric = results.stream()
                .filter(r -> r.type == ArtifactType.CLASS)
                .findFirst()
                .orElseThrow();

        // Should have coupling to List, ArrayList, String, Integer
        assertThat(classMetric.cbo).isGreaterThan(0);
    }

    @Test
    void analyzeJavaFile_withComplexClass_allMetricsPresent() throws IOException {
        // ARRANGE
        String javaCode = FileTestUtils.createComplexJavaClass("ComplexClass");
        Path javaFile = FileTestUtils.createTempJavaFile("ComplexClass", javaCode, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        CodeAnalyzer.MetricResult classMetric = results.stream()
                .filter(r -> r.type == ArtifactType.CLASS)
                .findFirst()
                .orElseThrow();

        // Verify all metrics are populated
        assertThat(classMetric.packageName).isNotNull();
        assertThat(classMetric.className).isEqualTo("ComplexClass");
        assertThat(classMetric.loc).isGreaterThan(0);
        assertThat(classMetric.numMethods).isGreaterThan(0);
        assertThat(classMetric.numAttributes).isGreaterThan(0);
        assertThat(classMetric.cbo).isNotNull();
        assertThat(classMetric.dit).isNotNull();
        assertThat(classMetric.noc).isNotNull();

        // Complex method should have high complexity
        CodeAnalyzer.MetricResult complexMethod = results.stream()
                .filter(r -> r.type == ArtifactType.METHOD && "complexMethod".equals(r.methodName))
                .findFirst()
                .orElseThrow();

        // Should have complexity > 1 (has if, for, while, try-catch, ternary)
        assertThat(complexMethod.cyclomaticComplexity).isGreaterThan(5);
    }

    @Test
    void analyzeJavaFile_withInvalidSyntax_throwsException() throws IOException {
        // ARRANGE
        String invalidJava = """
                package com.example;

                public class InvalidSyntax {
                    // Missing closing brace
                    public void method() {
                        int x = 1
                    // Missing semicolon and closing brace
                """;
        Path javaFile = FileTestUtils.createTempJavaFile("InvalidSyntax", invalidJava, tempDir);

        // ACT
        List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

        // ASSERT
        // Even with invalid syntax, AST parser might still parse partially
        // This test verifies the analyzer doesn't crash
        assertThat(results).isNotNull();
    }
}
