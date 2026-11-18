package pe.edu.pucp.lab7;

/** Códigos válidos del catálogo (los mismos que tiene curso-service). */
public final class Catalogo {
    public static final String[] CODIGOS = {"1TEL05", "1TEL04", "1TEL03", "1TEL02"};
    public static final String[] TIPOS = {"Teoría", "Práctica", "Laboratorio", "Repaso"};

    private Catalogo() { }

    public static boolean esValido(String codigo) {
        for (String c : CODIGOS) {
            if (c.equals(codigo)) return true;
        }
        return false;
    }
}
