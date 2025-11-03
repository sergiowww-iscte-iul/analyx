package pt.iscteiul.analyx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.analyx.entity.Artifact;

public interface ArtifactRepository extends JpaRepository<Artifact, Integer> {

}
