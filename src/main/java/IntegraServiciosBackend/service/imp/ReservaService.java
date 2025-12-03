package IntegraServiciosBackend.service.imp;

import IntegraServiciosBackend.dto.register.ReservaRegisterDTO;
import IntegraServiciosBackend.dto.modification.ReservaModificationDTO;
import IntegraServiciosBackend.dto.exit.ReservaExitDTO;
import IntegraServiciosBackend.dto.exit.RecursoExitDTO;
import IntegraServiciosBackend.dto.exit.UnidadExitDTO;
import IntegraServiciosBackend.dto.exit.UsuarioExitDTO;
import IntegraServiciosBackend.entity.*;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.repository.RecursoRepository;
import IntegraServiciosBackend.repository.ReservaRepository;
import IntegraServiciosBackend.repository.UnidadRepository;
import IntegraServiciosBackend.repository.UsuarioRepository;
import IntegraServiciosBackend.service.IReservaService;
import IntegraServiciosBackend.utils.JsonPrinter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Service
public class ReservaService implements IReservaService {

    private final Logger LOGGER = LoggerFactory.getLogger(ReservaService.class);
    private final ReservaRepository reservaRepository;
    private final RecursoRepository recursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadRepository unidadRepository;
    private final UnidadService unidadService;
    private ModelMapper modelMapper;

    @Override
    public Object registrarReserva(ReservaRegisterDTO reserva) {
        LOGGER.info("ReservaRegisterDTO: " + JsonPrinter.toString(reserva));

        Optional<Recurso> recursoOptional = recursoRepository.findById(reserva.getIdRecurso());
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(reserva.getIdUsuario());

        if (recursoOptional.isEmpty()) {
            LOGGER.error("El recurso con ID {} no existe.", reserva.getIdRecurso());
            throw new BadRequestException("El recurso no existe.");
        }
        if (usuarioOptional.isEmpty()) {
            LOGGER.error("El usuario con ID {} no existe.", reserva.getIdUsuario());
            throw new BadRequestException("El usuario no existe.");
        }

        Recurso recurso = recursoOptional.get();
        Usuario usuario = usuarioOptional.get();
        Unidad unidad = recurso.getUnidad();

        // 2. VALIDACIONES DE TIEMPO Y UNIDAD
        LocalTime horaInicio = LocalTime.parse(reserva.getHoraInicio());
        LocalTime horaFin = LocalTime.parse(reserva.getHoraFin());

        if (horaInicio.isAfter(horaFin) || horaInicio.equals(horaFin)) {
            LOGGER.error("Hora de inicio ({}) inválida respecto a hora de fin ({}).", horaInicio, horaFin);
            throw new BadRequestException("La hora de inicio debe ser estrictamente anterior a la hora de fin.");
        }

        // 2.1. Validar Duración (mínima y máxima)
        Duration duracionSolicitada = Duration.between(horaInicio, horaFin);
        Duration tiempoMinimo = Duration.ofMinutes(unidad.getTiempoMinimo());
        Duration tiempoMaximo = Duration.ofMinutes(unidad.getTiempoMaximo());

        if (duracionSolicitada.compareTo(tiempoMinimo) < 0) {
            LOGGER.error("Duración solicitada es menor al mínimo de la unidad ({} minutos).", unidad.getTiempoMinimo());
            throw new BadRequestException("La reserva debe durar al menos " + unidad.getTiempoMinimo() + " minutos.");
        }
        if (duracionSolicitada.compareTo(tiempoMaximo) > 0) {
            LOGGER.error("Duración solicitada es mayor al máximo de la unidad ({} minutos).", unidad.getTiempoMaximo());
            throw new BadRequestException("La reserva no puede durar más de " + unidad.getTiempoMaximo() + " minutos.");
        }

        // 2.2. Validar Disponibilidad General de la Unidad (Horario de Apertura/Cierre)
        LocalTime horaAperturaUnidad = LocalTime.parse(unidad.getHoraInicio());
        LocalTime horaCierreUnidad = LocalTime.parse(unidad.getHoraFinal());

        // Se verifica que la reserva inicie DESPUÉS de la apertura y finalice ANTES o IGUAL al cierre.
        if (horaInicio.isBefore(horaAperturaUnidad) || horaFin.isAfter(horaCierreUnidad)) {
            LOGGER.error("Horario solicitado ({}-{}) fuera del rango de la unidad ({}-{}).", horaInicio, horaFin, horaAperturaUnidad, horaCierreUnidad);
            throw new BadRequestException("El horario de la reserva está fuera del horario de disponibilidad de la unidad (" + unidad.getHoraInicio() + " a " + unidad.getHoraFinal() + ").");
        }

        // 2.4. Validar Día de la Semana 
        DayOfWeek dayOfWeek = reserva.getFechaReserva().getDayOfWeek();

        String nombreDiaSolicitado = dayOfWeek.getDisplayName(TextStyle.FULL, new Locale("es")).toLowerCase();

        boolean diaDisponible = unidad.getDiasDisponibles().stream()
                .anyMatch(d -> d.getNombre().toLowerCase().equals(nombreDiaSolicitado));

        if (!diaDisponible) {
            LOGGER.error("Día solicitado ({}) no está disponible en la unidad.", reserva.getFechaReserva().getDayOfWeek());
            throw new BadRequestException("El recurso no está disponible el día " + reserva.getFechaReserva().getDayOfWeek() + ".");
        }

        // 3. VALIDACIÓN DE SOLAPAMIENTO (Consulta a la BD)
        List<Reserva> solapadas = reservaRepository.findOverlappingReservations(
                reserva.getIdRecurso(),
                reserva.getFechaReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFin()
        );

        if (!solapadas.isEmpty()) {
            LOGGER.error("Recurso con id {} ya reservado en el horario {}-{} el {}", reserva.getIdRecurso(), reserva.getHoraInicio(), reserva.getHoraFin(), reserva.getFechaReserva());
            throw new BadRequestException("El recurso ya está reservado o en préstamo durante el horario solicitado.");
        }

        Reserva reservaEntidad = modelMapper.map(reserva, Reserva.class);

        reservaEntidad.setRecurso(recurso);
        reservaEntidad.setUsuario(usuario);
        reservaEntidad.setFechaCreacion(LocalDateTime.now()); 

        Reserva reservaGuardada = reservaRepository.save(reservaEntidad);

        ReservaExitDTO reservaexit = modelMapper.map(reservaGuardada, ReservaExitDTO.class);

        reservaexit.setUsuario(modelMapper.map(usuario, UsuarioExitDTO.class));
        reservaexit.setRecurso(modelMapper.map(recurso, RecursoExitDTO.class));

        LOGGER.info("Reserva registrada con éxito: {}", JsonPrinter.toString(reservaexit));
        return reservaexit;
    }

