package IntegraServiciosBackend.service.imp;

import IntegraServiciosBackend.dto.register.UsuarioRegisterDTO;
import IntegraServiciosBackend.dto.register.UsuarioLoginDTO;
import IntegraServiciosBackend.dto.modification.UsuarioModificationDTO;
import IntegraServiciosBackend.dto.exit.UsuarioExitDTO;
import IntegraServiciosBackend.entity.Usuario;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.repository.UsuarioRepository;
import IntegraServiciosBackend.service.IUsuarioService;
import IntegraServiciosBackend.utils.JsonPrinter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@Service
public class UsuarioService implements IUsuarioService {

    private final Logger LOGGER = LoggerFactory.getLogger(UsuarioService.class);
    private UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;

    @Override
    public Object registrarUsuario(UsuarioRegisterDTO usuario) {
        //convertimos mediante el mapper de dtoRegister a entidad
        LOGGER.info("UsuarioRegisterDTO: " + JsonPrinter.toString(usuario));

        List<Usuario> usuarioBuscadoCedula = usuarioRepository.findByCedula(usuario.getCedula());
        Usuario usuarioBuscadoEmail = usuarioRepository.findOneByEmail(usuario.getEmail()).orElse(null);
        //Usuario usuarioBuscado = null;

        if(usuarioBuscadoEmail != null){
            LOGGER.info("Correo ya registrado");
            throw new BadRequestException("Correo ya registrado");
        }


        if(usuarioBuscadoCedula.size()>0){
            LOGGER.info("Usuario ya registrado");
            throw new BadRequestException("Cedula ya registrada");
        }

        Usuario usuarioEntidad = modelMapper.map(usuario, Usuario.class);
        LOGGER.info("Usuario Entidad Entrada: " + JsonPrinter.toString(usuarioEntidad));

        //Se encripta la contraseña
        usuarioEntidad.setContraseña(passwordEncoder.encode(usuarioEntidad.getContraseña()));

        //mandamos a persistir a la capa dao y obtenemos una entidad
        Usuario usuarioAPersistir = usuarioRepository.save(usuarioEntidad);
        LOGGER.info("Usuario Entidad Guardado: " + JsonPrinter.toString(usuarioAPersistir));

        //transformamos la entidad obtenida en salidaDto
        UsuarioExitDTO UsuarioExitDTO = modelMapper.map(usuarioAPersistir, UsuarioExitDTO.class);
        LOGGER.info("UsuarioExitDTO: " + JsonPrinter.toString(UsuarioExitDTO));
        return UsuarioExitDTO;
    }

    @Override
    public UsuarioExitDTO iniciarSesion(UsuarioLoginDTO usuario) {


        LOGGER.info("UsuarioLoginEntradaDto: " + JsonPrinter.toString(usuario));

        Usuario usuarioEntidad = modelMapper.map(usuario, Usuario.class);


        Usuario usuarioABuscar = usuarioRepository.findByEmail(usuarioEntidad.getEmail(),usuarioEntidad.getContraseña());

        UsuarioExitDTO UsuarioExitDTO;

        if(usuarioABuscar != null){
            LOGGER.info("Usuario existe");
            UsuarioExitDTO = modelMapper.map(usuarioABuscar, UsuarioExitDTO.class);
            LOGGER.info("UsuarioExitDTO: " + JsonPrinter.toString(UsuarioExitDTO));
            return UsuarioExitDTO;
        }
        else{
            LOGGER.info("Usuario no existe ");
            return null;
        }

    }

    @Override
    public List<UsuarioExitDTO> listarUsuarios() {
        List<UsuarioExitDTO> usuarios = usuarioRepository.findAll().stream()
                .map(u -> modelMapper.map(u, UsuarioExitDTO.class))
                .collect(Collectors.toList());

        return usuarios;
    }

