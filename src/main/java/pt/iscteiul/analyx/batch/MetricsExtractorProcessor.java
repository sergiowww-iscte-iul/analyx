package pt.iscteiul.analyx.batch;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import pt.iscteiul.analyx.entity.Artifact;

@StepScope
@Component
public class MetricsExtractorProcessor implements ItemProcessor<FileSystemResource, Artifact> {
	@Override
	public Artifact process(FileSystemResource item) throws Exception {
		return null;
	}
}
