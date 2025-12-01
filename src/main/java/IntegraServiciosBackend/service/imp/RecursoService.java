package IntegraServiciosBackend.service.imp;

import IntegraServiciosBackend.dto.register.HorarioDisponibleRecursoRegisterDTO;
import IntegraServiciosBackend.dto.register.RecursoRegisterDTO;
import IntegraServiciosBackend.dto.modification.HorarioDisponibleRecursoModificationDTO;
import IntegraServiciosBackend.dto.modification.RecursoModificationDTO;
import IntegraServiciosBackend.dto.exit.RecursoExitDTO;
import IntegraServiciosBackend.entity.*;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.repository.*;
import IntegraServiciosBackend.service.IRecursoService;
import IntegraServiciosBackend.utils.JsonPrinter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@Service
public class RecursoService implements IRecursoService {

    private final Logger LOGGER = LoggerFactory.getLogger(RecursoService.class);
    private final RecursoRepository recursoRepository;
    private final DiaRepository diaRepository;
    private final UnidadRepository unidadRepository;
    private final HorarioDisponibleRecursoRepository horarioDisponibleRecursoRepository;
    private ModelMapper modelMapper;


    @Override
    public Object registrarRecurso(RecursoRegisterDTO recurso) throws BadRequestException {
        Unidad unidadRecurso = unidadRepository.findById(recurso.getUnidad()).orElse(null);

        if(unidadRecurso == null){
            return "La unidad no existe";
        }

        List<HorarioDisponibleRecurso> horarios = new ArrayList<>();
        for (HorarioDisponibleRecursoRegisterDTO horarioDto : recurso.getHorarioDisponible()) {
            Dia dia = diaRepository.findById(horarioDto.getDia()).orElseThrow(); // Obtener entidad Dia
            HorarioDisponibleRecurso horario = new HorarioDisponibleRecurso();

            Boolean flagDia = false;
            for(Dia diaUnidad: unidadRecurso.getDiasDisponibles()){
                if(diaUnidad.getId()==dia.getId()){
                    flagDia=true;
                }
            }
            if(!flagDia){
                return "Día de disponibilidad inválido por dias de unidad";
            }

            if(LocalTime.parse(horarioDto.getHoraInicio()).isBefore(LocalTime.parse(unidadRecurso.getHoraInicio()))){
                return "Hora de inicio inválida por horario de unidad";
            }
            if(LocalTime.parse(horarioDto.getHoraFin()).isAfter(LocalTime.parse(unidadRecurso.getHoraFinal()))){
                return "Hora de fin inválida por horario de unidad";
            }
            horario.setDia(dia);
            horario.setHoraInicio(horarioDto.getHoraInicio());
            horario.setHoraFin(horarioDto.getHoraFin());
            // Guardar el horario en la base de datos
            horario = horarioDisponibleRecursoRepository.save(horario);
            horarios.add(horario);
        }

        Recurso recursoEntidad = modelMapper.map(recurso, Recurso.class);
        recursoEntidad.setUnidad(unidadRecurso);
        recursoEntidad.setHorarioDisponible(horarios);
        Recurso recursoGuardado = recursoRepository.save(recursoEntidad);
        LOGGER.info("Recurso guardado: {}", JsonPrinter.toString(recursoEntidad));
        return modelMapper.map(recursoGuardado,RecursoExitDTO.class);
    }


    @Override
    public List<RecursoExitDTO> listarRecursos() throws BadRequestException{
        List<RecursoExitDTO> recursos = recursoRepository.findAll().stream()
                .map(r -> modelMapper.map(r, RecursoExitDTO.class)).toList();

        return recursos;
    }


    @Override
    public RecursoExitDTO eliminarRecurso(Long id) throws ResourceNotFoundException {
        RecursoExitDTO recursoAEliminar = null;
        recursoAEliminar = buscarRecursoPorId(id);
        if (recursoAEliminar != null) {
            recursoRepository.deleteById(id);
            LOGGER.warn("Se ha eliminado el recurso con id: {}", id);
        } else {
            LOGGER.error("No se ha encontrado el recurso con id {}", id);
            throw new ResourceNotFoundException("No se ha encontrado el recurso con id " + id);
        }
        return recursoAEliminar;
    }

    @Override
    public RecursoExitDTO buscarRecursoPorId(Long id) throws ResourceNotFoundException{
        Recurso recursoBuscado = recursoRepository.findById(id).orElse(null);

        RecursoExitDTO RecursoExitDTO = null;
        if (recursoBuscado != null) {
            RecursoExitDTO = modelMapper.map(recursoBuscado, RecursoExitDTO.class);
            LOGGER.info("Recurso encontrado: {}", RecursoExitDTO);
        } else {
            LOGGER.error("El id no se encuentra registrado en la base de datos");
            throw new ResourceNotFoundException("El id no se encuentra registrado en la base de datos");
        }

        return RecursoExitDTO;
    }

