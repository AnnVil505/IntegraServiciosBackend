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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecursoServiceTest {

    @Mock
    private RecursoRepository recursoRepository;

    @Mock
    private DiaRepository diaRepository;

    @Mock
    private UnidadRepository unidadRepository;

    @Mock
    private HorarioDisponibleRecursoRepository horarioDisponibleRecursoRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RecursoService recursoService;

    // ---------------------------
    // Helpers para crear objetos
    // ---------------------------
    private Unidad buildUnidad(Long id, String horaInicio, String horaFinal, List<Dia> diasDisponibles) {
        Unidad u = new Unidad();
        u.setId(id);
        u.setHoraInicio(horaInicio);
        u.setHoraFinal(horaFinal);
        u.setDiasDisponibles(diasDisponibles);
        return u;
    }

    private Dia buildDia(Long id, String nombre) {
        Dia d = new Dia();
        d.setId(id);
        d.setNombre(nombre);
        return d;
    }

    private HorarioDisponibleRecurso buildHorario(Long id, Dia dia, String hi, String hf) {
        HorarioDisponibleRecurso h = new HorarioDisponibleRecurso();
        h.setId(id);
        h.setDia(dia);
        h.setHoraInicio(hi);
        h.setHoraFin(hf);
        return h;
    }

    private Recurso buildRecurso(Long id, Unidad unidad, List<HorarioDisponibleRecurso> horarios) {
        Recurso r = new Recurso();
        r.setId(id);
        r.setUnidad(unidad);
        r.setHorarioDisponible(horarios == null ? new ArrayList<>() : new ArrayList<>(horarios));
        return r;
    }

    // -------------------------------------------
    // registrarRecurso - casos exitoso y fallidos
    // -------------------------------------------
    @Test
    void registrarRecurso_Exito() throws BadRequestException {
        // Arrange
        RecursoRegisterDTO registerDTO = new RecursoRegisterDTO();
        registerDTO.setUnidad(10L);
        HorarioDisponibleRecursoRegisterDTO horarioDTO = new HorarioDisponibleRecursoRegisterDTO();
        horarioDTO.setDia(1L);
        horarioDTO.setHoraInicio("08:00");
        horarioDTO.setHoraFin("10:00");
        registerDTO.setHorarioDisponible(List.of(horarioDTO));

        Dia dia = buildDia(1L, "Lunes");
        Unidad unidad = buildUnidad(10L, "07:00", "18:00", List.of(dia));

        // Entidades/DTOs mapeadas/guardadas
        HorarioDisponibleRecurso savedHorario = buildHorario(100L, dia, "08:00", "10:00");
        Recurso recursoEntidad = buildRecurso(null, unidad, List.of(savedHorario));
        Recurso recursoGuardado = buildRecurso(1L, unidad, List.of(savedHorario));
        RecursoExitDTO salida = new RecursoExitDTO();
        salida.setId(1L);

        when(unidadRepository.findById(10L)).thenReturn(Optional.of(unidad));
        when(diaRepository.findById(1L)).thenReturn(Optional.of(dia));
        when(horarioDisponibleRecursoRepository.save(any(HorarioDisponibleRecurso.class))).thenReturn(savedHorario);
        when(modelMapper.map(eq(registerDTO), eq(Recurso.class))).thenReturn(recursoEntidad);
        when(recursoRepository.save(any(Recurso.class))).thenReturn(recursoGuardado);
        when(modelMapper.map(eq(recursoGuardado), eq(RecursoExitDTO.class))).thenReturn(salida);

        // Act
        Object resultObj = recursoService.registrarRecurso(registerDTO);

        // Assert
        assertNotNull(resultObj);
        assertTrue(resultObj instanceof RecursoExitDTO);
        RecursoExitDTO result = (RecursoExitDTO) resultObj;
        assertEquals(1L, result.getId());

        // Verificaciones finas
        verify(unidadRepository).findById(10L);
        verify(diaRepository).findById(1L);
        verify(horarioDisponibleRecursoRepository).save(any(HorarioDisponibleRecurso.class));
        ArgumentCaptor<Recurso> captor = ArgumentCaptor.forClass(Recurso.class);
        verify(recursoRepository).save(captor.capture());
        Recurso savedArg = captor.getValue();
        assertEquals(unidad, savedArg.getUnidad());
        assertEquals(1, savedArg.getHorarioDisponible().size());
    }

    @Test
    void registrarRecurso_UnidadNoExiste_RetornaMensaje() throws BadRequestException{
        // Arrange
        RecursoRegisterDTO registerDTO = new RecursoRegisterDTO();
        registerDTO.setUnidad(999L);

        when(unidadRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Object result = recursoService.registrarRecurso(registerDTO);

        // Assert
        assertTrue(result instanceof String);
        assertEquals("La unidad no existe", result);
        verify(unidadRepository).findById(999L);
        verifyNoMoreInteractions(diaRepository, horarioDisponibleRecursoRepository, recursoRepository);
    }

    @Test
    void registrarRecurso_DiaNoEncontrado_ProduceNoSuchElementException() {
        // Arrange
        RecursoRegisterDTO registerDTO = new RecursoRegisterDTO();
        registerDTO.setUnidad(10L);
        HorarioDisponibleRecursoRegisterDTO horarioDTO = new HorarioDisponibleRecursoRegisterDTO();
        horarioDTO.setDia(5L);
        horarioDTO.setHoraInicio("08:00");
        horarioDTO.setHoraFin("09:00");
        registerDTO.setHorarioDisponible(List.of(horarioDTO));

        Dia existingDia = buildDia(1L, "Lunes");
        Unidad unidad = buildUnidad(10L, "07:00", "18:00", List.of(existingDia));

        when(unidadRepository.findById(10L)).thenReturn(Optional.of(unidad));
        // diaRepository no tiene el id 5
        when(diaRepository.findById(5L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> recursoService.registrarRecurso(registerDTO));
        verify(diaRepository).findById(5L);
    }

    @Test
    void registrarRecurso_DiaNoPermitido_RetornaMensaje() throws BadRequestException {
        // Arrange
        RecursoRegisterDTO registerDTO = new RecursoRegisterDTO();
        registerDTO.setUnidad(10L);
        HorarioDisponibleRecursoRegisterDTO horarioDTO = new HorarioDisponibleRecursoRegisterDTO();
        horarioDTO.setDia(2L); // día 2 no está en la unidad
        horarioDTO.setHoraInicio("08:00");
        horarioDTO.setHoraFin("09:00");
        registerDTO.setHorarioDisponible(List.of(horarioDTO));

        Dia dia2 = buildDia(2L, "Martes");
        Dia diaUnidad = buildDia(1L, "Lunes");
        Unidad unidad = buildUnidad(10L, "07:00", "18:00", List.of(diaUnidad));

        when(unidadRepository.findById(10L)).thenReturn(Optional.of(unidad));
        when(diaRepository.findById(2L)).thenReturn(Optional.of(dia2));

        // Act
        Object result = recursoService.registrarRecurso(registerDTO);

        // Assert
        assertTrue(result instanceof String);
        assertEquals("Día de disponibilidad inválido por dias de unidad", result);
    }

    // --------------------------------
    // listarRecursos
    // --------------------------------
    @Test
    void listarRecursos_ListaVacia() throws BadRequestException {
        // Arrange
        when(recursoRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<RecursoExitDTO> result = recursoService.listarRecursos();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(recursoRepository).findAll();
    }

    // --------------------------------
    // buscarRecursoPorId
    // --------------------------------
    @Test
    void buscarRecursoPorId_Found() throws ResourceNotFoundException, BadRequestException {
        // Arrange
        Unidad unidad = buildUnidad(1L, "07:00", "18:00", List.of());
        Recurso recurso = buildRecurso(5L, unidad, List.of());
        RecursoExitDTO salida = new RecursoExitDTO();
        salida.setId(5L);

        when(recursoRepository.findById(5L)).thenReturn(Optional.of(recurso));
        when(modelMapper.map(recurso, RecursoExitDTO.class)).thenReturn(salida);

        // Act
        RecursoExitDTO result = recursoService.buscarRecursoPorId(5L);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getId());
        verify(recursoRepository).findById(5L);
        verify(modelMapper).map(recurso, RecursoExitDTO.class);
    }

    @Test
    void buscarRecursoPorId_NotFound_Throws() {
        // Arrange
        when(recursoRepository.findById(100L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> recursoService.buscarRecursoPorId(100L));
        verify(recursoRepository).findById(100L);
    }

    // --------------------------------
    // eliminarRecurso
    // --------------------------------
    @Test
    void eliminarRecurso_Exito() throws ResourceNotFoundException, ResourceNotFoundException, BadRequestException {
        // Arrange
        Unidad unidad = buildUnidad(1L, "07:00", "18:00", List.of());
        Recurso recurso = buildRecurso(5L, unidad, List.of());
        RecursoExitDTO salida = new RecursoExitDTO();
        salida.setId(5L);

        when(recursoRepository.findById(5L)).thenReturn(Optional.of(recurso));
        when(modelMapper.map(recurso, RecursoExitDTO.class)).thenReturn(salida);

        // Act
        RecursoExitDTO result = recursoService.eliminarRecurso(5L);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getId());
        verify(recursoRepository).findById(5L);
        verify(recursoRepository).deleteById(5L);
    }

    @Test
    void eliminarRecurso_NotFound_Throws() {
        // Arrange
        when(recursoRepository.findById(6L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> recursoService.eliminarRecurso(6L));
        verify(recursoRepository).findById(6L);
        verify(recursoRepository, never()).deleteById(anyLong());
    }

    // --------------------------------
    // actualizarRecurso
    // --------------------------------
    @Test
    void actualizarRecurso_Exito() throws ResourceNotFoundException, BadRequestException {
        // Arrange
        // Recurso existente
        Dia dia = buildDia(1L, "Lunes");
        Unidad unidad = buildUnidad(20L, "07:00", "20:00", List.of(dia));
        HorarioDisponibleRecursoModificationDTO hDto = new HorarioDisponibleRecursoModificationDTO();
        hDto.setDia(1L);
        hDto.setHoraInicio("08:00");
        hDto.setHoraFin("09:00");

        RecursoModificationDTO modDto = new RecursoModificationDTO();
        modDto.setId(50L);
        modDto.setUnidad(20L);
        modDto.setHorarioDisponible(List.of(hDto));

        Recurso recursoExistente = buildRecurso(50L, unidad, List.of(buildHorario(200L, dia, "07:00", "08:00")));

        // Mocks
        when(recursoRepository.findById(50L)).thenReturn(Optional.of(recursoExistente));
        when(unidadRepository.findById(20L)).thenReturn(Optional.of(unidad));
        when(diaRepository.findById(1L)).thenReturn(Optional.of(dia));
        // when saving horarios, return the same objects (simulate save)
        when(horarioDisponibleRecursoRepository.save(any(HorarioDisponibleRecurso.class)))
                .thenAnswer(inv -> {
                    HorarioDisponibleRecurso arg = inv.getArgument(0);
                    if (arg.getId() == null) arg.setId(999L);
                    return arg;
                });

        Recurso recursoGuardado = buildRecurso(50L, unidad, List.of(buildHorario(999L, dia, "08:00", "09:00")));
        when(recursoRepository.save(any(Recurso.class))).thenReturn(recursoGuardado);
        when(horarioDisponibleRecursoRepository.findAll()).thenReturn(List.of(buildHorario(999L, dia, "08:00", "09:00")));
        when(modelMapper.map(recursoGuardado, RecursoExitDTO.class)).thenReturn(new RecursoExitDTO(){{
            setId(50L);
        }});

        // Act
        RecursoExitDTO salida = recursoService.actualizarRecurso(modDto);

        // Assert
        assertNotNull(salida);
        assertEquals(50L, salida.getId());
        verify(recursoRepository).findById(50L);
        verify(unidadRepository).findById(20L);
        verify(diaRepository).findById(1L);
        verify(horarioDisponibleRecursoRepository).save(any(HorarioDisponibleRecurso.class));
        verify(recursoRepository).save(any(Recurso.class));
        verify(horarioDisponibleRecursoRepository).deleteAll(anyList());
    }

    @Test
    void actualizarRecurso_NotFound_Recurso() {
        // Arrange
        RecursoModificationDTO modDto = new RecursoModificationDTO();
        modDto.setId(999L);

        when(recursoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> recursoService.actualizarRecurso(modDto));
        verify(recursoRepository).findById(999L);
    }

    @Test
    void actualizarRecurso_BadRequest_DiaInvalido() {
        // Arrange
        Dia diaNoPermitido = buildDia(2L, "Martes");
        Unidad unidad = buildUnidad(30L, "07:00", "20:00", List.of(buildDia(1L, "Lunes")));

        RecursoModificationDTO modDto = new RecursoModificationDTO();
        modDto.setId(77L);
        modDto.setUnidad(30L);
        HorarioDisponibleRecursoModificationDTO hDto = new HorarioDisponibleRecursoModificationDTO();
        hDto.setDia(2L); // dia no permitido por la unidad
        hDto.setHoraInicio("08:00");
        hDto.setHoraFin("09:00");
        modDto.setHorarioDisponible(List.of(hDto));

        Recurso recursoExistente = buildRecurso(77L, unidad, List.of());

        when(recursoRepository.findById(77L)).thenReturn(Optional.of(recursoExistente));
        when(unidadRepository.findById(30L)).thenReturn(Optional.of(unidad));
        when(diaRepository.findById(2L)).thenReturn(Optional.of(diaNoPermitido));

        // Act & Assert - service throws BadRequestException for invalid day
        assertThrows(BadRequestException.class, () -> recursoService.actualizarRecurso(modDto));
        verify(diaRepository).findById(2L);
    }
}
