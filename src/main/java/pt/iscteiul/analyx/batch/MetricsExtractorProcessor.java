package pt.iscteiul.analyx.batch;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKMethodResult;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import pt.iscteiul.analyx.entity.Artifact;
import pt.iscteiul.analyx.entity.ClassArtifact;
import pt.iscteiul.analyx.entity.MethodArtifact;
import pt.iscteiul.analyx.entity.Project;
import pt.iscteiul.analyx.service.ProjectService;

import java.util.ArrayList;
import java.util.List;

@StepScope
@Component
public class MetricsExtractorProcessor implements ItemProcessor<FileSystemResource, List<Artifact>> {
	@Autowired
	private CK ck;

	@Value(BatchConstants.PARAM_ID_PROJECT_EXPR)
	private Long idProject;

	@Autowired
	private ProjectService projectService;

	@Override
	public List<Artifact> process(FileSystemResource item) {
		Project project = projectService.getProjectById(idProject);
		String javaClassFile = item.getPath();
		List<Artifact> artifacts = new ArrayList<>();
		ck.calculate(javaClassFile, result -> {
			ClassArtifact classArtifact = new ClassArtifact();
			classArtifact.setProject(project);
			classArtifact.setName(result.getClassName());
			classArtifact.setLinesCode(result.getLoc());
			classArtifact.setCbo(result.getCbo());
			classArtifact.setDit(result.getDit());
			classArtifact.setNoc(result.getNoc());
			classArtifact.setNumberAttributes(result.getNumberOfFields());
			classArtifact.setFanIn(result.getFanin());
			classArtifact.setFanOut(result.getFanout());
			artifacts.add(classArtifact);

			artifacts.addAll(result.getMethods().stream()
					.map(m -> getCkMethodResultMethodArtifactFunction(m, classArtifact, project))
					.toList());

		});
		return artifacts;
	}

	private MethodArtifact getCkMethodResultMethodArtifactFunction(CKMethodResult m, ClassArtifact classArtifact, Project project) {
		MethodArtifact methodArtifact = new MethodArtifact();
		methodArtifact.setName(m.getMethodName());
		methodArtifact.setClassArtifact(classArtifact);
		methodArtifact.setCyclomaticComplexity(m.getWmc());
		methodArtifact.setLinesCode(m.getLoc());
		methodArtifact.setProject(project);
		return methodArtifact;
	}
}
