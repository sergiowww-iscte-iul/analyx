package pt.iscteiul.analyx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import pt.iscteiul.analyx.entity.MethodArtifact;
import pt.iscteiul.analyx.entity.Project;

public interface MethodArtifactRepository extends JpaRepository<MethodArtifact, Integer> {
	@Modifying
	void deleteByProject(Project project);
}
