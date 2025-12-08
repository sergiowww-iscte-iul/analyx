package pt.iscteiul.analyx.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.iscteiul.analyx.service.ProjectService;

@Component
@JobScope
public class FinishProjectTasklet implements Tasklet {
	@Autowired
	private ProjectService projectService;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		Long idProject = (Long) chunkContext.getStepContext().getJobParameters().get(BatchConstants.PARAM_ID_PROJECT);
		projectService.markProjectAsAnalysisFinished(idProject);
		return RepeatStatus.FINISHED;
	}
}
