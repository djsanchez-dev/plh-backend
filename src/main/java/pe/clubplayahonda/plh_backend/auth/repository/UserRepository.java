package pe.clubplayahonda.plh_backend.auth.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.clubplayahonda.plh_backend.auth.model.User;
import pe.clubplayahonda.plh_backend.auth.model.UserRole;

public interface UserRepository extends JpaRepository<User, UUID> {

	Optional<User> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);

	List<User> findByRoleOrderByLastNameAscFirstNameAsc(UserRole role);

	List<User> findByEnabledFalseOrderByCreatedAtAsc();

	long countByRole(UserRole role);

	long countByEnabled(boolean enabled);

	/** Miembros de la directiva: rol BOARD o cualquier usuario marcado como miembro. */
	@Query("select count(u) from User u where u.role = :role or u.boardMember = true")
	long countByRoleOrBoardMember(@Param("role") UserRole role);
}
