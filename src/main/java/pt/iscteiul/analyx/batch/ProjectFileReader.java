package pt.iscteiul.analyx.batch;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import pt.iscteiul.analyx.entity.Project;
import pt.iscteiul.analyx.service.ProjectService;
import pt.iscteiul.analyx.service.WorkspaceService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

@Component
@StepScope
public class ProjectFileReader implements ItemReader<FileSystemResource> {
	@Autowired
	private WorkspaceService workspaceService;

	@Autowired
	private ProjectService projectService;

	@Value(BatchConstants.PARAM_ID_PROJECT_EXPR)
	private Long idProject;

	private boolean recordsLoaded = false;

	private final Queue<FileSystemResource> buffer = new ArrayDeque<>();

	@Override
	public FileSystemResource read() throws IOException {
		if (recordsLoaded) {
			return buffer.poll();
		}

		recordsLoaded = true;
		Project project = projectService.getProjectById(idProject);
		Path projectFolder = workspaceService.getProjectFolder(project);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources("file:" + projectFolder.toString() + "/**/*.java");
		buffer.addAll(
				Arrays.stream(resources)
						.map(FileSystemResource.class::cast)
						.toList()
		);
		return buffer.poll();
	}


}
