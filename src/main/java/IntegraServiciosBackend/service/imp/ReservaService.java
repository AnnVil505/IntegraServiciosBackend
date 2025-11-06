package IntegraServiciosBackend.service.imp;

import IntegraServiciosBackend.dto.register.ReservaRegisterDTO;
import IntegraServiciosBackend.dto.exit.ReservaExitDTO;
import IntegraServiciosBackend.entity.Reserva;
import IntegraServiciosBackend.entity.Recurso;
import IntegraServiciosBackend.entity.Usuario;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.repository.ReservaRepository;
import IntegraServiciosBackend.repository.RecursoRepository;
import IntegraServiciosBackend.repository.UsuarioRepository;
import IntegraServiciosBackend.service.IReservaService;
import IntegraServiciosBackend.utils.JsonPrinter;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ReservaService implements IReservaService {

    private final Logger LOGGER = LoggerFactory.getLogger(ReservaService.class);
    private final ReservaRepository reservaRepository;
    private final RecursoRepository recursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    @Override
    public ReservaExitDTO crearReserva(ReservaRegisterDTO dto) throws BadRequestException, ResourceNotFoundException {
        Recurso recurso = recursoRepository.findById(dto.getRecursoId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el recurso con id " + dto.getRecursoId()));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id " + dto.getUsuarioId()));

        List<Reserva> existentes = reservaRepository.findByRecurso_RecursoIdAndFecha(dto.getRecursoId(), dto.getFecha());
        boolean hayConflicto = existentes.stream().anyMatch(r ->
                dto.getInicio().isBefore(r.getFin()) && dto.getFin().isAfter(r.getInicio()));

        if (hayConflicto)
            throw new BadRequestException("El recurso ya está reservado en ese horario.");

        Reserva nueva = modelMapper.map(dto, Reserva.class);
        nueva.setEstado("pendiente");
        nueva.setRecurso(recurso);
        nueva.setUsuario(usuario);

        Reserva guardada = reservaRepository.save(nueva);
        LOGGER.info("Reserva creada: {}", JsonPrinter.toString(guardada));

        return modelMapper.map(guardada, ReservaExitDTO.class);
    }

    @Override
    public List<ReservaExitDTO> listarReservas() {
        return reservaRepository.findAll().stream()
                .map(r -> modelMapper.map(r, ReservaExitDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReservaExitDTO buscarPorId(UUID id) throws ResourceNotFoundException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la reserva con id " + id));
        return modelMapper.map(reserva, ReservaExitDTO.class);
    }

    @Override
    public ReservaExitDTO cancelarReserva(UUID id) throws ResourceNotFoundException, BadRequestException {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la reserva con id " + id));

        if (!"pendiente".equalsIgnoreCase(reserva.getEstado())) {
            throw new BadRequestException("Solo las reservas pendientes pueden cancelarse.");
        }

        reserva.setEstado("cancelada");
        Reserva actualizada = reservaRepository.save(reserva);
        LOGGER.warn("Reserva cancelada: {}", JsonPrinter.toString(actualizada));

        return modelMapper.map(actualizada, ReservaExitDTO.class);
    }
}
