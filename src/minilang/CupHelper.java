package minilang;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase puente entre action code y parser code de JCUP.
 * Almacena errores semanticos de forma estatica.
 */
public class CupHelper {

    private static final List<String> erroresSemanticos = new ArrayList<>();

    public static void agregarError(String msg) {
        erroresSemanticos.add(msg);
    }

    public static List<String> getErroresSemanticos() {
        return erroresSemanticos;
    }

    public static void limpiar() {
        erroresSemanticos.clear();
    }
}