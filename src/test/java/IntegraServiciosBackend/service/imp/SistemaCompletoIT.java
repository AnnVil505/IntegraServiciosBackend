package IntegraServiciosBackend.service.imp;

import IntegraServiciosBackend.repository.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import IntegraServiciosBackend.entity.Dia;
import IntegraServiciosBackend.entity.Recurso;
import IntegraServiciosBackend.entity.Reserva;
import IntegraServiciosBackend.entity.Unidad;
import IntegraServiciosBackend.entity.Usuario;

@SpringBootTest
@ActiveProfiles("test")   // Usa H2 en vez de PostgreSQL
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SistemaCompletoIT {

    @Autowired
    private DiaRepository diaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UnidadRepository unidadRepository;
    @Autowired
    private RecursoRepository recursoRepository;
    @Autowired
    private ReservaRepository reservaRepository;

    // --------------------------
    // 1. CREAR DATOS BASE
    // --------------------------
    private void crearDias() {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        for (String d : dias) {
            Dia dia = new Dia();
            dia.setNombre(d);
            diaRepository.save(dia);
        }
    }

    private Usuario crearUsuario() {
        Usuario u = new Usuario();
        u.setFullname("Usuario Test");
        u.setEmail("test@correo.com");
        u.setCedula(100111222);
        u.setRol(1);
        u.setFechaRegistro(LocalDateTime.now());
        u.setContraseña("abc123");
        return usuarioRepository.save(u);
    }

    private Unidad crearUnidad() {
        Unidad unidad = new Unidad();
        unidad.setNombre("Unidad Falsa");
        unidad.setHoraInicio("07:00");
        unidad.setHoraFinal("21:00");
        unidad.setTiempoMinimo(30);
        unidad.setTiempoMaximo(240);
        unidad.setTipo("Principal");

        // asignar lunes a viernes
        List<Dia> dias = StreamSupport.stream(
                diaRepository.findAll().spliterator(),
                false
        ).filter(d -> d.getId() <= 5)
                .toList();

        unidad.setDiasDisponibles(dias);

        return unidadRepository.save(unidad);
    }

    private Recurso crearRecurso(Unidad unidad) {
        Recurso r = new Recurso();
        r.setNombre("Recurso Falso");
        r.setDescripcion("Recurso creado solo para pruebas");
        r.setTipo("Salón");
        r.setImageUrl("/test.png");
        r.setUnidad(unidad);
        return recursoRepository.save(r);
    }

    // --------------------------
    // 2. PRUEBA COMPLETA
    // --------------------------
    @Test
    void flujoCompleto_deberiaFuncionarCorrectamente() {

        // Crear datos base
        crearDias();
        Usuario usuario = crearUsuario();
        Unidad unidad = crearUnidad();
        Recurso recurso = crearRecurso(unidad);

        assertNotNull(unidad.getId());
        assertEquals(5, unidad.getDiasDisponibles().size());
        assertNotNull(recurso.getId());
        assertNotNull(usuario.getId());

        // --------------------------------
        // 3. CREAR RESERVA (PENDIENTE)
        // --------------------------------
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setRecurso(recurso);
        reserva.setEstado("Activa");
        reserva.setFechaReserva(LocalDate.now().plusDays(1));
        reserva.setHoraInicio("10:00");
        reserva.setHoraFin("12:00");
        reserva.setFechaCreacion(LocalDateTime.now());

        Reserva r1 = reservaRepository.save(reserva);

        assertNotNull(r1.getId());
        assertEquals("Activa", r1.getEstado());

        // -------------------------------
        // 4. SIMULAR PRÉSTAMO
        // -------------------------------
        r1.setEstado("Prestamo");
        r1.setFechaPrestamo(LocalDateTime.now());
        Reserva r2 = reservaRepository.save(r1);

        assertEquals("Prestamo", r2.getEstado());
        assertNotNull(r2.getFechaPrestamo());

        // -------------------------------
        // 5. SIMULAR DEVOLUCIÓN
        // -------------------------------
        r2.setEstado("Devolucion");
        r2.setFechaDevolucion(LocalDateTime.now());
        Reserva r3 = reservaRepository.save(r2);

        assertEquals("Devolucion", r3.getEstado());
        assertNotNull(r3.getFechaDevolucion());

        // -------------------------------
        // 6. VERIFICAR QUE TODO ESTÁ OK
        // -------------------------------
        List<Reserva> reservas = reservaRepository.findAll();
        assertEquals(1, reservas.size());
        assertEquals("Devolucion", reservas.get(0).getEstado());
    }
}
