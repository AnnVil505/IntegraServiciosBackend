package IntegraServiciosBackend.service.imp;

import IntegraServiciosBackend.dto.register.UnidadRegisterDTO;
import IntegraServiciosBackend.dto.modification.UnidadModificationDTO;
import IntegraServiciosBackend.dto.exit.UnidadExitDTO;
import IntegraServiciosBackend.entity.Unidad;
import IntegraServiciosBackend.entity.Dia;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.repository.UnidadRepository;
import IntegraServiciosBackend.repository.DiaRepository;
import IntegraServiciosBackend.service.IUnidadService;
import IntegraServiciosBackend.utils.JsonPrinter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@Service
public class UnidadService implements IUnidadService {

    private final Logger LOGGER = LoggerFactory.getLogger(UnidadService.class);
    private UnidadRepository unidadRepository;
    private DiaRepository diaRepository;
    private ModelMapper modelMapper;

    @Override
    public UnidadExitDTO registrarUnidad(UnidadRegisterDTO unidad) {
        // Convertir DTO a entidad
        Unidad unidadEntidad = modelMapper.map(unidad,Unidad.class);

        // Obtener los días de la base de datos
        List<Dia> diasDisponibles = (List<Dia>) diaRepository.findAllById(unidad.getDiasDisponibles());

        unidadEntidad.setDiasDisponibles(diasDisponibles);

        // Guardar la unidad
        return modelMapper.map(unidadRepository.save(unidadEntidad),UnidadExitDTO.class);
    }

    @Override
    public List<UnidadExitDTO> listarUnidades() {
        List<UnidadExitDTO> unidades = unidadRepository.findAll().stream()
                .map(u -> modelMapper.map(u, UnidadExitDTO.class))
                .collect(Collectors.toList());

        return unidades;
    }

    @Override
    public UnidadExitDTO buscarUnidadPorId(Long id) throws ResourceNotFoundException{
        Unidad unidadBuscada = unidadRepository.findById(id).orElse(null);

        UnidadExitDTO UnidadExitDTO = null;
        if (unidadBuscada != null) {
            UnidadExitDTO = modelMapper.map(unidadBuscada, UnidadExitDTO.class);
            LOGGER.info("Recurso encontrado: {}", UnidadExitDTO);
        } else {
            LOGGER.error("El id no se encuentra registrado en la base de datos");
            throw new ResourceNotFoundException("El id no se encuentra registrado en la base de datos");
        }

        return UnidadExitDTO;
    }

    @Override
    public UnidadExitDTO actualizarUnidad(UnidadModificationDTO unidad) throws ResourceNotFoundException{
        Unidad unidadComprobacion = unidadRepository.findById(unidad.getId()).orElse(null);

        UnidadExitDTO UnidadExitDTO = null;

        LOGGER.info("Unidad modificacion entrada: {}", JsonPrinter.toString(unidad));

        List<Dia> listaDias = new ArrayList<Dia>();

        if(unidadComprobacion!=null){

            for(Long idDia:unidad.getDiasDisponibles()){
                listaDias.add(diaRepository.findById(idDia).orElse(null));
            }
            Unidad unidadGuardar = modelMapper.map(unidad,Unidad.class);

            unidadGuardar.setDiasDisponibles(listaDias);

            UnidadExitDTO = modelMapper.map(unidadRepository.save(unidadGuardar),UnidadExitDTO.class);
        }
        else{
            throw new ResourceNotFoundException("La unidad no existe");
        }

        LOGGER.info("Unidad actualizada: {}", JsonPrinter.toString(UnidadExitDTO));
        return UnidadExitDTO;
    }

    @Override
    public UnidadExitDTO eliminarUnidad(Long id) throws ResourceNotFoundException {
        UnidadExitDTO unidadAEliminar = null;
        unidadAEliminar = buscarUnidadPorId(id);
        if (unidadAEliminar != null) {
            unidadRepository.deleteById(id);
            LOGGER.warn("Se ha eliminado la unidad con id: {}", id);
        } else {
            LOGGER.error("No se ha encontrado el recurso con id {}", id);
            throw new ResourceNotFoundException("No se ha encontrado el recurso con id " + id);
        }
        return unidadAEliminar;
    }

    @PostConstruct
    private void configureMapping() {
        modelMapper.typeMap(UnidadRegisterDTO.class, Unidad.class);
        modelMapper.typeMap(Unidad.class, UnidadExitDTO.class);
        modelMapper.typeMap(UnidadModificationDTO.class,Unidad.class);
    }

}