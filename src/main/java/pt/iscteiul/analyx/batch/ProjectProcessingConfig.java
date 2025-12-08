package pt.iscteiul.analyx.batch;

import com.github.mauricioaniche.ck.CK;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.orm.jpa.JpaTransactionManager;
import pt.iscteiul.analyx.entity.Artifact;

import static pt.iscteiul.analyx.batch.BatchConstants.JOB_PROCESS_PROJECT;
import static pt.iscteiul.analyx.batch.BatchConstants.JOB_PROCESS_PROJECT_RESTART;

@Configuration
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ProjectProcessingConfig {
	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JpaTransactionManager transactionManager;

	@Autowired
	private BatchExceptionHandler batchExceptionHandler;

	@Bean
	public CK ck() {
		return new CK();
	}

	@Bean(JOB_PROCESS_PROJECT)
	public Job jobProcessProject(Step stepExtractZipFiles,
								 Step stepReadProjectFiles,
								 Step stepStartProject,
								 Step stepFinishProjectExecution
	) {
		return new JobBuilder(JOB_PROCESS_PROJECT, jobRepository)
				.start(stepStartProject)
				.next(stepExtractZipFiles)
				.next(stepReadProjectFiles)
				.next(stepFinishProjectExecution)
				.build();
	}

	@Bean(JOB_PROCESS_PROJECT_RESTART)
	public Job jobProcessProjectRestart(
			Step stepRemoveCurrentArtifacts,
			Step stepExtractZipFiles,
			Step stepReadProjectFiles,
			Step stepStartProject,
			Step stepFinishProjectExecution
	) {
		return new JobBuilder(JOB_PROCESS_PROJECT_RESTART, jobRepository)
				.start(stepStartProject)
				.next(stepRemoveCurrentArtifacts)
				.next(stepExtractZipFiles)
				.next(stepReadProjectFiles)
				.next(stepFinishProjectExecution)
				.build();
	}

	@Bean
	public Step stepRemoveCurrentArtifacts(RemoveCurrentArtifactsTasklet removeCurrentArtifactsTasklet) {
		return new StepBuilder("stepRemoveCurrentArtifacts", jobRepository)
				.tasklet(removeCurrentArtifactsTasklet, transactionManager)
				.exceptionHandler(batchExceptionHandler)
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
				.exceptionHandler(batchExceptionHandler)
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
				.usePersist(true)
				.build();
		return new StepBuilder("stepReadProjectFiles", jobRepository)
				.<FileSystemResource, Artifact>chunk(processingChunkFiles, transactionManager)
				.reader(projectFilesReader)
				.processor(new CompositeItemProcessorBuilder<FileSystemResource, Artifact>()
						.delegates(
								metricsExtractorProcessor,
								new ListUnpackingItemProcessor<Artifact>()
						)
						.build())
				.writer(writer)
				.exceptionHandler(batchExceptionHandler)
				.build();
	}

}
