package pt.iscteiul.analyx.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.iscteiul.analyx.model.ArtifactType;
import pt.iscteiul.analyx.model.Metric;
import pt.iscteiul.analyx.model.Report;
import pt.iscteiul.analyx.model.ReportStatus;
import pt.iscteiul.analyx.util.TestDataBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExportServiceTest {

    private ExportService exportService;
    private Report testReport;

    @BeforeEach
    void setUp() {
        exportService = new ExportService();
        testReport = TestDataBuilder.createReport(
                TestDataBuilder.createProject(TestDataBuilder.createUser("testuser"), "Test Project"),
                ReportStatus.COMPLETED
        );
    }

    // ==================== CSV Export Tests ====================

    @Test
    void exportToCSV_withMetrics_generatesValidCSV() {
        // ARRANGE
        List<Metric> metrics = List.of(
                TestDataBuilder.createClassMetric(testReport),
                TestDataBuilder.createMethodMetric(testReport),
                TestDataBuilder.createMetric(testReport, ArtifactType.METHOD)
        );

        // ACT
        byte[] result = exportService.exportToCSV(metrics);
        String csv = new String(result);

        // ASSERT
        String[] lines = csv.split("\n");
        assertThat(lines).hasSize(4); // header + 3 data rows
        assertThat(lines[0]).contains("Tipo,Package,Classe,Método,LOC,Atributos,Métodos,Complexidade,CBO,DIT,NOC");
        assertThat(csv).contains("CLASS");
        assertThat(csv).contains("METHOD");
        assertThat(csv).contains("com.example");
    }

    @Test
    void exportToCSV_withEmptyList_returnsHeaderOnly() {
        // ARRANGE
        List<Metric> metrics = new ArrayList<>();

        // ACT
        byte[] result = exportService.exportToCSV(metrics);
        String csv = new String(result);

        // ASSERT
        String[] lines = csv.split("\n");
        assertThat(lines).hasSize(1); // only header
        assertThat(lines[0]).isEqualTo("Tipo,Package,Classe,Método,LOC,Atributos,Métodos,Complexidade,CBO,DIT,NOC");
    }

    @Test
    void exportToCSV_withNullValues_handlesGracefully() {
        // ARRANGE
        Metric metricWithNulls = TestDataBuilder.createMetricWithNulls(testReport);
        List<Metric> metrics = List.of(metricWithNulls);

        // ACT
        byte[] result = exportService.exportToCSV(metrics);
        String csv = new String(result);

        // ASSERT
        assertThat(csv).isNotNull();
        String[] lines = csv.split("\n");
        assertThat(lines).hasSize(2); // header + 1 data row
        // Verify that null values are represented as empty strings
        assertThat(lines[1]).contains("CLASS,,,,,,,,,,");
    }

    @Test
    void exportToCSV_escapesCommasInValues() {
        // ARRANGE
        Metric metric = TestDataBuilder.createMetric(testReport, ArtifactType.CLASS);
        metric.setPackageName("com,example,test");
        List<Metric> metrics = List.of(metric);

        // ACT
        byte[] result = exportService.exportToCSV(metrics);
        String csv = new String(result);

        // ASSERT
        assertThat(csv).contains("\"com,example,test\"");
    }

    @Test
    void exportToCSV_escapesQuotesInValues() {
        // ARRANGE
        Metric metric = TestDataBuilder.createMetric(testReport, ArtifactType.CLASS);
        metric.setClassName("Test\"Class\"Name");
        List<Metric> metrics = List.of(metric);

        // ACT
        byte[] result = exportService.exportToCSV(metrics);
        String csv = new String(result);

        // ASSERT
        // Quotes should be doubled and the value should be wrapped in quotes
        assertThat(csv).contains("\"Test\"\"Class\"\"Name\"");
    }

    @Test
    void exportToCSV_escapesNewlinesInValues() {
        // ARRANGE
        Metric metric = TestDataBuilder.createMetric(testReport, ArtifactType.METHOD);
        metric.setMethodName("method\nwith\nnewlines");
        List<Metric> metrics = List.of(metric);

        // ACT
        byte[] result = exportService.exportToCSV(metrics);
        String csv = new String(result);

        // ASSERT
        assertThat(csv).contains("\"method\nwith\nnewlines\"");
    }

    @Test
    void exportToCSV_withAllMetricTypes_includesAllRows() {
        // ARRANGE
        Metric classMetric = TestDataBuilder.createMetric(testReport, ArtifactType.CLASS);
        Metric methodMetric1 = TestDataBuilder.createMetric(testReport, ArtifactType.METHOD);
        Metric methodMetric2 = TestDataBuilder.createMetric(testReport, ArtifactType.METHOD);
        List<Metric> metrics = List.of(classMetric, methodMetric1, methodMetric2);

        // ACT
        byte[] result = exportService.exportToCSV(metrics);
        String csv = new String(result);

        // ASSERT
        String[] lines = csv.split("\n");
        assertThat(lines).hasSize(4); // header + 3 rows
        assertThat(csv.split("CLASS,")).hasSize(2); // 1 CLASS metric
        assertThat(csv.split("METHOD,")).hasSize(3); // 2 METHOD metrics
    }

    @Test
    void escapeCSV_withNoSpecialChars_returnsUnchanged() {
        // ARRANGE
        ExportService service = new ExportService();

        // ACT
        byte[] result = service.exportToCSV(List.of());
        String csv = new String(result);

        // ASSERT
        // Test indirect behavior: values without special chars aren't quoted
        Metric metric = TestDataBuilder.createMetric(testReport, ArtifactType.CLASS);
        metric.setPackageName("com.example.simple");
        metric.setClassName("SimpleClass");
        result = service.exportToCSV(List.of(metric));
        csv = new String(result);
        String[] lines = csv.split("\n");

        // Simple values should not be quoted
        assertThat(lines[1]).contains("com.example.simple");
        assertThat(lines[1]).contains("SimpleClass");
    }

    @Test
    void escapeCSV_withNull_returnsEmptyString() {
        // ARRANGE
        Metric metric = new Metric();
        metric.setArtifactType(ArtifactType.CLASS);
        metric.setPackageName(null);
        metric.setClassName(null);
        List<Metric> metrics = List.of(metric);

        // ACT
        byte[] result = exportService.exportToCSV(metrics);
        String csv = new String(result);

        // ASSERT
        String[] lines = csv.split("\n");
        // Null values should be represented as empty strings in CSV
        assertThat(lines[1]).contains("CLASS,,");
    }

    @Test
    void escapeCSV_withMultipleSpecialChars_handlesProperly() {
        // ARRANGE
        Metric metric = TestDataBuilder.createMetric(testReport, ArtifactType.CLASS);
        metric.setPackageName("com,\"example\"\ntest");
        List<Metric> metrics = List.of(metric);

        // ACT
        byte[] result = exportService.exportToCSV(metrics);
        String csv = new String(result);

        // ASSERT
        // Should escape both commas, quotes, and newlines
        assertThat(csv).contains("\"com,\"\"example\"\"\ntest\"");
    }

    // ==================== JSON Export Tests ====================

    @Test
    void exportToJSON_withMetrics_generatesValidJSON() throws IOException {
        // ARRANGE
        List<Metric> metrics = List.of(
                TestDataBuilder.createClassMetric(testReport),
                TestDataBuilder.createMethodMetric(testReport)
        );

        // ACT
        byte[] result = exportService.exportToJSON(metrics);
        String json = new String(result);

        // ASSERT
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        assertThat(jsonNode.isArray()).isTrue();
        assertThat(jsonNode.size()).isEqualTo(2);

        // Verify fields are present
        JsonNode firstMetric = jsonNode.get(0);
        assertThat(firstMetric.has("tipo")).isTrue();
        assertThat(firstMetric.has("package")).isTrue();
        assertThat(firstMetric.has("classe")).isTrue();
        assertThat(firstMetric.has("loc")).isTrue();
    }

    @Test
    void exportToJSON_withEmptyList_returnsEmptyArray() throws IOException {
        // ARRANGE
        List<Metric> metrics = new ArrayList<>();

        // ACT
        byte[] result = exportService.exportToJSON(metrics);
        String json = new String(result);

        // ASSERT
        assertThat(json.trim()).contains("[ ]");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        assertThat(jsonNode.isArray()).isTrue();
        assertThat(jsonNode.size()).isEqualTo(0);
    }

    @Test
    void exportToJSON_withLocalDateTime_formatsCorrectly() throws IOException {
        // ARRANGE
        Metric metric = TestDataBuilder.createClassMetric(testReport);
        List<Metric> metrics = List.of(metric);

        // ACT
        byte[] result = exportService.exportToJSON(metrics);
        String json = new String(result);

        // ASSERT
        // Verify it's not a timestamp (number), but a formatted string
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        JsonNode criadoEm = jsonNode.get(0).get("criadoEm");

        // Should be a string in ISO-8601 format, not a number
        assertThat(criadoEm.isTextual()).isTrue();
        assertThat(criadoEm.asText()).matches("\\d{4}-\\d{2}-\\d{2}T.*");
    }

    @Test
    void exportToJSON_withNullFields_includesNullsInJSON() throws IOException {
        // ARRANGE
        Metric metric = TestDataBuilder.createMetricWithNulls(testReport);
        List<Metric> metrics = List.of(metric);

        // ACT
        byte[] result = exportService.exportToJSON(metrics);
        String json = new String(result);

        // ASSERT
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        JsonNode firstMetric = jsonNode.get(0);

        // Null fields should be present as null
        assertThat(firstMetric.get("package").isNull()).isTrue();
        assertThat(firstMetric.get("classe").isNull()).isTrue();
        assertThat(firstMetric.get("loc").isNull()).isTrue();
    }

    @Test
    void exportToJSON_prettyPrintEnabled_hasIndentation() throws IOException {
        // ARRANGE
        List<Metric> metrics = List.of(TestDataBuilder.createClassMetric(testReport));

        // ACT
        byte[] result = exportService.exportToJSON(metrics);
        String json = new String(result);

        // ASSERT
        // Pretty printing should include newlines and indentation
        assertThat(json).contains("\n");
        assertThat(json).contains("  "); // indentation
    }

    @Test
    void metricToMap_convertsAllFields() throws IOException {
        // ARRANGE
        Metric metric = new Metric();
        metric.setId(123L);
        metric.setArtifactType(ArtifactType.CLASS);
        metric.setPackageName("com.example");
        metric.setClassName("TestClass");
        metric.setMethodName("testMethod");
        metric.setLoc(100);
        metric.setNumAttributes(5);
        metric.setNumMethods(10);
        metric.setCyclomaticComplexity(8);
        metric.setCbo(3);
        metric.setDit(2);
        metric.setNoc(1);
        metric.setReport(testReport);
        List<Metric> metrics = List.of(metric);

        // ACT
        byte[] result = exportService.exportToJSON(metrics);
        String json = new String(result);

        // ASSERT
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(json);
        JsonNode metricNode = jsonNode.get(0);

        // Verify all 13+ keys are present
        assertThat(metricNode.has("id")).isTrue();
        assertThat(metricNode.has("tipo")).isTrue();
        assertThat(metricNode.has("package")).isTrue();
        assertThat(metricNode.has("classe")).isTrue();
        assertThat(metricNode.has("metodo")).isTrue();
        assertThat(metricNode.has("loc")).isTrue();
        assertThat(metricNode.has("atributos")).isTrue();
        assertThat(metricNode.has("metodos")).isTrue();
        assertThat(metricNode.has("complexidade")).isTrue();
        assertThat(metricNode.has("cbo")).isTrue();
        assertThat(metricNode.has("dit")).isTrue();
        assertThat(metricNode.has("noc")).isTrue();
        assertThat(metricNode.has("criadoEm")).isTrue();

        // Verify values
        assertThat(metricNode.get("id").asLong()).isEqualTo(123L);
        assertThat(metricNode.get("loc").asInt()).isEqualTo(100);
        assertThat(metricNode.get("complexidade").asInt()).isEqualTo(8);
    }

    @Test
    void metricToMap_withNullMetric_handlesGracefully() throws IOException {
        // ARRANGE - using a minimal metric
        Metric metric = new Metric();
        metric.setArtifactType(ArtifactType.CLASS);
        List<Metric> metrics = List.of(metric);

        // ACT & ASSERT - should not throw NullPointerException
        byte[] result = exportService.exportToJSON(metrics);
        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }
}
