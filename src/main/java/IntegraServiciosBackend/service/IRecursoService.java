package IntegraServiciosBackend.service;

import IntegraServiciosBackend.dto.register.RecursoRegisterDTO;
import IntegraServiciosBackend.dto.modification.RecursoModificationDTO;
import IntegraServiciosBackend.dto.exit.RecursoExitDTO;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;

import java.util.List;

public interface IRecursoService {

    Object registrarRecurso(RecursoRegisterDTO recurso) throws BadRequestException;
    List<RecursoExitDTO> listarRecursos() throws BadRequestException;
    RecursoExitDTO buscarRecursoPorId(Long id) throws ResourceNotFoundException;
    RecursoExitDTO actualizarRecurso(RecursoModificationDTO recursoModificado) throws ResourceNotFoundException,BadRequestException;
    RecursoExitDTO eliminarRecurso(Long id) throws ResourceNotFoundException;

}
