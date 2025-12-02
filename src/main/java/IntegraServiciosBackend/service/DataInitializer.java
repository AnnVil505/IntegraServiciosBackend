package IntegraServiciosBackend.service;

import IntegraServiciosBackend.entity.Dia;
import IntegraServiciosBackend.entity.Usuario;
import IntegraServiciosBackend.repository.DiaRepository;
import IntegraServiciosBackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DiaRepository diaRepository;

    // Dependencias añadidas para la inicialización de Usuarios
    @Autowired
    private UsuarioRepository usuarioRepository; 
    

    @Override
    public void run(String... args) throws Exception {
        // 1. Inicializar Días
        initializeDias();

        // 2. Inicializar Usuarios (los 3 registros solicitados)
        initializeUsuarios();
    }
    
    // =======================================================================
    // LÓGICA DE INICIALIZACIÓN
    // =======================================================================

    private void initializeDias() {
        List<String> diasSemana = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

        if (diaRepository.count() == 0) {
            diasSemana.forEach(nombre -> {
                Dia dia = new Dia();
                dia.setNombre(nombre);
                diaRepository.save(dia);
            });
            System.out.println("✅ Data Seeding: Días de la semana creados.");
        }
    }
    
    private void initializeUsuarios() {
        // Usuario 1: Ana Pérez (Rol 1: Usuario)
        if (usuarioRepository.findOneByEmail("ana.perez@user.com").isEmpty()) {
            Usuario ana = new Usuario();
            ana.setFullname("Ana Pérez");
            ana.setEmail("ana.perez@user.com");
            ana.setCedula(1001234567);
            ana.setRol(1); // Usuario normal
            ana.setFechaRegistro(LocalDateTime.of(2024, 1, 10, 10, 0, 0));
            ana.setContraseña("123AP");
            usuarioRepository.save(ana);
            System.out.println("Data Seeding: Usuario 'Ana Pérez' (ana.perez@user.com)");
        }

        // Usuario 2: Luis Gómez (Rol 1: Usuario)
        if (usuarioRepository.findOneByEmail("luis.gomez@user.com").isEmpty()) {
            Usuario luis = new Usuario();
            luis.setFullname("Luis Gómez");
            luis.setEmail("luis.gomez@user.com");
            luis.setCedula(1009876543);
            luis.setRol(1); // Usuario normal
            luis.setFechaRegistro(LocalDateTime.of(2024, 3, 20, 12, 30, 0));
            luis.setContraseña("123LG"); 
            usuarioRepository.save(luis);
            System.out.println("Data Seeding: Usuario 'Luis Gómez' (luis.gomez@user.com)");
        }

        // Usuario 3: María López (Rol 2: Empleado/Administrativo)
        if (usuarioRepository.findOneByEmail("maria.lopez@ud.edu").isEmpty()) {
            Usuario maria = new Usuario();
            maria.setFullname("María López");
            maria.setEmail("maria.lopez@ud.edu");
            maria.setCedula(1005555555);
            maria.setRol(2); // Empleado
            maria.setFechaRegistro(LocalDateTime.of(2024, 4, 1, 8, 0, 0));
            maria.setContraseña("123ML");
            usuarioRepository.save(maria);
            System.out.println("Data Seeding: Usuario 'María López' (maria.lopez@ud.edu)");
        }
    }
}