package pt.iscteiul.analyx.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import pt.iscteiul.analyx.entity.Artifact;
import pt.iscteiul.analyx.entity.ClassArtifact;
import pt.iscteiul.analyx.entity.MethodArtifact;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ExportService {

	public static final String COMMA = ",";

	public ByteArrayResource exportToCSV(List<ClassArtifact> metrics) {

		// Dados
		byte[] csvData = metrics.stream()
				.flatMap(c -> c.getMethodsArtifact().stream()
						.map(m ->
								Stream.of(
												c.getName(),
												m.getName(),
												m.getLinesCode(),
												c.getNumberAttributes(),
												c.getMethodsArtifact().size(),
												m.getCyclomaticComplexity(),
												c.getCbo(),
												c.getDit(),
												c.getNoc()
										)
										.map(String::valueOf)
										.map(this::escapeCSV)
										.collect(Collectors.joining(COMMA))
						))
				.collect(Collectors.joining(System.lineSeparator(), "Classe,Método,LOC,Atributos,Métodos,Complexidade,CBO,DIT,NOC", ""))
				.getBytes(StandardCharsets.UTF_8);

		return new ByteArrayResource(csvData);
	}

	public List<Map<String, Object>> exportToJSON(List<ClassArtifact> metrics) {


		return metrics.stream()
				.flatMap(c -> Stream.concat(Stream.of(c), c.getMethodsArtifact().stream()))
				.map(this::metricToMap)
				.collect(Collectors.toList());
	}

	private Map<String, Object> metricToMap(Artifact metric) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", metric.getId());
		map.put("loc", metric.getLinesCode());
		if (metric instanceof ClassArtifact c) {
			map.put("classe", c.getName());
			map.put("atributos", c.getNumberAttributes());
			map.put("metodos", c.getMethodsArtifact().size());

			map.put("cbo", c.getCbo());
			map.put("dit", c.getDit());
			map.put("noc", c.getNoc());

		}
		if (metric instanceof MethodArtifact m) {
			map.put("metodo", m.getName());
			map.put("complexidade", m.getCyclomaticComplexity());

		}
		return map;
	}

	private String escapeCSV(String value) {
		if (value == null) {
			return "";
		}

		if (value.contains(COMMA) || value.contains("\"") || value.contains("\n")) {
			return "\"" + value.replace("\"", "\"\"") + "\"";
		}

		return value;
	}
}