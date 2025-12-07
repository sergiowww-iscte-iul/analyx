package pt.iscteiul.analyx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.iscteiul.analyx.model.Metric;
import pt.iscteiul.analyx.model.Report;
import pt.iscteiul.analyx.model.ReportStatus;
import pt.iscteiul.analyx.repository.MetricRepository;
import pt.iscteiul.analyx.repository.ReportRepository;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class AnalysisService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private MetricRepository metricRepository;

    public void processReport(Long reportId, Path zipFilePath) {
        System.out.println("🔄 Iniciando processamento do report ID: " + reportId);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report não encontrado"));

        try {
            // Criar diretório para extração
            Path extractDir = Paths.get("/tmp/analyx/extracted", reportId.toString());
            Files.createDirectories(extractDir);
            System.out.println("📁 Diretório de extração criado: " + extractDir);

            // Descompactar o .zip
            List<Path> javaFiles = unzip(zipFilePath, extractDir);
            System.out.println("📦 Arquivos .java encontrados: " + javaFiles.size());

            if (javaFiles.isEmpty()) {
                throw new RuntimeException("Nenhum arquivo .java encontrado no .zip!");
            }

            // Analisar cada arquivo Java
            CodeAnalyzer analyzer = new CodeAnalyzer();
            int totalMetrics = 0;

            for (Path javaFile : javaFiles) {
                try {
                    List<CodeAnalyzer.MetricResult> results = analyzer.analyzeJavaFile(javaFile);

                    // Salvar métricas no banco
                    for (CodeAnalyzer.MetricResult result : results) {
                        Metric metric = new Metric();
                        metric.setReport(report);
                        metric.setArtifactType(result.type);
                        metric.setPackageName(result.packageName);
                        metric.setClassName(result.className);
                        metric.setMethodName(result.methodName);
                        metric.setLoc(result.loc);
                        metric.setNumMethods(result.numMethods);
                        metric.setNumAttributes(result.numAttributes);
                        metric.setCyclomaticComplexity(result.cyclomaticComplexity);
                        metric.setCbo(result.cbo);
                        metric.setDit(result.dit);
                        metric.setNoc(result.noc);

                        metricRepository.save(metric);
                        totalMetrics++;
                    }

                } catch (Exception e) {
                    System.err.println("   ⚠️ Erro ao analisar " + javaFile.getFileName() + ": " + e.getMessage());
                }
            }

            // Marcar como COMPLETED
            report.setStatus(ReportStatus.COMPLETED);
            report.setCompletedAt(java.time.LocalDateTime.now());
            reportRepository.save(report);

            // Limpar arquivos temporários
            deleteDirectory(extractDir);
            Files.deleteIfExists(zipFilePath);

        } catch (Exception e) {
            System.err.println("❌ Erro ao processar report: " + e.getMessage());
            e.printStackTrace();

            report.setStatus(ReportStatus.FAILED);
            report.setCompletedAt(java.time.LocalDateTime.now());
            reportRepository.save(report);
        }
    }

    private List<Path> unzip(Path zipFilePath, Path destDir) throws IOException {
        List<Path> javaFiles = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath.toFile()))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                Path filePath = destDir.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(filePath);
                } else {
                    // Criar diretórios pai se necessário
                    Files.createDirectories(filePath.getParent());

                    // Extrair arquivo
                    Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Se for .java, adicionar à lista
                    if (entry.getName().endsWith(".java")) {
                        javaFiles.add(filePath);
                    }
                }

                zis.closeEntry();
            }
        }

        return javaFiles;
    }

    private void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                .sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        System.err.println("Erro ao deletar: " + path);
                    }
                });
        }
    }
}