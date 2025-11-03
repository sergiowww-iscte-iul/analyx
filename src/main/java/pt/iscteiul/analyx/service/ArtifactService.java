package pt.iscteiul.analyx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.iscteiul.analyx.entity.Artifact;
import pt.iscteiul.analyx.repository.ArtifactRepository;

import java.util.List;

@Service
public class ArtifactService {

	@Autowired
	private ArtifactRepository artifactRepository;

	public List<Artifact> findAll() {
		return artifactRepository.findAll();
	}
}
