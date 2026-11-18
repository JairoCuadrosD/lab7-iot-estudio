package pe.edu.pucp.lab7.avance;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * name = "curso-service" es el spring.application.name del otro servicio.
 * No hay "url = ..." : la dirección real la resuelve Eureka.
 *
 * Flujo cuando se llama obtenerCurso("1TEL05"):
 *   1. LoadBalancer pregunta a Eureka por las instancias de CURSO-SERVICE
 *   2. Eureka responde p.ej. 192.168.1.10:8001
 *   3. Feign hace GET http://192.168.1.10:8001/curso/1TEL05 y convierte el JSON a CursoDto
 */
@FeignClient(name = "curso-service")
public interface CursoClient {

    @GetMapping("/curso/{codigo}")
    CursoDto obtenerCurso(@PathVariable("codigo") String codigo);
}
