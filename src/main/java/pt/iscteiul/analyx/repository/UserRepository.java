package pt.iscteiul.analyx.repository;

import org.springframework.data.repository.CrudRepository;
import pt.iscteiul.analyx.entity.AppUser;

import java.util.Optional;

public interface UserRepository extends CrudRepository<AppUser, Integer> {
	Optional<AppUser> findByName(String name);
}
