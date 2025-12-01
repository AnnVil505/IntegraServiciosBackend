package IntegraServiciosBackend.controller;

import IntegraServiciosBackend.dto.register.ReservaRegisterDTO;
import IntegraServiciosBackend.dto.modification.RecursoModificationDTO;
import IntegraServiciosBackend.dto.modification.ReservaModificationDTO;
import IntegraServiciosBackend.dto.exit.RecursoExitDTO;
import IntegraServiciosBackend.dto.exit.ReservaExitDTO;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.service.imp.ReservaService;
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
@RequestMapping("/reservas")
public class ReservaController {

    private final Logger LOGGER = LoggerFactory.getLogger(ReservaController.class);
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarReserva(@RequestBody ReservaRegisterDTO reserva) throws BadRequestException {
        LOGGER.info("Reserva: "+ JsonPrinter.toString(reserva));
        return new ResponseEntity<>(reservaService.registrarReserva(reserva), HttpStatus.OK);
        //return new ResponseEntity<>("Hecho", HttpStatus.OK);
    }
    @PutMapping("/actualizar")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ReservaExitDTO> actualizarReserva(@Valid @RequestBody ReservaModificationDTO ReservaModificationDTO) throws ResourceNotFoundException,BadRequestException {
        return new ResponseEntity<>(reservaService.actualizarReserva(ReservaModificationDTO), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/listar")
    public ResponseEntity<List<ReservaExitDTO>> listarRecursos() throws BadRequestException {
        return new ResponseEntity<>(reservaService.listarReservas(), HttpStatus.OK);
    }
    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ReservaExitDTO> obtenerReservaPorId(@PathVariable Long id) throws ResourceNotFoundException{
        return new ResponseEntity<>(reservaService.buscarReservaPorId(id), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<ReservaExitDTO>> listarReservasPorUsuario(@PathVariable Long id) throws BadRequestException {
        return new ResponseEntity<>(reservaService.listarReservasPorUsuario(id), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/recurso/{id}")
    public ResponseEntity<List<ReservaExitDTO>> listarReservasPorRecurso(@PathVariable Long id) throws BadRequestException {
        return new ResponseEntity<>(reservaService.listarReservasPorRecurso(id), HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @DeleteMapping("/cancelar/{id}")
    public ResponseEntity<?> eliminarReserva(@PathVariable Long id) throws ResourceNotFoundException {
        return new ResponseEntity<>(reservaService.cancelarReserva(id), HttpStatus.OK);
    }
}