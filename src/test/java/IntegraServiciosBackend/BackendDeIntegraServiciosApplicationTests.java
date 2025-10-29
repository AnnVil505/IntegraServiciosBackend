package IntegraServiciosBackend;

import IntegraServiciosBackend.dto.register.UnidadRegisterDTO;
import IntegraServiciosBackend.dto.modification.UnidadModificationDTO;
import IntegraServiciosBackend.dto.exit.UnidadExitDTO;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.service.imp.UnidadService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // asegura el orden de ejecución
class BackendDeIntegraServiciosApplicationTests {

    @Autowired
    private UnidadService unidadService;

    private static UUID unidadIdCreada;

    @Test
    @Order(1)
    void registrarUnidadTest() throws BadRequestException {
        UnidadRegisterDTO dto = new UnidadRegisterDTO();
        dto.setNombre("Laboratorio de electrica");
        dto.setDescripcion("Sala equipada para experimentos de electrica");
        dto.setTiempoMinimoMinutos(60);

		Map<String, Object> horario = new HashMap<>();
    	horario.put("lun", "08:00-12:00");
    	horario.put("mar", "10:00-16:00");
    	dto.setHorarioGlobal(horario);

        UnidadExitDTO result = unidadService.registrarUnidad(dto);

        assertNotNull(result.getUnidadId());
        assertEquals("Laboratorio de electrica", result.getNombre());

        unidadIdCreada = result.getUnidadId();
        System.out.println("✅ Unidad registrada con ID: " + unidadIdCreada);
    }

    @Test
    @Order(2)
    void listarUnidadesTest() {
        List<UnidadExitDTO> unidades = unidadService.listarUnidades();
        assertFalse(unidades.isEmpty());
        System.out.println("✅ Se listaron " + unidades.size() + " unidades");
    }

    @Test
    @Order(3)
    void buscarUnidadPorIdTest() throws ResourceNotFoundException {
        UnidadExitDTO unidad = unidadService.buscarUnidadPorId(unidadIdCreada);
        assertNotNull(unidad);
        assertEquals("Laboratorio de electrica", unidad.getNombre());
        System.out.println("✅ Unidad encontrada: " + unidad.getNombre());
    }

    @Test
    @Order(4)
    void actualizarUnidadTest() throws ResourceNotFoundException, BadRequestException {
        UnidadModificationDTO dto = new UnidadModificationDTO();
        dto.setUnidadId(unidadIdCreada);
        dto.setNombre("Laboratorio de Física Muy Avanzada");
        dto.setDescripcion("Actualizado para pruebas avanzadas");

		Map<String, Object> horario = new HashMap<>();
    	horario.put("lun", "08:00-12:00");
    	horario.put("mie", "10:00-16:00");
    	dto.setHorarioGlobal(horario);

        UnidadExitDTO actualizada = unidadService.actualizarUnidad(dto);

        assertEquals("Laboratorio de Física Muy Avanzada", actualizada.getNombre());
        System.out.println("✅ Unidad actualizada correctamente: " + actualizada.getNombre());
    }

    @Test
    @Order(5)
    void eliminarUnidadTest() throws ResourceNotFoundException {
        UnidadExitDTO eliminada = unidadService.eliminarUnidad(unidadIdCreada);
        assertEquals(unidadIdCreada, eliminada.getUnidadId());
        System.out.println("✅ Unidad eliminada correctamente: " + eliminada.getNombre());
    }

    @Test
    @Order(6)
    void buscarUnidadEliminadaTest() {
        assertThrows(ResourceNotFoundException.class, () -> {
            unidadService.buscarUnidadPorId(unidadIdCreada);
        });
        System.out.println("✅ Se validó que la unidad eliminada no existe");
    }
}

