package pe.edu.pucp.lab7.curso;

/**
 * Un record de Java genera constructor, getters y equals/hashCode.
 * Jackson lo serializa como: { "codigo": "...", "nombre": "...", "creditos": 4 }
 */
public record Curso(String codigo, String nombre, int creditos) {
}
