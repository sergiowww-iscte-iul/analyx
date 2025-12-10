package pt.iscteiul.analyx.repository;

import org.springframework.data.repository.CrudRepository;
import pt.iscteiul.analyx.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends CrudRepository<Project, Integer> {

	List<Project> findByUser_Name(String userName);

	Optional<Project> findByIdAndUser_Name(Integer id, String userName);
}
