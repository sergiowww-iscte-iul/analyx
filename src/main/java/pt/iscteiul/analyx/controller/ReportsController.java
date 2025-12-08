package pt.iscteiul.analyx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pt.iscteiul.analyx.entity.ClassArtifact;
import pt.iscteiul.analyx.service.ArtifactService;
import pt.iscteiul.analyx.service.ExportService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reports")
public class ReportsController {

	@Autowired
	private ExportService exportService;

	@Autowired
	private ArtifactService artifactService;

	@GetMapping(value = "/{idProject}/csv", produces = "text/csv")
	@ResponseBody
	public ByteArrayResource exportToCSV(@PathVariable Integer idProject, Authentication auth) {
		List<ClassArtifact> classArtifacts = artifactService.findByProject(idProject, auth.getName());
		return exportService.exportToCSV(classArtifacts);
	}

	@GetMapping(value = "/{idProject}/json", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<Map<String, Object>> exportToJSON(@PathVariable Integer idProject, Authentication auth) {
		List<ClassArtifact> classArtifacts = artifactService.findByProject(idProject, auth.getName());
		return exportService.exportToJSON(classArtifacts);
	}
}
