package pt.iscteiul.analyx.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;
import pt.iscteiul.analyx.model.Metric;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExportService {

    public byte[] exportToCSV(List<Metric> metrics) {
        StringBuilder csv = new StringBuilder();

        // Cabeçalho
        csv.append("Tipo,Package,Classe,Método,LOC,Atributos,Métodos,Complexidade,CBO,DIT,NOC\n");

        // Dados
        for (Metric metric : metrics) {
            csv.append(metric.getArtifactType()).append(",");
            csv.append(escapeCSV(metric.getPackageName())).append(",");
            csv.append(escapeCSV(metric.getClassName())).append(",");
            csv.append(escapeCSV(metric.getMethodName())).append(",");
            csv.append(metric.getLoc() != null ? metric.getLoc() : "").append(",");
            csv.append(metric.getNumAttributes() != null ? metric.getNumAttributes() : "").append(",");
            csv.append(metric.getNumMethods() != null ? metric.getNumMethods() : "").append(",");
            csv.append(metric.getCyclomaticComplexity() != null ? metric.getCyclomaticComplexity() : "").append(",");
            csv.append(metric.getCbo() != null ? metric.getCbo() : "").append(",");
            csv.append(metric.getDit() != null ? metric.getDit() : "").append(",");
            csv.append(metric.getNoc() != null ? metric.getNoc() : "").append("\n");
        }

        return csv.toString().getBytes();
    }

    public byte[] exportToJSON(List<Metric> metrics) throws IOException {
        // Converter para DTO simples (evitar referências circulares)
        List<Map<String, Object>> simplifiedMetrics = metrics.stream()
                .map(this::metricToMap)
                .collect(Collectors.toList());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Suporte a LocalDateTime
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mapper.writeValue(outputStream, simplifiedMetrics);

        return outputStream.toByteArray();
    }

    private Map<String, Object> metricToMap(Metric metric) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", metric.getId());
        map.put("tipo", metric.getArtifactType());
        map.put("package", metric.getPackageName());
        map.put("classe", metric.getClassName());
        map.put("metodo", metric.getMethodName());
        map.put("loc", metric.getLoc());
        map.put("atributos", metric.getNumAttributes());
        map.put("metodos", metric.getNumMethods());
        map.put("complexidade", metric.getCyclomaticComplexity());
        map.put("cbo", metric.getCbo());
        map.put("dit", metric.getDit());
        map.put("noc", metric.getNoc());
        map.put("criadoEm", metric.getCreatedAt());
        return map;
    }

    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }
}