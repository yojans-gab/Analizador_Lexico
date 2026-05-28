package minilang;

import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {

    public static class Entrada {
        public final Token  token;
        public final String tipo;

        public Entrada(Token token, String tipo) {
            this.token = token;
            this.tipo  = tipo;
        }
    }

    private static TablaSimbolos instancia;
    private final Map<String, Entrada> tabla = new HashMap<>();

    private TablaSimbolos() {}

    public static TablaSimbolos getInstance() {
        if (instancia == null) instancia = new TablaSimbolos();
        return instancia;
    }

    // ── Con tipo explícito (llamado desde parser.cup) ─────────────────────
    public void agregar(String nombre, Token token, String tipo) {
        tabla.put(nombre, new Entrada(token, tipo));
    }

    // ── Sin tipo (llamado desde lexer — solo registra el identificador) ───
    public void agregar(String nombre, Token token) {
        tabla.putIfAbsent(nombre, new Entrada(token, "desconocido"));
    }

    public boolean existe(String nombre) {
        return tabla.containsKey(nombre);
    }

    // ── Retorna el tipo declarado de una variable ─────────────────────────
    public String getTipo(String nombre) {
        Entrada e = tabla.get(nombre);
        return e != null ? e.tipo : null;
    }

    public Token getToken(String nombre) {
        Entrada e = tabla.get(nombre);
        return e != null ? e.token : null;
    }

    public int  tamanio() { return tabla.size(); }
    public void limpiar() { tabla.clear(); }

    public Map<String, Token> getTabla() {
        Map<String, Token> simple = new HashMap<>();
        tabla.forEach((k, v) -> simple.put(k, v.token));
        return simple;
    }

    public void imprimir() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println(" TABLA DE SIMBOLOS");
        System.out.println("=".repeat(70));
        if (tabla.isEmpty()) {
            System.out.println("  (ningún identificador encontrado)");
        } else {
            System.out.printf("%-20s %-12s %-8s %-8s%n",
                    "IDENTIFICADOR", "TIPO", "LINEA", "COLUMNA");
            System.out.println("-".repeat(52));
            tabla.forEach((nombre, entrada) -> {
                if (!entrada.tipo.equals("desconocido") &&
                        !entrada.tipo.equals("programa")) {   // ← filtrar nombre del programa
                    System.out.printf("%-20s %-12s %-8d %-8d%n",
                            nombre,
                            entrada.tipo,
                            entrada.token.getLinea(),
                            entrada.token.getColumna());
                }
            });
        }
        System.out.println("=".repeat(70));
    }
}