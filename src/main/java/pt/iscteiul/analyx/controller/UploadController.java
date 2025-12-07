package pt.iscteiul.analyx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.iscteiul.analyx.model.Project;
import pt.iscteiul.analyx.model.Report;
import pt.iscteiul.analyx.model.ReportStatus;
import pt.iscteiul.analyx.model.User;
import pt.iscteiul.analyx.repository.ProjectRepository;
import pt.iscteiul.analyx.repository.ReportRepository;
import pt.iscteiul.analyx.repository.UserRepository;
import pt.iscteiul.analyx.service.AnalysisService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/projects/{projectId}")
public class UploadController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/upload")
    public String uploadPage(@PathVariable Long projectId, Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        model.addAttribute("project", project);
        return "upload/form";
    }

    @PostMapping("/upload")
    public String handleUpload(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String version,
            Authentication authentication,
            Model model) {

        try {
            // Validações
            if (file.isEmpty()) {
                throw new RuntimeException("Arquivo não selecionado!");
            }

            if (!file.getOriginalFilename().endsWith(".zip")) {
                throw new RuntimeException("Apenas arquivos .zip são permitidos!");
            }

            // Verificar usuário e projeto
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

            if (!project.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Acesso negado");
            }

            // Criar diretório temporário para este upload
            Path uploadDirPath = Paths.get("/tmp", "analyx", "uploads", projectId.toString());

            // Criar diretórios se não existirem
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }

            // Salvar arquivo
            String fileName = file.getOriginalFilename();
            Path filePath = uploadDirPath.resolve(fileName);

            // Transferir arquivo
            Files.copy(file.getInputStream(), filePath);

            // Criar Report no banco
            Report report = new Report();
            report.setProject(project);
            report.setFileName(fileName);
            report.setVersion(version);
            report.setStatus(ReportStatus.PROCESSING);

            reportRepository.save(report);

            analysisService.processReport(report.getId(), filePath);

            return "redirect:/projects/" + projectId + "?uploaded=true";

        } catch (Exception e) {
            e.printStackTrace(); // Ver erro no console
            model.addAttribute("error", "Erro ao processar upload: " + e.getMessage());
            model.addAttribute("project", projectRepository.findById(projectId).orElse(null));
            return "upload/form";
        }
    }
}