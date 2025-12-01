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

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/unidades")
public class UnidadController {

    private final Logger LOGGER = LoggerFactory.getLogger(UnidadController.class);
    private final UnidadService unidadService;

    public UnidadController(UnidadService unidadService) {
        this.unidadService = unidadService;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUnidad(@RequestBody UnidadRegisterDTO unidad) throws BadRequestException {
        LOGGER.info("Unidad: "+ JsonPrinter.toString(unidad));
        return new ResponseEntity<>(unidadService.registrarUnidad(unidad), HttpStatus.OK);
        //return new ResponseEntity<>("Hecho", HttpStatus.OK);
    }

    @PutMapping("actualizar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UnidadExitDTO> actualizarUnidad(@Valid @RequestBody UnidadModificationDTO unidad) throws ResourceNotFoundException,BadRequestException {
        return new ResponseEntity<>(unidadService.actualizarUnidad(unidad), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/listar")
    public ResponseEntity<List<UnidadExitDTO>> listarUnidades() throws BadRequestException {
        return new ResponseEntity<>(unidadService.listarUnidades(), HttpStatus.OK);
        //return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','ALIADO')")
    public ResponseEntity<UnidadExitDTO> obtenerUnidadPorId(@PathVariable Long id) throws ResourceNotFoundException{
        return new ResponseEntity<>(unidadService.buscarUnidadPorId(id), HttpStatus.OK);
    }

    @DeleteMapping("eliminar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> eliminarUnidad(@PathVariable Long id) throws ResourceNotFoundException {
        return new ResponseEntity<>(unidadService.eliminarUnidad(id), HttpStatus.OK);
    }
}