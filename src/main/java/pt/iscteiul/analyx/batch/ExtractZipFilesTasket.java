package pt.iscteiul.analyx.batch;

import org.apache.commons.io.IOUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.iscteiul.analyx.entity.Project;
import pt.iscteiul.analyx.service.ProjectService;
import pt.iscteiul.analyx.service.WorkspaceService;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
@JobScope
public class ExtractZipFilesTasket implements Tasklet {

	@Autowired
	private WorkspaceService workspaceService;

	@Autowired
	private ProjectService projectService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Long idProject = (Long) chunkContext.getStepContext().getJobParameters().get(BatchConstants.PARAM_ID_PROJECT);
		Project project = projectService.getProjectById(idProject);
		Path projectZipFile = workspaceService.getProjectZipFile(project);
		Path projectFolder = workspaceService.getProjectFolder(project);
		workspaceService.deleteProjectFolder(project);
		Files.createDirectories(projectFolder);

		try (InputStream fis = Files.newInputStream(projectZipFile);
			 ZipInputStream zis = new ZipInputStream(fis)) {

			ZipEntry entry;
			// Loop through all entries in the zip file
			while ((entry = zis.getNextEntry()) != null) {
				Path newPath = projectFolder.resolve(entry.getName());

				if (entry.isDirectory()) {
					// Create the directory if it's a directory entry
					Files.createDirectories(newPath);
				} else {
					// Ensure parent directory structure exists for the file
					Files.createDirectories(newPath.getParent());

					// Write the file content
					try (var outputStream = Files.newOutputStream(newPath)) {
						IOUtils.copy(zis, outputStream);
					}
				}
				zis.closeEntry();
			}
		}

		return RepeatStatus.FINISHED;
	}
}
