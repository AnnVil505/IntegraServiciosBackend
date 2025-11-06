package IntegraServiciosBackend.service;

import IntegraServiciosBackend.dto.register.ReservaRegisterDTO;
import IntegraServiciosBackend.dto.exit.ReservaExitDTO;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

public interface IReservaService {
    ReservaExitDTO crearReserva(ReservaRegisterDTO reserva) throws BadRequestException, ResourceNotFoundException;
    List<ReservaExitDTO> listarReservas();
    ReservaExitDTO buscarPorId(UUID id) throws ResourceNotFoundException;
    ReservaExitDTO cancelarReserva(UUID id) throws ResourceNotFoundException, BadRequestException;
}
