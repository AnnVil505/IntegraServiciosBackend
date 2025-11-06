package IntegraServiciosBackend.repository;

import IntegraServiciosBackend.entity.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, UUID> {
    
}