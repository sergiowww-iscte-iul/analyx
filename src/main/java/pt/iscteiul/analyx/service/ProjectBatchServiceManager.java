package pt.iscteiul.analyx.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pt.iscteiul.analyx.batch.BatchConstants;

import java.util.Date;

@Service
public class ProjectBatchServiceManager {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier(BatchConstants.JOB_PROCESS_PROJECT)
	private Job jobProcessProject;


	public void startAnalysis(Integer idProject) throws Exception {

		Date executionDate = new Date();
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong(BatchConstants.PARAM_ID_PROJECT, idProject.longValue(), false)
				.addDate("executionTime", executionDate)
				.toJobParameters();
		jobLauncher.run(jobProcessProject, jobParameters);
	}
}