    @Override
    public RecursoExitDTO actualizarRecurso(RecursoModificationDTO recursoModificado) throws ResourceNotFoundException,BadRequestException{

        //Comprobar si el recurso existe
        Recurso recursoComprobacion = recursoRepository.findById(recursoModificado.getId()).orElse(null);
        if(recursoComprobacion==null){
            LOGGER.error("El id del recurso no se encuentra registrado en la base de datos");
            throw new ResourceNotFoundException("El id del recurso no se encuentra registrado en la base de datos");
        }

        //Validar si la unidad existe
        Unidad unidadmodification = unidadRepository.findById(recursoModificado.getUnidad()).orElse(null);
        if(unidadmodification==null){
            LOGGER.error("El id de la unidad no se encuentra registrada en la base de datos");
            throw new ResourceNotFoundException("El id de la unidad no se encuentra registrada en la base de datos");
        }

        List<HorarioDisponibleRecurso> horarios = new ArrayList<>();
        for (HorarioDisponibleRecursoModificationDTO horariomodificationDto : recursoModificado.getHorarioDisponible()) {
            Dia dia = diaRepository.findById(horariomodificationDto.getDia()).orElseThrow(); // Obtener entidad Dia
            HorarioDisponibleRecurso horario = new HorarioDisponibleRecurso();

            Boolean flagDia = false;
            for(Dia diaUnidad: unidadmodification.getDiasDisponibles()){
                if(diaUnidad.getId()==dia.getId()){
                    flagDia=true;
                }
            }
            if(!flagDia){
                //return "Día de disponibilidad inválido por dias de unidad";
                throw new BadRequestException("Día de disponibilidad inválido por dias de unidad");
            }
            if(LocalTime.parse(horariomodificationDto.getHoraInicio()).isBefore(LocalTime.parse(unidadmodification.getHoraInicio()))){
                //return "Hora de inicio inválida por horario de unidad";
                throw new BadRequestException("Hora de inicio inválida por horario de unidad");
            }
            if(LocalTime.parse(horariomodificationDto.getHoraFin()).isAfter(LocalTime.parse(unidadmodification.getHoraFinal()))){
                //return "Hora de fin inválida por horario de unidad";
                throw new BadRequestException("Hora de fin inválida por horario de unidad");
            }
            horario.setDia(dia);
            horario.setHoraInicio(horariomodificationDto.getHoraInicio());
            horario.setHoraFin(horariomodificationDto.getHoraFin());
            // Guardar el horario en la base de datos
            //horario = horarioDisponibleRecursoRepository.save(horario);
            horarios.add(horario);
        }

        recursoComprobacion.getHorarioDisponible().clear();

        for(HorarioDisponibleRecurso horarioRecurso : recursoComprobacion.getHorarioDisponible()){
            horarioDisponibleRecursoRepository.delete(horarioRecurso);
        }

        for(HorarioDisponibleRecurso horario: horarios){
            horarioDisponibleRecursoRepository.save(horario);
        }

        Recurso recursoActualizar = modelMapper.map(recursoModificado,Recurso.class);
        recursoActualizar.setUnidad(unidadmodification);
        recursoActualizar.setHorarioDisponible(horarios);

        RecursoExitDTO recursoActualizadoSalida = modelMapper.map(recursoRepository.save(recursoActualizar),RecursoExitDTO.class);

        //Eliminar entidades de horario que no están siendo usados por ningun Recurso
        List<HorarioDisponibleRecurso> horariosUtilizados = recursoRepository.findAll().stream()
                .flatMap(recurso -> recurso.getHorarioDisponible().stream())
                .distinct()
                .collect(Collectors.toList());

        // Obtener todos los horarios disponibles
        List<HorarioDisponibleRecurso> todosLosHorarios = horarioDisponibleRecursoRepository.findAll();

        // Identificar los horarios disponibles no utilizados
        List<HorarioDisponibleRecurso> horariosNoUtilizados = todosLosHorarios.stream()
                .filter(horario -> !horariosUtilizados.contains(horario))
                .collect(Collectors.toList());

        // Eliminar los horarios disponibles no utilizados
        horarioDisponibleRecursoRepository.deleteAll(horariosNoUtilizados);

        return recursoActualizadoSalida;
    }

    @PostConstruct
    private void configureMapping() {
        modelMapper.typeMap(RecursoRegisterDTO.class, Recurso.class);
        modelMapper.typeMap(Recurso.class, RecursoExitDTO.class);
        modelMapper.typeMap(RecursoModificationDTO.class,Recurso.class);
    }
}