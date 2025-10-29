package IntegraServiciosBackend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import IntegraServiciosBackend.entity.Unidad;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, UUID> {
    Optional<Unidad> findByNombre(String nombre);
}
