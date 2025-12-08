package pt.iscteiul.analyx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.iscteiul.analyx.dto.ProjectDTO;
import pt.iscteiul.analyx.entity.ClassArtifact;
import pt.iscteiul.analyx.entity.MethodArtifact;
import pt.iscteiul.analyx.entity.Project;
import pt.iscteiul.analyx.service.ArtifactService;
import pt.iscteiul.analyx.service.ProjectBatchServiceManager;
import pt.iscteiul.analyx.service.ProjectService;
import pt.iscteiul.analyx.util.ControllerKeys;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Controller
@RequestMapping("/projects")
public class ProjectsController {
	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectBatchServiceManager projectBatchServiceManager;

	@Autowired
	private ArtifactService artifactService;

	@GetMapping("/dashboard")
	public String home(Authentication auth, Model model) {
		List<Project> projects = projectService.findAllByUserName(auth.getName());
		model.addAttribute("projects", projects);
		return "dashboard";
	}

	@GetMapping("/new")
	public String newProject(Model model) {
		model.addAttribute("projectDTO", ProjectDTO.builder().build());
		return "project";
	}

	@GetMapping("/{idProject}/view")
	public String viewProject(Model model, @PathVariable Integer idProject, Authentication auth) {
		String username = auth.getName();
		Project project = projectService.getProjectByIdAndUser(idProject, username);
		List<ClassArtifact> classArtifacts = artifactService.findByProject(idProject, username);
		model.addAttribute("classArtifacts", classArtifacts);
		long totalLinesOfCode = classArtifacts.stream()
				.flatMap(c -> c.getMethodsArtifact().stream())
				.collect(Collectors.summarizingInt(MethodArtifact::getLinesCode))
				.getSum();
		long totalMethods = classArtifacts.stream()
				.mapToLong(c -> c.getMethodsArtifact().size())
				.sum();
		model.addAttribute("totalMethods", totalMethods);

		double averageCyclomaticComplexity = classArtifacts.stream()
				.flatMap(c -> c.getMethodsArtifact().stream())
				.collect(Collectors.summarizingDouble(MethodArtifact::getCyclomaticComplexity))
				.getAverage();
		model.addAttribute("averageCyclomaticComplexity", averageCyclomaticComplexity);

		model.addAttribute("totalLinesOfCode", totalLinesOfCode);
		model.addAttribute("project", project);
		return "view";
	}

	@GetMapping("{idProject}/edit")
	public String editProject(Model model, @PathVariable Integer idProject) {
		ProjectDTO projectDTO = projectService.getProjectForEditing(idProject);
		model.addAttribute("projectDTO", projectDTO);
		return "project";
	}

	@PostMapping("{idProject}/delete")
	public String deleteProject(@PathVariable Integer idProject, RedirectAttributes redirectAttributes) throws Exception {
		projectBatchServiceManager.deleteProject(idProject);
		redirectAttributes.addFlashAttribute(ControllerKeys.INFO_MESSAGE, "Project %d deleted".formatted(idProject));
		return "redirect:/projects/dashboard";
	}


	@PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String uploadProject(
			@RequestPart(name = "projectFiles", required = false)
			MultipartFile projectFiles,
			ProjectDTO projectDTO,
			Authentication auth,
			RedirectAttributes redirectAttributes
	) throws Exception {

		Project project = projectService.saveProject(projectDTO, auth);
		if (isNull(projectDTO.id())) {
			projectService.startAnalysis(project, projectFiles);
			redirectAttributes.addFlashAttribute(ControllerKeys.INFO_MESSAGE, "Project uploaded");
		} else {
			projectService.uploadProject(project, projectFiles);
			redirectAttributes.addFlashAttribute(ControllerKeys.INFO_MESSAGE, "Project saved");
		}


		return "redirect:/projects/dashboard";
	}

	@PostMapping("/{idProject}/start-analysis")
	public String startAnalysis(@PathVariable Integer idProject, RedirectAttributes redirectAttributes) throws Exception {
		projectBatchServiceManager.restartAnalysis(idProject);
		redirectAttributes.addFlashAttribute(ControllerKeys.INFO_MESSAGE, "Project analysis started");

		return "redirect:/projects/dashboard";
	}
}
