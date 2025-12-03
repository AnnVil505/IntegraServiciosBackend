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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnidadServiceTest {

    @Mock
    private UnidadRepository unidadRepository;

    @Mock
    private DiaRepository diaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UnidadService unidadService;

    private Unidad unidad;
    private Dia dia1;
    private Dia dia2;

    @BeforeEach
    void setup() {
        dia1 = new Dia();
        dia1.setId(1L);

        dia2 = new Dia();
        dia2.setId(2L);

        unidad = new Unidad();
        unidad.setId(10L);
        unidad.setDiasDisponibles(Arrays.asList(dia1, dia2));
    }

    // -----------------------------------------------------------------------------------------
    // REGISTRAR UNIDAD
    // -----------------------------------------------------------------------------------------

    @Test
    void registrarUnidad_ok() throws BadRequestException {
        UnidadRegisterDTO dto = new UnidadRegisterDTO();
        dto.setDiasDisponibles(Arrays.asList(1L, 2L));

        when(diaRepository.findAllById(dto.getDiasDisponibles()))
                .thenReturn(Arrays.asList(dia1, dia2));

        when(modelMapper.map(dto, Unidad.class)).thenReturn(unidad);
        when(unidadRepository.save(unidad)).thenReturn(unidad);
        when(modelMapper.map(unidad, UnidadExitDTO.class)).thenReturn(new UnidadExitDTO());

        UnidadExitDTO salida = unidadService.registrarUnidad(dto);

        assertNotNull(salida);
        verify(unidadRepository, times(1)).save(unidad);
    }

    @Test
    void registrarUnidad_diasInvalidos_exception() {
        UnidadRegisterDTO dto = new UnidadRegisterDTO();
        dto.setDiasDisponibles(Arrays.asList(1L, 2L));

        when(diaRepository.findAllById(dto.getDiasDisponibles()))
                .thenReturn(Arrays.asList(dia1)); // falta uno → ERROR

        assertThrows(BadRequestException.class, () -> unidadService.registrarUnidad(dto));
    }

    // -----------------------------------------------------------------------------------------
    // BUSCAR POR ID
    // -----------------------------------------------------------------------------------------

    @Test
    void buscarUnidadPorId_ok() throws ResourceNotFoundException {
        when(unidadRepository.findById(10L)).thenReturn(Optional.of(unidad));
        when(modelMapper.map(unidad, UnidadExitDTO.class)).thenReturn(new UnidadExitDTO());

        UnidadExitDTO salida = unidadService.buscarUnidadPorId(10L);

        assertNotNull(salida);
        verify(unidadRepository, times(1)).findById(10L);
    }

    @Test
    void buscarUnidadPorId_noExiste_exception() {
        when(unidadRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> unidadService.buscarUnidadPorId(99L));
    }

    // -----------------------------------------------------------------------------------------
    // LISTAR
    // -----------------------------------------------------------------------------------------

    @Test
    void listarUnidades_ok() {

        when(unidadRepository.findAll()).thenReturn(Arrays.asList(unidad));
        when(modelMapper.map(unidad, UnidadExitDTO.class)).thenReturn(new UnidadExitDTO());

        List<UnidadExitDTO> lista = unidadService.listarUnidades();

        assertEquals(1, lista.size());
        verify(unidadRepository, times(1)).findAll();
    }

    // -----------------------------------------------------------------------------------------
    // ACTUALIZAR
    // -----------------------------------------------------------------------------------------

    @Test
    void actualizarUnidad_ok() throws Exception {
        UnidadModificationDTO dto = new UnidadModificationDTO();
        dto.setId(10L);
        dto.setDiasDisponibles(Arrays.asList(1L, 2L));

        when(unidadRepository.findById(10L)).thenReturn(Optional.of(unidad));
        when(diaRepository.findAllById(dto.getDiasDisponibles()))
                .thenReturn(Arrays.asList(dia1, dia2));

        when(modelMapper.map(dto, Unidad.class)).thenReturn(unidad);
        when(unidadRepository.save(unidad)).thenReturn(unidad);
        when(modelMapper.map(unidad, UnidadExitDTO.class)).thenReturn(new UnidadExitDTO());

        UnidadExitDTO salida = unidadService.actualizarUnidad(dto);

        assertNotNull(salida);
        verify(unidadRepository, times(1)).save(unidad);
    }

    @Test
    void actualizarUnidad_noExiste_exception() {
        UnidadModificationDTO dto = new UnidadModificationDTO();
        dto.setId(50L);

        when(unidadRepository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> unidadService.actualizarUnidad(dto));
    }

    @Test
    void actualizarUnidad_diasInvalidos_exception() {
        UnidadModificationDTO dto = new UnidadModificationDTO();
        dto.setId(10L);
        dto.setDiasDisponibles(Arrays.asList(1L, 2L));

        when(unidadRepository.findById(10L)).thenReturn(Optional.of(unidad));
        when(diaRepository.findAllById(dto.getDiasDisponibles()))
                .thenReturn(Arrays.asList(dia1)); // Falta un día → error

        assertThrows(BadRequestException.class, () -> unidadService.actualizarUnidad(dto));
    }

    // -----------------------------------------------------------------------------------------
    // ELIMINAR
    // -----------------------------------------------------------------------------------------

    @Test
    void eliminarUnidad_ok() throws Exception {
        when(unidadRepository.findById(10L)).thenReturn(Optional.of(unidad));
        when(modelMapper.map(unidad, UnidadExitDTO.class))
                .thenReturn(new UnidadExitDTO());

        UnidadExitDTO salida = unidadService.eliminarUnidad(10L);

        assertNotNull(salida);
        verify(unidadRepository, times(1)).deleteById(10L);
    }

    @Test
    void eliminarUnidad_noExiste_exception() {
        when(unidadRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> unidadService.eliminarUnidad(999L));
    }
}
