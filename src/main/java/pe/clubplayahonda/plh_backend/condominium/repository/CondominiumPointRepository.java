package pe.clubplayahonda.plh_backend.condominium.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.clubplayahonda.plh_backend.condominium.model.CondominiumPoint;

public interface CondominiumPointRepository extends JpaRepository<CondominiumPoint, UUID> {

    List<CondominiumPoint> findAllByOrderByCodeAsc();

    boolean existsByCondominiumIdAndCodeIgnoreCase(UUID condominiumId, String code);

    long countByCondominiumId(UUID condominiumId);
}
