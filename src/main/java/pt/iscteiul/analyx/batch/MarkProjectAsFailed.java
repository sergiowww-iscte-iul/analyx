package pt.iscteiul.analyx.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.iscteiul.analyx.service.ProjectService;

@Slf4j
@Component
@JobScope
public class MarkProjectAsFailed implements ExceptionHandler {
	@Value(BatchConstants.PARAM_ID_PROJECT_EXPR)
	private Long idProject;

	@Autowired
	private ProjectService projectService;

	@Override
	public void handleException(RepeatContext context, Throwable throwable) {
		log.error("job failed: {}", throwable.getMessage(), throwable);
		projectService.markProjectAsFailed(idProject);
	}
}
