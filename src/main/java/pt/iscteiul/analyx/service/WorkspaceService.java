package pt.iscteiul.analyx.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pt.iscteiul.analyx.entity.Project;
import pt.iscteiul.analyx.exception.BusinessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class WorkspaceService {
	public static final String APPLICATION_ZIP = "application/zip";
	public static final String PROJECT_FILE = "project-file";
	@Value("${analyx.project-files-directory}")
	private String projectsWorkspace;

	@SneakyThrows
	public void moveUploadedFileToWorkspace(Project project, MultipartFile projectFiles) {
		if (!APPLICATION_ZIP.equals(projectFiles.getContentType())) {
			throw new BusinessException("Invalid file type");
		}
		Path projectZipFile = getProjectZipFile(project);
		projectFiles.transferTo(projectZipFile);
	}

	@SneakyThrows
	public Path getProjectZipFile(Project project) {
		Path workspace = createWorkspace();
		return workspace.resolve("%s-%d.zip".formatted(PROJECT_FILE, project.getId()));
	}

	private Path createWorkspace() throws IOException {
		Path workspace = Paths.get(projectsWorkspace);
		if (!Files.exists(workspace)) {
			Files.createDirectories(workspace);
		}
		return workspace;
	}

	@SneakyThrows
	public Project deleteProjectFiles( Project project) {
		Path projectZipFile = getProjectZipFile(project);
		Files.deleteIfExists(projectZipFile);
		return project;
	}

	@SneakyThrows
	public Path getProjectFolder(Project project) {
		Path workspace = createWorkspace();
		Path projectFolder = workspace.resolve("%s-%d".formatted(PROJECT_FILE, project.getId()));
		if (!Files.exists(projectFolder)) {
			Files.createDirectories(projectFolder);
		}
		return projectFolder;
	}
}
