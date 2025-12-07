package pt.iscteiul.analyx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pt.iscteiul.analyx.model.Project;
import pt.iscteiul.analyx.model.User;
import pt.iscteiul.analyx.repository.ProjectRepository;
import pt.iscteiul.analyx.repository.UserRepository;
import pt.iscteiul.analyx.model.Report;
import pt.iscteiul.analyx.repository.ReportRepository;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @GetMapping("/new")
    public String newProjectPage() {
        return "projects/new";
    }

    @PostMapping("/new")
    public String createProject(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            Authentication authentication,
            Model model) {

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            Project project = new Project();
            project.setUser(user);
            project.setName(name);
            project.setDescription(description);

            projectRepository.save(project);

            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "projects/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editProjectPage(@PathVariable Long id, Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        model.addAttribute("project", project);
        return "projects/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateProject(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            Authentication authentication,
            Model model) {

        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

            if (!project.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Acesso negado");
            }

            project.setName(name);
            project.setDescription(description);
            projectRepository.save(project);

            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "projects/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        projectRepository.delete(project);

        return "redirect:/dashboard";
    }

    @GetMapping("/{id}")
    public String viewProject(@PathVariable Long id, Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        List<Report> reports = reportRepository.findByProjectIdOrderByCreatedAtDesc(project.getId());

        model.addAttribute("project", project);
        model.addAttribute("reports", reports);

        return "projects/view";
    }
}