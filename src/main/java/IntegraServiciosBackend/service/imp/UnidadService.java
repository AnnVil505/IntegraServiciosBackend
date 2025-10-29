package IntegraServiciosBackend.service.imp;

import IntegraServiciosBackend.dto.register.UnidadRegisterDTO;
import IntegraServiciosBackend.dto.modification.UnidadModificationDTO;
import IntegraServiciosBackend.dto.exit.UnidadExitDTO;
import IntegraServiciosBackend.entity.Unidad;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.repository.UnidadRepository;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@Service
public class UnidadService implements IUnidadService {

    private final Logger LOGGER = LoggerFactory.getLogger(UnidadService.class);
    private UnidadRepository unidadRepository;
    private ModelMapper modelMapper;

    @Override
    public UnidadExitDTO registrarUnidad(UnidadRegisterDTO unidad) throws BadRequestException {
        if (unidadRepository.findByNombre(unidad.getNombre()).isPresent()) {
            throw new BadRequestException("Ya existe una unidad con ese nombre");
        }

        Unidad entidad = modelMapper.map(unidad, Unidad.class);
        Unidad guardada = unidadRepository.save(entidad);

        LOGGER.info("Unidad registrada: {}", JsonPrinter.toString(guardada));
        return modelMapper.map(guardada, UnidadExitDTO.class);
    }

    @Override
    public List<UnidadExitDTO> listarUnidades() {
        List<UnidadExitDTO> unidades = unidadRepository.findAll()
                .stream()
                .map(u -> modelMapper.map(u, UnidadExitDTO.class))
                .collect(Collectors.toList());

        LOGGER.info("Listando {} unidades registradas", unidades.size());
        return unidades;
    }

    @Override
    public UnidadExitDTO buscarUnidadPorId(UUID id) throws ResourceNotFoundException {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la unidad con id " + id));

        LOGGER.info("Unidad encontrada: {}", JsonPrinter.toString(unidad));
        return modelMapper.map(unidad, UnidadExitDTO.class);
    }

    @Override
    public UnidadExitDTO actualizarUnidad(UnidadModificationDTO unidad) throws ResourceNotFoundException, BadRequestException {
        Unidad existente = unidadRepository.findById(unidad.getUnidadId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la unidad con id " + unidad.getUnidadId()));

        LOGGER.info("Unidad antes de actualizar: {}", JsonPrinter.toString(existente));
        LOGGER.info("Datos de actualización: {}", JsonPrinter.toString(unidad));

        if (unidad.getNombre() != null && !unidad.getNombre().equalsIgnoreCase(existente.getNombre())) {
            if (unidadRepository.findByNombre(unidad.getNombre()).isPresent()) {
                throw new BadRequestException("El nombre ya está en uso por otra unidad");
            }
            existente.setNombre(unidad.getNombre());
        }

        if (unidad.getDescripcion() != null) existente.setDescripcion(unidad.getDescripcion());
        if (unidad.getHorarioGlobal() != null) existente.setHorarioGlobal(unidad.getHorarioGlobal());
        if (unidad.getTiempoMinimoMinutos() != null)
            existente.setTiempoMinimoMinutos(unidad.getTiempoMinimoMinutos());

        Unidad actualizada = unidadRepository.save(existente);
        LOGGER.info("Unidad actualizada: {}", JsonPrinter.toString(actualizada));

        return modelMapper.map(actualizada, UnidadExitDTO.class);
    }

    @Override
    public UnidadExitDTO eliminarUnidad(UUID id) throws ResourceNotFoundException {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la unidad con id " + id));

        unidadRepository.deleteById(id);
        LOGGER.warn("Unidad eliminada: {}", JsonPrinter.toString(unidad));
        return modelMapper.map(unidad, UnidadExitDTO.class);
    }

    @PostConstruct
    private void configureMapping() {
        modelMapper.typeMap(UnidadRegisterDTO.class, Unidad.class);
        modelMapper.typeMap(Unidad.class, UnidadExitDTO.class);
        modelMapper.typeMap(UnidadModificationDTO.class, Unidad.class);
    }
}
