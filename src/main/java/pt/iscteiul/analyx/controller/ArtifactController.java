package pt.iscteiul.analyx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.iscteiul.analyx.service.MetricsExtractorService;
import pt.iscteiul.analyx.util.ControllerKeys;

@Controller
public class ArtifactController {
	@Autowired
	private MetricsExtractorService metricsExtractorService;

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String uploadProject(
			@RequestPart(name = "projectFiles")
			MultipartFile projectFiles,
			RedirectAttributes redirectAttributes
	) {

		metricsExtractorService.addProject(projectFiles);
		redirectAttributes.addAttribute(ControllerKeys.INFO_MESSAGE, "Project uploaded");

		return "redirect:/";
	}
}
