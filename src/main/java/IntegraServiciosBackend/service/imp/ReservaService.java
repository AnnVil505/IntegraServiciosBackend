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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    public Object registrarReserva(ReservaRegisterDTO reserva) throws BadRequestException {
        LOGGER.info("ReservaRegisterDTO: " + JsonPrinter.toString(reserva));

        Recurso recursoReserva = recursoRepository.findById(reserva.getIdRecurso()).orElse(null);
        Usuario usuarioReserva = usuarioRepository.findById(reserva.getIdUsuario()).orElse(null);

        if(recursoReserva==null){
            return "El recurso no existe";
        }
        if(usuarioReserva==null){
            return "El usuario no existe";
        }


        Reserva reservaEntidad = modelMapper.map(reserva,Reserva.class);
        Reserva reservaGuardada = reservaRepository.save(reservaEntidad);

        ReservaExitDTO reservaexit = modelMapper.map(reservaGuardada,ReservaExitDTO.class);
        reservaexit.setUsuario(modelMapper.map(usuarioReserva, UsuarioExitDTO.class));
        reservaexit.setRecurso(modelMapper.map(recursoReserva,RecursoExitDTO.class));


        return reservaexit;
    }

    @Override
    public ReservaExitDTO actualizarReserva(ReservaModificationDTO ReservaModificationDTO) throws ResourceNotFoundException{
        LOGGER.info("ReservaModificationDTO: " + JsonPrinter.toString(ReservaModificationDTO));

        Recurso recursoReserva = recursoRepository.findById(ReservaModificationDTO.getIdRecurso()).orElse(null);
        Usuario usuarioReserva = usuarioRepository.findById(ReservaModificationDTO.getIdUsuario()).orElse(null);

        if(recursoReserva==null){
            throw new ResourceNotFoundException("El recurso no existe");
        }
        if(usuarioReserva==null){
            throw new ResourceNotFoundException("El usuario no existe");
        }

        Reserva reservaAActualizar = modelMapper.map(ReservaModificationDTO,Reserva.class);
        reservaAActualizar.setUsuario(usuarioReserva);
        reservaAActualizar.setRecurso(recursoReserva);

        return modelMapper.map(reservaRepository.save(reservaAActualizar),ReservaExitDTO.class);
    }


    @Override
    public List<ReservaExitDTO> listarReservas() throws BadRequestException {
        List<ReservaExitDTO> reservas = reservaRepository.findAll().stream()
                .map(r -> modelMapper.map(r, ReservaExitDTO.class)).toList();

        for (ReservaExitDTO reserva: reservas) {
            reserva.setRecurso(modelMapper.map(recursoRepository.findById(reserva.getRecurso().getId()).orElse(null),RecursoExitDTO.class));
            reserva.setUsuario(modelMapper.map(usuarioRepository.findById(reserva.getUsuario().getId()).orElse(null),UsuarioExitDTO.class));
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
    public ReservaExitDTO buscarReservaPorId(Long id) throws ResourceNotFoundException{
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
        return modelMapper.map(reservaACancelar,ReservaExitDTO.class);
    }

    @PostConstruct
    private void configureMapping() {
        modelMapper.typeMap(ReservaService.class, Reserva.class);
        modelMapper.typeMap(Reserva.class, ReservaExitDTO.class);
    }
}