package IntegraServiciosBackend.service.imp;

import IntegraServiciosBackend.dto.register.ReservaRegisterDTO;
import IntegraServiciosBackend.dto.exit.ReservaExitDTO;
import IntegraServiciosBackend.entity.Reserva;
import IntegraServiciosBackend.entity.Recurso;
import IntegraServiciosBackend.entity.Usuario;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.exceptions.ResourceNotFoundException;
import IntegraServiciosBackend.repository.ReservaRepository;
import IntegraServiciosBackend.repository.RecursoRepository;
import IntegraServiciosBackend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    // Dependencias a simular (mockear)
    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private RecursoRepository recursoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ModelMapper modelMapper;

    // La clase que queremos probar, inyectando los mocks
    @InjectMocks
    private ReservaService reservaService;

    // --- Datos de prueba reutilizables ---
    private UUID recursoId;
    private UUID usuarioId;
    private UUID reservaId;
    private Recurso recursoPrueba;
    private Usuario usuarioPrueba;
    private LocalDate fechaPrueba;

    @BeforeEach
    void setUp() {
        // Inicializar datos comunes para las pruebas
        recursoId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        reservaId = UUID.randomUUID();
        fechaPrueba = LocalDate.of(2025, 11, 10);

        recursoPrueba = new Recurso();
        recursoPrueba.setRecursoId(recursoId);
        recursoPrueba.setNombre("Salón 201");

        usuarioPrueba = new Usuario();
        usuarioPrueba.setUsuarioId(usuarioId);
        usuarioPrueba.setNombreCompleto("Usuario de Prueba");
    }

    // --- Pruebas para crearReserva ---

    @Test
    void deberiaCrearReserva_CuandoNoHayConflictos() throws BadRequestException, ResourceNotFoundException {
        // 1. Given (Dado)
        ReservaRegisterDTO dto = new ReservaRegisterDTO(recursoId, usuarioId, fechaPrueba, LocalTime.of(10, 0), LocalTime.of(11, 0));
        
        Reserva reservaMapeada = new Reserva(); // Simula el DTO mapeado a Entidad
        reservaMapeada.setFecha(dto.getFecha());
        reservaMapeada.setInicio(dto.getInicio());
        reservaMapeada.setFin(dto.getFin());

        Reserva reservaGuardada = new Reserva(); // Simula la entidad guardada en BD
        reservaGuardada.setReservaId(reservaId);
        reservaGuardada.setEstado("pendiente");
        reservaGuardada.setRecurso(recursoPrueba);
        reservaGuardada.setUsuario(usuarioPrueba);
        
        ReservaExitDTO exitDTO = new ReservaExitDTO(); // Simula la entidad mapeada a DTO de salida
        exitDTO.setReservaId(reservaId);
        exitDTO.setEstado("pendiente");

        // Simulación de los repositorios y mapper
        when(recursoRepository.findById(recursoId)).thenReturn(Optional.of(recursoPrueba));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioPrueba));
        when(reservaRepository.findByRecurso_RecursoIdAndFecha(recursoId, fechaPrueba)).thenReturn(Collections.emptyList()); // No hay reservas existentes
        when(modelMapper.map(dto, Reserva.class)).thenReturn(reservaMapeada);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);
        when(modelMapper.map(reservaGuardada, ReservaExitDTO.class)).thenReturn(exitDTO);

        // 2. When (Cuando)
        ReservaExitDTO resultado = reservaService.crearReserva(dto);

        // 3. Then (Entonces)
        assertNotNull(resultado);
        assertEquals(reservaId, resultado.getReservaId());
        assertEquals("pendiente", resultado.getEstado());
        
        // Verificar que los métodos correctos fueron llamados
        verify(recursoRepository, times(1)).findById(recursoId);
        verify(usuarioRepository, times(1)).findById(usuarioId);
        verify(reservaRepository, times(1)).findByRecurso_RecursoIdAndFecha(recursoId, fechaPrueba);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    void deberiaLanzarBadRequest_CuandoHayConflictoDeHorario() {
        // 1. Given (Dado)
        // Reserva nueva de 10:00 a 11:00
        ReservaRegisterDTO dto = new ReservaRegisterDTO(recursoId, usuarioId, fechaPrueba, LocalTime.of(10, 0), LocalTime.of(11, 0));

        // Reserva existente de 09:30 a 10:30 (se solapa)
        Reserva reservaExistente = new Reserva();
        reservaExistente.setInicio(LocalTime.of(9, 30));
        reservaExistente.setFin(LocalTime.of(10, 30));

        // Simulación de los repositorios
        when(recursoRepository.findById(recursoId)).thenReturn(Optional.of(recursoPrueba));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioPrueba));
        when(reservaRepository.findByRecurso_RecursoIdAndFecha(recursoId, fechaPrueba)).thenReturn(List.of(reservaExistente));

        // 2. When (Cuando) y 3. Then (Entonces)
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            reservaService.crearReserva(dto);
        });

        assertEquals("El recurso ya está reservado en ese horario.", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class)); // Verificar que NUNCA se guardó
    }

    @Test
    void deberiaLanzarResourceNotFound_CuandoRecursoNoExiste() {
        // 1. Given
        ReservaRegisterDTO dto = new ReservaRegisterDTO(recursoId, usuarioId, fechaPrueba, LocalTime.of(10, 0), LocalTime.of(11, 0));
        when(recursoRepository.findById(recursoId)).thenReturn(Optional.empty()); // Recurso no encontrado

        // 2. When y 3. Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            reservaService.crearReserva(dto);
        });
        
        assertTrue(exception.getMessage().contains("No existe el recurso"));
    }

    // --- Pruebas para cancelarReserva ---

    @Test
    void deberiaCancelarReserva_CuandoEstadoEsPendiente() throws BadRequestException, ResourceNotFoundException {
        // 1. Given
        Reserva reservaPendiente = new Reserva();
        reservaPendiente.setReservaId(reservaId);
        reservaPendiente.setEstado("pendiente"); // Estado correcto para cancelar

        Reserva reservaCancelada = new Reserva();
        reservaCancelada.setReservaId(reservaId);
        reservaCancelada.setEstado("cancelada");

        ReservaExitDTO exitDTO = new ReservaExitDTO();
        exitDTO.setReservaId(reservaId);
        exitDTO.setEstado("cancelada");

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reservaPendiente));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaCancelada);
        when(modelMapper.map(reservaCancelada, ReservaExitDTO.class)).thenReturn(exitDTO);

        // 2. When
        ReservaExitDTO resultado = reservaService.cancelarReserva(reservaId);

        // 3. Then
        assertNotNull(resultado);
        assertEquals("cancelada", resultado.getEstado());
        verify(reservaRepository, times(1)).save(reservaPendiente); // Verifica que se guardó la entidad correcta
        assertEquals("cancelada", reservaPendiente.getEstado()); // Verifica que el estado se cambió antes de guardar
    }

    @Test
    void deberiaLanzarBadRequest_CuandoIntentaCancelarReservaNoPendiente() {
        // 1. Given
        Reserva reservaConfirmada = new Reserva();
        reservaConfirmada.setReservaId(reservaId);
        reservaConfirmada.setEstado("confirmada"); // Estado incorrecto

        when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reservaConfirmada));

        // 2. When y 3. Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            reservaService.cancelarReserva(reservaId);
        });

        assertEquals("Solo las reservas pendientes pueden cancelarse.", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class)); // No se debe llamar a save
    }

    @Test
    void deberiaLanzarResourceNotFound_CuandoCancelaReservaNoExistente() {
        // 1. Given
        when(reservaRepository.findById(reservaId)).thenReturn(Optional.empty());

        // 2. When y 3. Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reservaService.cancelarReserva(reservaId);
        });
    }
}