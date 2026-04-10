package minilang;

import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {

    private static TablaSimbolos instancia;
    private final Map<String, Token> tabla = new HashMap<>();

    private TablaSimbolos() {}

    public static TablaSimbolos getInstance() {
        if (instancia == null) instancia = new TablaSimbolos();
        return instancia;
    }

    public void agregar(String nombre, Token token) {
        tabla.putIfAbsent(nombre, token);
    }

    public boolean existe(String nombre) {
        return tabla.containsKey(nombre);
    }

    public int  tamanio() { return tabla.size(); }
    public void limpiar() { tabla.clear(); }

    public void imprimir() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println(" TABLA DE SÍMBOLOS");
        System.out.println("=".repeat(70));
        if (tabla.isEmpty()) {
            System.out.println("  (ningún identificador encontrado)");
        } else {
            System.out.printf("%-20s %-8s %-8s%n",
                    "IDENTIFICADOR", "LÍNEA", "COLUMNA");
            System.out.println("-".repeat(40));
            tabla.forEach((nombre, tok) ->
                    System.out.printf("%-20s %-8d %-8d%n",
                            nombre, tok.getLinea(), tok.getColumna()));
        }
        System.out.println("=".repeat(70));
    }

    // Agrega este método a TablaSimbolos.java
    public Map<String, Token> getTabla() {
        return tabla;
    }
}