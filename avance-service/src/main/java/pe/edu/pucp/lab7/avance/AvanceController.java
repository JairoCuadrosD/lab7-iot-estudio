package pe.edu.pucp.lab7.avance;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AvanceController {

    private final CursoClient cursoClient;

    // Inyección por constructor: Spring pasa el proxy de Feign generado automáticamente
    public AvanceController(CursoClient cursoClient) {
        this.cursoClient = cursoClient;
    }

    // GET http://localhost:8002/avance/1TEL05/7.5
    @GetMapping("/avance/{codigo}/{horas}")
    public AvanceResponse evaluar(@PathVariable String codigo, @PathVariable double horas) {
        // Llamada servicio -> servicio (vía Eureka)
        CursoDto curso = cursoClient.obtenerCurso(codigo);

        int horasRecomendadas = curso.creditos() * 3;
        String estado = horas >= horasRecomendadas ? "AL DÍA" : "PENDIENTE";

        return new AvanceResponse(curso.nombre(), horas, horasRecomendadas, estado);
    }
}
