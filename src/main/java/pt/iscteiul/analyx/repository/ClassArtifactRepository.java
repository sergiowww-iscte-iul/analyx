package pt.iscteiul.analyx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import pt.iscteiul.analyx.entity.ClassArtifact;
import pt.iscteiul.analyx.entity.Project;

public interface ClassArtifactRepository extends JpaRepository<ClassArtifact, Integer> {
	@Modifying
	void deleteByProject(Project project);

}
