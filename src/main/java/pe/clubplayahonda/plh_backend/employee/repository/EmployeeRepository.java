package pe.clubplayahonda.plh_backend.employee.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.clubplayahonda.plh_backend.employee.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    List<Employee> findAllByOrderByLastNameAscFirstNameAsc();

    Optional<Employee> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
