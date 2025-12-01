package IntegraServiciosBackend.service;

import IntegraServiciosBackend.dto.register.ReservaRegisterDTO;
import IntegraServiciosBackend.dto.modification.ReservaModificationDTO;
import IntegraServiciosBackend.dto.exit.ReservaExitDTO;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;

import java.util.List;

public interface IReservaService {

    Object registrarReserva(ReservaRegisterDTO reserva) throws BadRequestException;
    ReservaExitDTO actualizarReserva(ReservaModificationDTO ReservaModificationDTO) throws ResourceNotFoundException;
    List<ReservaExitDTO> listarReservas() throws BadRequestException;
    List<ReservaExitDTO> listarReservasPorUsuario(Long id);
    List<ReservaExitDTO> listarReservasPorRecurso(Long id);
    ReservaExitDTO buscarReservaPorId(Long id) throws ResourceNotFoundException;
    ReservaExitDTO cancelarReserva(Long id) throws ResourceNotFoundException;

}
