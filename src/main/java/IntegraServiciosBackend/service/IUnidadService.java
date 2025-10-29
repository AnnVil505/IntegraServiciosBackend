package IntegraServiciosBackend.service;

import IntegraServiciosBackend.dto.register.UnidadRegisterDTO;
import IntegraServiciosBackend.dto.modification.UnidadModificationDTO;
import IntegraServiciosBackend.dto.exit.UnidadExitDTO;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

public interface IUnidadService {
    UnidadExitDTO registrarUnidad(UnidadRegisterDTO unidad) throws BadRequestException;
    List<UnidadExitDTO> listarUnidades();
    UnidadExitDTO buscarUnidadPorId(UUID id) throws ResourceNotFoundException;
    UnidadExitDTO actualizarUnidad(UnidadModificationDTO unidad) throws ResourceNotFoundException, BadRequestException;
    UnidadExitDTO eliminarUnidad(UUID id) throws ResourceNotFoundException;
}

