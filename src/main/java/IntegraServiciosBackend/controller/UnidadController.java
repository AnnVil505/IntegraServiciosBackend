package IntegraServiciosBackend.controller;

import IntegraServiciosBackend.dto.register.UnidadRegisterDTO;
import IntegraServiciosBackend.dto.modification.UnidadModificationDTO;
import IntegraServiciosBackend.dto.exit.UnidadExitDTO;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.service.IUnidadService;
import IntegraServiciosBackend.utils.JsonPrinter;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("public/unidad")
@AllArgsConstructor
public class UnidadController {

    private final Logger LOGGER = LoggerFactory.getLogger(UnidadController.class);
    private final IUnidadService unidadService;

    /**
     * Registrar una nueva unidad
     */
    @PostMapping
    public ResponseEntity<UnidadExitDTO> registrarUnidad(@RequestBody UnidadRegisterDTO UnidadRegisterDTO)
            throws BadRequestException {
        LOGGER.info("Solicitud de registro recibida: {}", JsonPrinter.toString(UnidadRegisterDTO));
        UnidadExitDTO unidadCreada = unidadService.registrarUnidad(UnidadRegisterDTO);
        LOGGER.info("Unidad registrada correctamente: {}", JsonPrinter.toString(unidadCreada));
        return ResponseEntity.ok(unidadCreada);
    }

    /**
     * Listar todas las unidades
     */
    @GetMapping
    public ResponseEntity<List<UnidadExitDTO>> listarUnidades() {
        List<UnidadExitDTO> unidades = unidadService.listarUnidades();
        LOGGER.info("Se retornaron {} unidades", unidades.size());
        return ResponseEntity.ok(unidades);
    }

    /**
     * Buscar una unidad por su ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UnidadExitDTO> buscarPorId(@PathVariable UUID id)
            throws ResourceNotFoundException {
        LOGGER.info("Buscando unidad con id: {}", id);
        UnidadExitDTO unidad = unidadService.buscarUnidadPorId(id);
        return ResponseEntity.ok(unidad);
    }

    /**
     * Actualizar una unidad existente
     */
    @PutMapping
    public ResponseEntity<UnidadExitDTO> actualizarUnidad(@RequestBody UnidadModificationDTO UnidadModificationDTO)
            throws ResourceNotFoundException, BadRequestException {
        LOGGER.info("Solicitud de actualización recibida: {}", JsonPrinter.toString(UnidadModificationDTO));
        UnidadExitDTO actualizada = unidadService.actualizarUnidad(UnidadModificationDTO);
        LOGGER.info("Unidad actualizada correctamente: {}", JsonPrinter.toString(actualizada));
        return ResponseEntity.ok(actualizada);
    }

    /**
     * Eliminar una unidad por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<UnidadExitDTO> eliminarUnidad(@PathVariable UUID id)
            throws ResourceNotFoundException {
        LOGGER.warn("Solicitud de eliminación para unidad con id: {}", id);
        UnidadExitDTO eliminada = unidadService.eliminarUnidad(id);
        LOGGER.warn("Unidad eliminada correctamente: {}", JsonPrinter.toString(eliminada));
        return ResponseEntity.ok(eliminada);
    }
}