    @Override
    public ReservaExitDTO actualizarReserva(ReservaModificationDTO ReservaModificationDTO) throws ResourceNotFoundException {
        LOGGER.info("ReservaModificationDTO: " + JsonPrinter.toString(ReservaModificationDTO));

        Recurso recursoReserva = recursoRepository.findById(ReservaModificationDTO.getIdRecurso()).orElse(null);
        Usuario usuarioReserva = usuarioRepository.findById(ReservaModificationDTO.getIdUsuario()).orElse(null);

        if (recursoReserva == null) {
            throw new ResourceNotFoundException("El recurso no existe");
        }
        if (usuarioReserva == null) {
            throw new ResourceNotFoundException("El usuario no existe");
        }

        Reserva reservaAActualizar = modelMapper.map(ReservaModificationDTO, Reserva.class);
        reservaAActualizar.setUsuario(usuarioReserva);
        reservaAActualizar.setRecurso(recursoReserva);

        return modelMapper.map(reservaRepository.save(reservaAActualizar), ReservaExitDTO.class);
    }

    @Override
    public List<ReservaExitDTO> listarReservas() throws BadRequestException {
        List<ReservaExitDTO> reservas = reservaRepository.findAll().stream()
                .map(r -> modelMapper.map(r, ReservaExitDTO.class)).toList();

        for (ReservaExitDTO reserva : reservas) {
            reserva.setRecurso(modelMapper.map(recursoRepository.findById(reserva.getRecurso().getId()).orElse(null), RecursoExitDTO.class));
            reserva.setUsuario(modelMapper.map(usuarioRepository.findById(reserva.getUsuario().getId()).orElse(null), UsuarioExitDTO.class));
        }

        LOGGER.info("Listado de todas las reservas: {}", JsonPrinter.toString(reservas));

        return reservas;
    }

    @Override
    public List<ReservaExitDTO> listarReservasPorUsuario(Long id) {
        List<ReservaExitDTO> reservas = reservaRepository.findByUsuario_Id(id).stream()
                .map(r -> modelMapper.map(r, ReservaExitDTO.class)).toList();

        LOGGER.info("Reservas por Usuario");
        LOGGER.info(JsonPrinter.toString(reservas));
        return reservas;
    }

    @Override
    public List<ReservaExitDTO> listarReservasPorRecurso(Long id) {
        List<ReservaExitDTO> reservas = reservaRepository.findByRecurso_Id(id).stream()
                .map(r -> modelMapper.map(r, ReservaExitDTO.class)).toList();

        LOGGER.info("Reserva por recurso");
        LOGGER.info(JsonPrinter.toString(reservas));
        return reservas;
    }

    @Override
    public ReservaExitDTO buscarReservaPorId(Long id) throws ResourceNotFoundException {
        Reserva reservaBuscada = reservaRepository.findById(id).orElse(null);

        ReservaExitDTO ReservaExitDTO = null;
        if (reservaBuscada != null) {
            ReservaExitDTO = modelMapper.map(reservaBuscada, ReservaExitDTO.class);
            LOGGER.info("Reserva encontrado: {}", ReservaExitDTO);
        } else {
            LOGGER.error("El id no se encuentra registrado en la base de datos");
            throw new ResourceNotFoundException("El id no se encuentra registrado en la base de datos");
        }

        return ReservaExitDTO;
    }

    @Override
    public ReservaExitDTO cancelarReserva(Long id) throws ResourceNotFoundException {
        Reserva reservaACancelar = reservaRepository.findById(id).orElse(null);
        if (reservaACancelar != null) {
            reservaACancelar.setEstado("Cancelada");
            reservaRepository.save(reservaACancelar);
            LOGGER.warn("Se ha cancelado la reserva con id: {}", id);
        } else {
            LOGGER.error("No se ha encontrado la reserva con id {}", id);
            throw new ResourceNotFoundException("No se ha encontrado la reserva con id " + id);
        }
        return modelMapper.map(reservaACancelar, ReservaExitDTO.class);
    }

    @PostConstruct
    private void configureMapping() {
        modelMapper.typeMap(ReservaService.class, Reserva.class);
        modelMapper.typeMap(Reserva.class, ReservaExitDTO.class);
    }
}
