package pt.iscteiul.analyx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.iscteiul.analyx.entity.Project;
import pt.iscteiul.analyx.repository.ClassArtifactRepository;
import pt.iscteiul.analyx.repository.MethodArtifactRepository;

@Service
public class ArtifactService {
	@Autowired
	private MethodArtifactRepository methodArtifactRepository;

	@Autowired
	private ClassArtifactRepository classArtifactRepository;


	public Project deleteByProject(Project project) {
		methodArtifactRepository.deleteByProject(project);
		classArtifactRepository.deleteByProject(project);
		return project;
	}
}
