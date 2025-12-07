package pt.iscteiul.analyx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.analyx.model.Report;
import pt.iscteiul.analyx.model.User;
import pt.iscteiul.analyx.repository.ReportRepository;
import pt.iscteiul.analyx.repository.UserRepository;
import pt.iscteiul.analyx.model.ArtifactType;
import pt.iscteiul.analyx.model.Metric;
import pt.iscteiul.analyx.repository.MetricRepository;
import pt.iscteiul.analyx.service.ExportService;

import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MetricRepository metricRepository;

    @Autowired
    private ExportService exportService;

    @PostMapping("/{id}/delete")
    public String deleteReport(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado"));

        if (!report.getProject().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        Long projectId = report.getProject().getId();

        reportRepository.delete(report);

        System.out.println("🗑️ Report ID " + id + " deletado");

        return "redirect:/projects/" + projectId;
    }

    @GetMapping("/{id}")
    public String viewReport(@PathVariable Long id, Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado"));

        if (!report.getProject().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        List<Metric> metrics = metricRepository.findByReportId(id);

        List<Metric> packageMetrics = metrics.stream()
                .filter(m -> m.getArtifactType() == ArtifactType.PACKAGE)
                .toList();

        List<Metric> classMetrics = metrics.stream()
                .filter(m -> m.getArtifactType() == ArtifactType.CLASS)
                .toList();

        List<Metric> methodMetrics = metrics.stream()
                .filter(m -> m.getArtifactType() == ArtifactType.METHOD)
                .toList();

        model.addAttribute("report", report);
        model.addAttribute("project", report.getProject());
        model.addAttribute("allMetrics", metrics);
        model.addAttribute("packageMetrics", packageMetrics);
        model.addAttribute("classMetrics", classMetrics);
        model.addAttribute("methodMetrics", methodMetrics);

        return "reports/view";
    }

    @GetMapping("/{id}/export/csv")
    @ResponseBody
    public ResponseEntity<byte[]> exportCSV(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado"));

        if (!report.getProject().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        List<Metric> metrics = metricRepository.findByReportId(id);
        byte[] csvData = exportService.exportToCSV(metrics);

        String fileName = "metricas_" + report.getFileName().replace(".zip", "") + ".csv";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
    }

    @GetMapping("/{id}/export/json")
    @ResponseBody
    public ResponseEntity<byte[]> exportJSON(@PathVariable Long id, Authentication authentication) throws IOException {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado"));

        if (!report.getProject().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        List<Metric> metrics = metricRepository.findByReportId(id);
        byte[] jsonData = exportService.exportToJSON(metrics);

        String fileName = "metricas_" + report.getFileName().replace(".zip", "") + ".json";

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");

        return ResponseEntity.ok()
                .headers(headers)
                .body(jsonData);
    }
}