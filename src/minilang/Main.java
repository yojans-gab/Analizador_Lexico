package minilang;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interfaz de consola para el Analizador Léxico de MiniLang.
 * Solicita la ruta del archivo interactivamente si no se pasa como argumento.
 */
public class Main {

    public static void main(String[] args) {

        String rutaArchivo;

        // Si se pasa argumento por línea de comandos, úsalo
        // Si no, pedirlo interactivamente
        if (args.length >= 1) {
            rutaArchivo = args[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("=".repeat(70));
            System.out.println("  ANALIZADOR LÉXICO - MiniLang");
            System.out.println("=".repeat(70));
            System.out.print("\n  Ingresa la ruta del archivo a analizar: ");
            rutaArchivo = scanner.nextLine().trim();
            scanner.close();
        }

        // Validar archivo
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            System.err.println("[ERROR] El archivo no existe: " + rutaArchivo);
            System.exit(1);
        }
        if (!archivo.canRead()) {
            System.err.println("[ERROR] No se puede leer el archivo: " + rutaArchivo);
            System.exit(1);
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("  ANALIZADOR LÉXICO - MiniLang");
        System.out.println("  Archivo: " + archivo.getAbsolutePath());
        System.out.println("=".repeat(70));

        // Listas para acumular resultados
        List<Token> tokens  = new ArrayList<>();
        List<Token> errores = new ArrayList<>();

        // Tabla de símbolos limpia
        TablaSimbolos tabla = TablaSimbolos.getInstance();
        tabla.limpiar();

        // ── Análisis léxico ──────────────────────────────────────────────────
        try (Reader reader = new FileReader(archivo)) {

            Lexer lexer = new Lexer(reader);

            System.out.println("\n TOKENS RECONOCIDOS");
            System.out.println("=".repeat(70));
            System.out.println(Token.cabecera());
            System.out.println("-".repeat(70));

            Token t;
            while (true) {
                t = lexer.yylex();
                if (t == null) break;

                System.out.println(t);

                if (t.esError()) {
                    errores.add(t);
                } else if (t.esEOF()) {
                    tokens.add(t);
                    break;
                } else {
                    tokens.add(t);
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("[ERROR] Archivo no encontrado: " + rutaArchivo);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("[ERROR] Error de lectura: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("[ERROR] Error inesperado: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // ── Errores léxicos ──────────────────────────────────────────────────
        if (!errores.isEmpty()) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println(" ERRORES LÉXICOS DETECTADOS");
            System.out.println("=".repeat(70));
            System.out.printf("%-6s %-8s %s%n", "Línea", "Columna", "Descripción");
            System.out.println("-".repeat(70));
            for (Token err : errores) {
                System.out.printf("%-6d %-8d Carácter no reconocido: '%s'%n",
                        err.getLinea(), err.getColumna(), err.getValor());
            }
            System.out.println("=".repeat(70));
        }

        // ── Tabla de símbolos ────────────────────────────────────────────────
        tabla.imprimir();

        // ── Resumen ──────────────────────────────────────────────────────────
        long tokensValidos = tokens.stream()
                .filter(tk -> !tk.esEOF() && !tk.esError())
                .count();

        System.out.println("\n RESUMEN DEL ANÁLISIS");
        System.out.println("=".repeat(70));
        System.out.printf("  Archivo analizado       : %s%n", archivo.getName());
        System.out.printf("  Tokens reconocidos      : %d%n", tokensValidos);
        System.out.printf("  Errores léxicos         : %d%n", errores.size());
        System.out.printf("  Identificadores únicos  : %d%n", tabla.tamanio());
        System.out.println(errores.isEmpty()
                ? "  Estado                  : ✓ Análisis léxico exitoso"
                : "  Estado                  : ✗ Se encontraron errores léxicos");
        System.out.println("=".repeat(70));
    }
}