package pt.iscteiul.analyx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import pt.iscteiul.analyx.entity.ClassArtifact;
import pt.iscteiul.analyx.entity.Project;

import java.util.List;

public interface ClassArtifactRepository extends JpaRepository<ClassArtifact, Integer> {
	@Modifying
	void deleteByProject(Project project);

	@Query("""
			select
				c
			from
				ClassArtifact c
				inner join fetch c.methodsArtifact
				inner join c.project p
				inner join p.user u
			where
				p.id = :idProject and
				u.name = :username
			"""
	)
	List<ClassArtifact> findByProjectIdAndUser(Integer idProject, String username);
}
