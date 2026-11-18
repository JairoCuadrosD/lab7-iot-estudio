package pe.edu.pucp.lab7.avance;

/**
 * Copia "local" del JSON que devuelve curso-service.
 * Cada microservicio tiene sus propias clases: no se comparte código entre ellos.
 */
public record CursoDto(String codigo, String nombre, int creditos) {
}
