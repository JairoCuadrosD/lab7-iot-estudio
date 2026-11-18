package pe.edu.pucp.lab7.curso;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CursoController {

    // Catálogo fijo del enunciado, en memoria (no hay base de datos)
    private static final Map<String, Curso> CATALOGO = Map.of(
            "1TEL05", new Curso("1TEL05", "Servicios y Aplicaciones para IoT", 4),
            "1TEL04", new Curso("1TEL04", "Redes de Computadoras", 4),
            "1TEL03", new Curso("1TEL03", "Comunicaciones Digitales", 3),
            "1TEL02", new Curso("1TEL02", "Sistemas Operativos", 3)
    );

    // GET http://localhost:8001/curso/1TEL05
    @GetMapping("/curso/{codigo}")
    public ResponseEntity<Curso> obtener(@PathVariable String codigo) {
        Curso curso = CATALOGO.get(codigo.toUpperCase());
        // El enunciado dice que no se evaluará un código inexistente,
        // pero devolver 404 es lo correcto y no cuesta nada.
        return curso != null ? ResponseEntity.ok(curso) : ResponseEntity.notFound().build();
    }
}
