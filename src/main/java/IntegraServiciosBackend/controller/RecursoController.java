package IntegraServiciosBackend.controller;

import IntegraServiciosBackend.dto.register.RecursoRegisterDTO;
import IntegraServiciosBackend.dto.modification.RecursoModificationDTO;
import IntegraServiciosBackend.dto.exit.RecursoExitDTO;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.service.imp.RecursoService;
import IntegraServiciosBackend.utils.JsonPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/recursos")
@CrossOrigin
public class RecursoController {

    private final Logger LOGGER = LoggerFactory.getLogger(RecursoController.class);
    private final RecursoService recursoService;

    public RecursoController(RecursoService recursoService) {
        this.recursoService = recursoService;
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> registrarRecurso(@RequestBody RecursoRegisterDTO recurso) throws BadRequestException {
        LOGGER.info("Recurso: "+ JsonPrinter.toString(recurso));
        return new ResponseEntity<>(recursoService.registrarRecurso(recurso), HttpStatus.OK);
    }

    @PutMapping("/actualizar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<RecursoExitDTO> actualizarRecurso(@Valid @RequestBody RecursoModificationDTO recurso) throws ResourceNotFoundException,BadRequestException {
        return new ResponseEntity<>(recursoService.actualizarRecurso(recurso), HttpStatus.OK);
    }

    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('USER','ADMIN','ALIADO')")
    public ResponseEntity<List<RecursoExitDTO>> listarRecursos() throws BadRequestException {
        LOGGER.info("Inicia endpoint listar recursos");
        return new ResponseEntity<>(recursoService.listarRecursos(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','ALIADO')")
    public ResponseEntity<RecursoExitDTO> obtenerRecursoPorId(@PathVariable Long id) throws ResourceNotFoundException{
        return new ResponseEntity<>(recursoService.buscarRecursoPorId(id), HttpStatus.OK);
    }

    @DeleteMapping("eliminar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> eliminarRecurso(@PathVariable Long id) throws ResourceNotFoundException {
        return new ResponseEntity<>(recursoService.eliminarRecurso(id), HttpStatus.OK);
    }
}