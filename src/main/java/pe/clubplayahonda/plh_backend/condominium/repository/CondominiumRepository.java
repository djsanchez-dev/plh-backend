package pe.clubplayahonda.plh_backend.condominium.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.clubplayahonda.plh_backend.condominium.model.Condominium;
import pe.clubplayahonda.plh_backend.condominium.model.CondominiumStatus;

public interface CondominiumRepository extends JpaRepository<Condominium, UUID> {

    boolean existsByCodeIgnoreCase(String code);

    Optional<Condominium> findByCodeIgnoreCase(String code);

    List<Condominium> findAllByOrderByCodeAsc();

    long countByStatus(CondominiumStatus status);
}
