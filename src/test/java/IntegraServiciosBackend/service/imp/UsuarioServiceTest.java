package IntegraServiciosBackend.service.imp;

import IntegraServiciosBackend.dto.register.UsuarioRegisterDTO;
import IntegraServiciosBackend.dto.register.UsuarioLoginDTO;
import IntegraServiciosBackend.dto.modification.UsuarioModificationDTO;
import IntegraServiciosBackend.dto.exit.UsuarioExitDTO;
import IntegraServiciosBackend.entity.Usuario;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.repository.UsuarioRepository;
import IntegraServiciosBackend.service.imp.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------
    // 1. REGISTRAR USUARIO
    // -------------------------------------------------------

    @Test
    void registrarUsuario_exito() throws BadRequestException {
        UsuarioRegisterDTO dto = new UsuarioRegisterDTO();
        dto.setEmail("test@mail.com");
        dto.setCedula(1230987678);

        Usuario usuarioEntidad = new Usuario();
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(1L);

        UsuarioExitDTO dtoSalida = new UsuarioExitDTO();
        dtoSalida.setId(1L);

        when(usuarioRepository.findOneByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByCedula(1230987678)).thenReturn(Collections.emptyList());
        when(modelMapper.map(dto, Usuario.class)).thenReturn(usuarioEntidad);
        when(passwordEncoder.encode(anyString())).thenReturn("passEncriptada");
        when(usuarioRepository.save(usuarioEntidad)).thenReturn(usuarioGuardado);
        when(modelMapper.map(usuarioGuardado, UsuarioExitDTO.class)).thenReturn(dtoSalida);

        Object result = usuarioService.registrarUsuario(dto);

        assertNotNull(result);
        assertTrue(result instanceof UsuarioExitDTO);
        assertEquals(1L, ((UsuarioExitDTO) result).getId());

        verify(usuarioRepository).save(usuarioEntidad);
    }

    @Test
    void registrarUsuario_correoDuplicado() {
        UsuarioRegisterDTO dto = new UsuarioRegisterDTO();
        dto.setEmail("test@mail.com");

        when(usuarioRepository.findOneByEmail("test@mail.com")).thenReturn(Optional.of(new Usuario()));

        assertThrows(BadRequestException.class, () -> usuarioService.registrarUsuario(dto));
    }

    @Test
    void registrarUsuario_cedulaDuplicada() {
        UsuarioRegisterDTO dto = new UsuarioRegisterDTO();
        dto.setEmail("new@mail.com");
        dto.setCedula(1230987678);

        when(usuarioRepository.findOneByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(usuarioRepository.findByCedula( 1230987678)).thenReturn(List.of(new Usuario()));

        assertThrows(BadRequestException.class, () -> usuarioService.registrarUsuario(dto));
    }

    // -------------------------------------------------------
    // 2. INICIAR SESIÓN
    // -------------------------------------------------------

    @Test
    void iniciarSesion_exito() {
        UsuarioLoginDTO dto = new UsuarioLoginDTO();
        dto.setEmail("test@mail.com");
        dto.setContraseña( "1230987678");

        Usuario entidad = new Usuario();
        entidad.setId(1L);

        UsuarioExitDTO salida = new UsuarioExitDTO();
        salida.setId(1L);

        when(modelMapper.map(dto, Usuario.class)).thenReturn(entidad);
        when(usuarioRepository.findByEmail("test@mail.com",  "1230987678")).thenReturn(entidad);
        when(modelMapper.map(entidad, UsuarioExitDTO.class)).thenReturn(salida);

        UsuarioExitDTO result = usuarioService.iniciarSesion(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void iniciarSesion_invalido() {
        UsuarioLoginDTO dto = new UsuarioLoginDTO();
        dto.setEmail("test@mail.com");
        dto.setContraseña( "1230987678");

        Usuario entidad = new Usuario();

        when(modelMapper.map(dto, Usuario.class)).thenReturn(entidad);
        when(usuarioRepository.findByEmail("test@mail.com",  "1230987678")).thenReturn(null);

        UsuarioExitDTO result = usuarioService.iniciarSesion(dto);

        assertNull(result);
    }

    // -------------------------------------------------------
    // 3. LISTAR USUARIOS
    // -------------------------------------------------------

    @Test
    void listarUsuarios_exito() {
        Usuario u = new Usuario();
        u.setId(1L);

        UsuarioExitDTO salida = new UsuarioExitDTO();
        salida.setId(1L);

        when(usuarioRepository.findAll()).thenReturn(List.of(u));
        when(modelMapper.map(u, UsuarioExitDTO.class)).thenReturn(salida);

        List<UsuarioExitDTO> result = usuarioService.listarUsuarios();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    // -------------------------------------------------------
    // 4. BUSCAR USUARIO POR ID
    // -------------------------------------------------------

    @Test
    void buscarUsuarioPorId_exito() throws ResourceNotFoundException {
        Usuario u = new Usuario();
        u.setId(1L);

        UsuarioExitDTO dto = new UsuarioExitDTO();
        dto.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(modelMapper.map(u, UsuarioExitDTO.class)).thenReturn(dto);

        UsuarioExitDTO result = usuarioService.buscarUsuarioPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void buscarUsuarioPorId_noEncontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.buscarUsuarioPorId(1L));
    }

    // -------------------------------------------------------
    // 5. BUSCAR POR EMAIL
    // -------------------------------------------------------

    @Test
    void buscarUsuarioPorEmail_exito() throws ResourceNotFoundException {
        Usuario u = new Usuario();
        UsuarioExitDTO dto = new UsuarioExitDTO();

        when(usuarioRepository.findOneByEmail("mail@mail.com")).thenReturn(Optional.of(u));
        when(modelMapper.map(u, UsuarioExitDTO.class)).thenReturn(dto);

        assertNotNull(usuarioService.buscarUsuarioPorEmail("mail@mail.com"));
    }

    @Test
    void buscarUsuarioPorEmail_noEncontrado() {
        when(usuarioRepository.findOneByEmail("mail@mail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.buscarUsuarioPorEmail("mail@mail.com"));
    }

    // -------------------------------------------------------
    // 6. ACTUALIZAR USUARIO
    // -------------------------------------------------------

    @Test
    void actualizarUsuario_exito() throws Exception {
        UsuarioModificationDTO dto = new UsuarioModificationDTO();
        dto.setId(1L);
        dto.setCedula( 1230987678);
        dto.setEmail("test@mail.com");

        Usuario existente = new Usuario();
        existente.setId(1L);

        Usuario actualizado = new Usuario();
        actualizado.setId(1L);

        UsuarioExitDTO salida = new UsuarioExitDTO();
        salida.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(modelMapper.map(any(UsuarioExitDTO.class), eq(Usuario.class))).thenReturn(existente);
        when(usuarioRepository.findByCedula( 1230987678)).thenReturn(Collections.emptyList());
        when(usuarioRepository.findOneByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(existente)).thenReturn(actualizado);
        when(modelMapper.map(actualizado, UsuarioExitDTO.class)).thenReturn(salida);

        UsuarioExitDTO result = usuarioService.actualizarUsuario(dto);

        assertNotNull(result);
    }

    @Test
    void actualizarUsuario_cedulaDuplicada() {
        UsuarioModificationDTO dto = new UsuarioModificationDTO();
        dto.setId(1L);
        dto.setCedula( 1230987678);

        Usuario existente = new Usuario();
        existente.setId(1L);

        Usuario duplicado = new Usuario();
        duplicado.setId(2L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(modelMapper.map(any(UsuarioExitDTO.class), eq(Usuario.class))).thenReturn(existente);
        when(usuarioRepository.findByCedula( 1230987678)).thenReturn(List.of(duplicado));

        assertThrows(BadRequestException.class,
                () -> usuarioService.actualizarUsuario(dto));
    }

    @Test
    void actualizarUsuario_emailDuplicado() {
        UsuarioModificationDTO dto = new UsuarioModificationDTO();
        dto.setId(1L);
        dto.setEmail("mail@mail.com");

        Usuario existente = new Usuario();
        existente.setId(1L);

        Usuario usuarioEmailRepetido = new Usuario();
        usuarioEmailRepetido.setId(2L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(modelMapper.map(any(UsuarioExitDTO.class), eq(Usuario.class))).thenReturn(existente);
        when(usuarioRepository.findByCedula(anyInt())).thenReturn(Collections.emptyList());
        when(usuarioRepository.findOneByEmail("mail@mail.com")).thenReturn(Optional.of(usuarioEmailRepetido));

        assertThrows(BadRequestException.class,
                () -> usuarioService.actualizarUsuario(dto));
    }

    // -------------------------------------------------------
    // 7. ELIMINAR USUARIO
    // -------------------------------------------------------

    @Test
    void eliminarUsuario_exito() throws Exception {
        Usuario u = new Usuario();
        u.setId(1L);

        UsuarioExitDTO dto = new UsuarioExitDTO();
        dto.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(modelMapper.map(u, UsuarioExitDTO.class)).thenReturn(dto);

        UsuarioExitDTO result = usuarioService.eliminarUsuario(1L);

        assertNotNull(result);
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void eliminarUsuario_noEncontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.eliminarUsuario(1L));
    }
}
