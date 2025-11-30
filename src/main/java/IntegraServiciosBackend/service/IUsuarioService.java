package IntegraServiciosBackend.service;

import IntegraServiciosBackend.dto.register.UsuarioRegisterDTO;
import IntegraServiciosBackend.dto.register.UsuarioLoginDTO;
import IntegraServiciosBackend.dto.modification.UsuarioModificationDTO;
import IntegraServiciosBackend.dto.exit.UsuarioExitDTO;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;

import java.util.List;

public interface IUsuarioService {

    Object registrarUsuario(UsuarioRegisterDTO usuario) throws BadRequestException;
    UsuarioExitDTO iniciarSesion(UsuarioLoginDTO usuario);
    List<UsuarioExitDTO> listarUsuarios();
    UsuarioExitDTO buscarUsuarioPorId(Long id) throws ResourceNotFoundException;

    UsuarioExitDTO buscarUsuarioPorEmail(String email) throws ResourceNotFoundException;
    UsuarioExitDTO actualizarUsuario(UsuarioModificationDTO usuario) throws ResourceNotFoundException, BadRequestException;
    UsuarioExitDTO eliminarUsuario(Long id) throws ResourceNotFoundException;
}