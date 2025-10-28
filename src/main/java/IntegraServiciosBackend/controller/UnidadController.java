package IntegraServiciosBackend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import IntegraServiciosBackend.entity.Unidad;
import IntegraServiciosBackend.repository.UnidadRepository;

@RestController
@RequestMapping("/api/unidades")
@CrossOrigin(origins = "*") // permite probar desde el navegador o Postman
public class UnidadController {

private final UnidadRepository unidadRepository;

public UnidadController(UnidadRepository unidadRepository) {
    this.unidadRepository = unidadRepository;
}

// Obtener todas las unidades
@GetMapping
public List<Unidad> getAllUnidades() {
    return unidadRepository.findAll();
}

// Crear una nueva unidad
@PostMapping
public Unidad createUnidad(@RequestBody Unidad unidad) {
    return unidadRepository.save(unidad);
}

// Buscar una unidad por ID
@GetMapping("/{id}")
public Unidad getUnidadById(@PathVariable("id") UUID id) {
    return unidadRepository.findById(id).orElse(null);
}


}