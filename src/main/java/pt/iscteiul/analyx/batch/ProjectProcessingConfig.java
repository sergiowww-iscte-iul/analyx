package pt.iscteiul.analyx.batch;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.JpaTransactionManager;
import pt.iscteiul.analyx.entity.Artifact;

@Configuration
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ProjectProcessingConfig {
	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JpaTransactionManager transactionManager;

	@Autowired
	private MarkProjectAsFailed markProjectAsFailed;

	@Bean(BatchConstants.JOB_PROCESS_PROJECT)
	public Job jobProcessProject(Step stepExtractZipFiles,
								 Step stepReadProjectFiles,
								 Step stepStartProject,
								 Step stepFinishProjectExecution
	) {
		return new JobBuilder(BatchConstants.JOB_PROCESS_PROJECT, jobRepository)
				.start(stepStartProject)
				.next(stepExtractZipFiles)
				.next(stepReadProjectFiles)
				.next(stepFinishProjectExecution)
				.build();
	}

	@Bean
	public Step stepStartProject(StartProjectTasklet startProjectTasklet) {
		return new StepBuilder("stepStartProject", jobRepository)
				.tasklet(startProjectTasklet, transactionManager)
				.build();
	}

	@Bean
	public Step stepFinishProjectExecution(FinishProjectTasklet startProjectTasklet) {
		return new StepBuilder("stepFinishProjectExecution", jobRepository)
				.tasklet(startProjectTasklet, transactionManager)
				.build();
	}

	@Bean
	public Step stepExtractZipFiles(ExtractZipFilesTasket extractZipFiles) {
		return new StepBuilder("stepExtractZipFiles", jobRepository)
				.tasklet(extractZipFiles, transactionManager)
				.exceptionHandler(markProjectAsFailed)
				.build();
	}


	@Bean
	public Step stepReadProjectFiles(
			MetricsExtractorProcessor metricsExtractorProcessor,
			EntityManagerFactory entityManagerFactory,
			ProjectFileReader projectFilesReader,

			@Value("${analyx.process-x-files-at-time}")
			int processingChunkFiles
	) {
		JpaItemWriter<Artifact> writer = new JpaItemWriterBuilder<Artifact>()
				.entityManagerFactory(entityManagerFactory)
				.usePersist(false)
				.build();
		return new StepBuilder("stepReadProjectFiles", jobRepository)
				.<FileSystemResource, Artifact>chunk(processingChunkFiles, transactionManager)
				.reader(projectFilesReader)
				.processor(metricsExtractorProcessor)
				.writer(writer)
				.exceptionHandler(markProjectAsFailed)
				.build();
	}

}