    @Override
    public UsuarioExitDTO buscarUsuarioPorId(Long id) throws ResourceNotFoundException{
        Usuario usuarioBuscado = usuarioRepository.findById(id).orElse(null);

        UsuarioExitDTO UsuarioExitDTO = null;
        if (usuarioBuscado != null) {
            UsuarioExitDTO = modelMapper.map(usuarioBuscado, UsuarioExitDTO.class);
            LOGGER.info("Usuario encontrado: {}", UsuarioExitDTO);
        } else {
            LOGGER.error("El id no se encuentra registrado en la base de datos");
            throw new ResourceNotFoundException("El id no se encuentra registrado en la base de datos");
        }

        return UsuarioExitDTO;
    }

    @Override
    public UsuarioExitDTO buscarUsuarioPorEmail(String email) throws ResourceNotFoundException{
        Usuario usuarioBuscado = usuarioRepository.findOneByEmail(email).orElse(null);

        UsuarioExitDTO UsuarioExitDTO = null;
        if (usuarioBuscado != null) {
            UsuarioExitDTO = modelMapper.map(usuarioBuscado, UsuarioExitDTO.class);
            LOGGER.info("Usuario encontrado: {}", UsuarioExitDTO);
        } else {
            LOGGER.error("El email no se encuentra registrado en la base de datos");
            throw new ResourceNotFoundException("El email no se encuentra registrado en la base de datos");
        }

        return UsuarioExitDTO;
    }

    @Override
    public UsuarioExitDTO actualizarUsuario(UsuarioModificationDTO usuario) throws ResourceNotFoundException {
        Usuario usuarioAActualizar = modelMapper.map(buscarUsuarioPorId(usuario.getId()),Usuario.class);

        if(usuarioAActualizar==null){
            throw new ResourceNotFoundException("El usuario no existe");
        }

        List<Usuario> usuarioBuscadoCedula = usuarioRepository.findByCedula(usuario.getCedula());
        Usuario usuarioBuscadoEmail = usuarioRepository.findOneByEmail(usuario.getEmail()).orElse(null);


        if(!usuarioBuscadoCedula.isEmpty() && usuarioBuscadoCedula.get(0).getId()!= usuario.getId()){
            LOGGER.info("Usuario ya registrado");
            throw new BadRequestException("La cedula ya se encuentra registrado");
        }

        if(usuarioBuscadoEmail != null && usuarioBuscadoEmail.getId()!= usuario.getId()){
            LOGGER.info("Correo ya registrado");
            throw new BadRequestException("El correo ya se encuentra registrado");
        }

        usuarioAActualizar.setFullname(usuario.getFullname());
        usuarioAActualizar.setRol(usuario.getRol());
        usuarioAActualizar.setEmail(usuario.getEmail());
        usuarioAActualizar.setCedula(usuario.getCedula());

        return modelMapper.map(usuarioRepository.save(usuarioAActualizar),UsuarioExitDTO.class);


    }

    @Override
    public UsuarioExitDTO eliminarUsuario(Long id) throws ResourceNotFoundException {
        UsuarioExitDTO usuarioAEliminar = null;
        usuarioAEliminar = buscarUsuarioPorId(id);
        if (usuarioAEliminar != null) {
            usuarioRepository.deleteById(id);
            LOGGER.warn("Se ha eliminado el usuario con id: {}", id);
        } else {
            LOGGER.error("No se ha encontrado el usuario con id {}", id);
            throw new ResourceNotFoundException("No se ha encontrado el recurso con id " + id);
        }
        return usuarioAEliminar;
    }
    @PostConstruct
    private void configureMapping() {
        modelMapper.typeMap(UsuarioRegisterDTO.class, Usuario.class);
        modelMapper.typeMap(Usuario.class, UsuarioExitDTO.class);
        modelMapper.typeMap(UsuarioExitDTO.class, Usuario.class);
        modelMapper.typeMap(UsuarioModificationDTO.class, Usuario.class);
    }
}