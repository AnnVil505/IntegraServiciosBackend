package IntegraServiciosBackend.service.imp;

import IntegraServiciosBackend.dto.register.ReservaRegisterDTO;
import IntegraServiciosBackend.dto.exit.ReservaExitDTO;
import IntegraServiciosBackend.dto.exit.RecursoExitDTO;
import IntegraServiciosBackend.dto.exit.UsuarioExitDTO;
import IntegraServiciosBackend.entity.Recurso;
import IntegraServiciosBackend.entity.Reserva;
import IntegraServiciosBackend.entity.Usuario;
import IntegraServiciosBackend.entity.Dia;
import IntegraServiciosBackend.entity.Unidad;
import IntegraServiciosBackend.exceptions.BadRequestException;
import IntegraServiciosBackend.repository.RecursoRepository;
import IntegraServiciosBackend.repository.ReservaRepository;
import IntegraServiciosBackend.repository.UsuarioRepository;
import IntegraServiciosBackend.repository.UnidadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Locale;
import static java.time.format.TextStyle.FULL;
import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private RecursoRepository recursoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UnidadRepository unidadRepository;
    @Mock
    private UnidadServiceTest unidadService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReservaService reservaService;
    @InjectMocks
    private ReservaExitDTO reservaExitDTO;
    private RecursoExitDTO recursoExitDTO;
    private UsuarioExitDTO usuarioExitDTO;
    private Dia diaLunes;
    private Unidad unidad;
    private Reserva reservaEntidad;
    private Recurso recurso;
    private Usuario usuario;
    private ReservaRegisterDTO reservaRegisterDTO;

    @BeforeEach
    void setUp() {
        // --- 1. SETUP DE UNIDAD Y DÍA (CRÍTICO) ---
        // Asumiendo que el nombre en la BD es en minúsculas 'lunes'
        diaLunes = new Dia(1L, "Lunes");

        // Unidad con reglas de negocio base: Min 30m, Max 120m, Horario 08:00-20:00, disponible solo Lunes.
        unidad = new Unidad(
                5L, "Biblioteca Central", "Espacio", 30, 120,
                "08:00", "20:00", List.of(diaLunes)
        );

        // Asignar la Unidad al Recurso (CORRECCIÓN DEL NPE)
        recurso = new Recurso(1L, "Salón A-301", "Aula", "Aula de 40 personas", "url_img", unidad, Collections.emptyList());

        usuario = new Usuario(10L, "Juan Pérez", "pass", "juan@mail.com", 123456, LocalDateTime.now(), 1);

        // --- 2. SETUP DTO (CAMINO FELIZ) ---
        reservaRegisterDTO = new ReservaRegisterDTO();
        reservaRegisterDTO.setIdRecurso(1L);
        reservaRegisterDTO.setIdUsuario(10L);

        // Aseguramos que la fecha es Lunes para que pase la validación de día.
        LocalDate nextMonday = LocalDate.now();
        while (nextMonday.getDayOfWeek() != DayOfWeek.MONDAY) {
            nextMonday = nextMonday.plusDays(1);
        }
        reservaRegisterDTO.setFechaReserva(nextMonday);

        // Horario y duración válidos (60 minutos, dentro de 08:00-20:00)
        reservaRegisterDTO.setHoraInicio("10:00");
        reservaRegisterDTO.setHoraFin("11:00");
        reservaRegisterDTO.setEstado("Activa");

        // --- 3. SETUP MAPPERS Y ENTIDADES ---
        reservaEntidad = new Reserva(100L, "Activa", "10:00", "11:00", reservaRegisterDTO.getFechaReserva(), LocalDateTime.now(), null, null, usuario, recurso);
        recursoExitDTO = new RecursoExitDTO();
        usuarioExitDTO = new UsuarioExitDTO();
        reservaExitDTO = new ReservaExitDTO();
    }

    @Test
    @DisplayName("01. Registrar reserva exitosamente (Camino feliz)")
    void registrarReserva_success() throws BadRequestException {
        // Mocks de Búsqueda de Entidades (OK)
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));

        // Mock de Solapamiento (Retorna lista vacía, es decir, NO hay conflicto)
        when(reservaRepository.findOverlappingReservations(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Mocks de Mapeo y Guardado
        when(modelMapper.map(any(ReservaRegisterDTO.class), eq(Reserva.class))).thenReturn(reservaEntidad);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaEntidad);
        when(modelMapper.map(any(Reserva.class), eq(ReservaExitDTO.class))).thenReturn(reservaExitDTO);

        Object result = reservaService.registrarReserva(reservaRegisterDTO);

        assertNotNull(result);
        assertTrue(result instanceof ReservaExitDTO);

        // Verifica que la persistencia se llamó SÓLO si todo pasó
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    @DisplayName("02. Fallo al registrar: Recurso no encontrado")
    void registrarReserva_recursoNotFound() {
        when(recursoRepository.findById(1L)).thenReturn(Optional.empty());
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));

        // CORRECCIÓN: Usar assertThrows
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> reservaService.registrarReserva(reservaRegisterDTO));

        assertEquals("El recurso no existe.", exception.getMessage()); // Verificar el mensaje
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("03. Fallo al registrar: Usuario no encontrado")
    void registrarReserva_usuarioNotFound() {
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.empty());

        // CORRECCIÓN: Usar assertThrows
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> reservaService.registrarReserva(reservaRegisterDTO));

        assertEquals("El usuario no existe.", exception.getMessage()); // Verificar el mensaje
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("04. Fallo al registrar: Solapamiento de horario existente (Lógica de Negocio Crítica)")
    void registrarReserva_scheduleConflict() throws BadRequestException {
        // Setup base (Unidad, Recurso, Usuario)
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));

        // Mock que fuerza el conflicto: devuelve una lista con una reserva solapada
        Reserva reservaSolapada = new Reserva(99L, "Activa", "10:30", "11:30", reservaRegisterDTO.getFechaReserva(), LocalDateTime.now(), null, null, usuario, recurso);

        when(reservaRepository.findOverlappingReservations(any(), any(), any(), any()))
                .thenReturn(List.of(reservaSolapada));

        assertThrows(BadRequestException.class,
                () -> reservaService.registrarReserva(reservaRegisterDTO),
                "Debe lanzar una excepción por solapamiento de horario");

        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    // --- NUEVOS TESTS DE VALIDACIÓN (RECOMENDADOS) ---
    @Test
    @DisplayName("05. Fallo al registrar: Duración menor al mínimo de la unidad")
    void registrarReserva_durationTooShort() {
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));

        // Reserva de 15 minutos (mínimo de la unidad mockeada es 30 minutos)
        reservaRegisterDTO.setHoraFin("10:15");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> reservaService.registrarReserva(reservaRegisterDTO));

        assertEquals("La reserva debe durar al menos 30 minutos.", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("06. Fallo al registrar: Horario de inicio fuera del horario de la unidad")
    void registrarReserva_startTimeOutOfUnitHours() {
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));

        // Corrección: Establecer un horario que tenga una duración válida (60m)
        reservaRegisterDTO.setHoraInicio("07:00"); // Empieza antes de 08:00 (Fallo esperado)
        reservaRegisterDTO.setHoraFin("08:00"); // Duración de 60 minutos (Válida)

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> reservaService.registrarReserva(reservaRegisterDTO));

        // El mensaje esperado es de la validación de la Unidad (08:00 a 20:00)
        assertEquals("El horario de la reserva está fuera del horario de disponibilidad de la unidad (08:00 a 20:00).", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("07. Fallo al registrar: Día de la semana no disponible")
    void registrarReserva_dayOfWeekNotAvailable() {
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));

        // Establecer la fecha de reserva para un martes (que no está disponible)
        LocalDate tuesday = LocalDate.now();
        while (tuesday.getDayOfWeek() != DayOfWeek.TUESDAY) {
            tuesday = tuesday.plusDays(1);
        }
        reservaRegisterDTO.setFechaReserva(tuesday);

        // Corrección: Esperar el nombre del día en inglés que usa el Enum
        String nombreDiaEnIngles = tuesday.getDayOfWeek().toString(); // "TUESDAY"

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> reservaService.registrarReserva(reservaRegisterDTO));

        // El mensaje de error coincidirá con el mensaje de tu servicio (que usa TUESDAY)
        assertEquals("El recurso no está disponible el día " + nombreDiaEnIngles + ".", exception.getMessage());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }
}
