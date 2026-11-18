package pe.edu.pucp.lab7.avance;

/**
 * JSON de salida:
 * { "curso": "...", "horasRegistradas": 7.5, "horasRecomendadas": 12, "estado": "PENDIENTE" }
 */
public record AvanceResponse(String curso,
                             double horasRegistradas,
                             int horasRecomendadas,
                             String estado) {
}
