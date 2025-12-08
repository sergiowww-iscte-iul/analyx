package pt.iscteiul.analyx.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.iscteiul.analyx.entity.Project;
import pt.iscteiul.analyx.service.ArtifactService;
import pt.iscteiul.analyx.service.ProjectService;

@Slf4j
@Component
@JobScope
public class RemoveCurrentArtifactsTasklet implements Tasklet {
	@Autowired
	private ArtifactService artifactService;

	@Autowired
	private ProjectService projectService;


	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info("Executing RemoveCurrentArtifactsTasklet");
		Long idProject = (Long) chunkContext.getStepContext().getJobParameters().get(BatchConstants.PARAM_ID_PROJECT);
		Project project = projectService.getProjectById(idProject);
		artifactService.deleteByProject(project);
		return RepeatStatus.FINISHED;
	}
}
