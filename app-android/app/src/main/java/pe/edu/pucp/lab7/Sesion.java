package pe.edu.pucp.lab7;

import com.google.firebase.firestore.Exclude;

/**
 * Documento en Firestore: usuarios/{uid}/sesiones/{idSesion}
 * Campos: curso, fecha, horas, tipo, evidenciaUrl
 *
 * Firestore necesita constructor vacío + getters/setters públicos
 * para convertir el documento a objeto (toObject) y viceversa (set).
 */
public class Sesion {

    private String id;           // no se guarda como campo, es el id del documento
    private String curso;        // ej. "1TEL05"
    private String fecha;        // "yyyy-MM-dd" (ordena bien como texto)
    private double horas;
    private String tipo;
    private String evidenciaUrl; // URL de descarga de Storage (puede ser null)

    public Sesion() { }

    @Exclude
    public String getId() { return id; }
    @Exclude
    public void setId(String id) { this.id = id; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public double getHoras() { return horas; }
    public void setHoras(double horas) { this.horas = horas; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEvidenciaUrl() { return evidenciaUrl; }
    public void setEvidenciaUrl(String evidenciaUrl) { this.evidenciaUrl = evidenciaUrl; }
}
