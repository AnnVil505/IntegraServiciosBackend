package IntegraServiciosBackend.service;

import IntegraServiciosBackend.entity.Dia;
import IntegraServiciosBackend.repository.DiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DiaRepository diaRepository;

    @Override
    public void run(String... args) throws Exception {
        // Lista de días de la semana
        List<String> diasSemana = Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

        // Verificar si ya existen días registrados
        long count = diaRepository.count();
        if (count == 0) {
            // Crear y guardar los días
            diasSemana.forEach(nombre -> {
                Dia dia = new Dia();
                dia.setNombre(nombre);
                diaRepository.save(dia);
            });
        }
    }
}