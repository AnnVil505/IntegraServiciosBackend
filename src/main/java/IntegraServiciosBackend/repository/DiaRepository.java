package IntegraServiciosBackend.repository;

import IntegraServiciosBackend.entity.Dia;
import IntegraServiciosBackend.entity.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaRepository extends CrudRepository<Dia,Long> {
    @Query("select u from Usuario u where u.email = ?1 and u.contraseña = ?2")
    Usuario findByEmail(String email,String contraseña);
}
