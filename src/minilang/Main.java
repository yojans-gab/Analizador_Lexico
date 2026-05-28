package minilang;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        String rutaArchivo;

        if (args.length >= 1) {
            rutaArchivo = args[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("=".repeat(70));
            System.out.println("  COMPILADOR TURBO X");
            System.out.println("=".repeat(70));
            System.out.print("\n  Ingresa la ruta del archivo a analizar: ");
            rutaArchivo = scanner.nextLine().trim();
            scanner.close();
        }

        // ── Validar archivo ───────────────────────────────────────────────────
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
        System.out.println("  COMPILADOR TURBO X");
        System.out.println("  Archivo: " + archivo.getAbsolutePath());
        System.out.println("=".repeat(70));

        // ── Preparar tabla de símbolos ────────────────────────────────────────
        TablaSimbolos tabla = TablaSimbolos.getInstance();
        tabla.limpiar();

        // Declarar fuera del try para acceder después
        Lexer   lexer           = null;
        Parser  parser          = null;
        boolean exitoSintactico = false;

        // ── Fase 1 + 2: Léxico y Sintáctico ──────────────────────────────────
        try (Reader reader = new FileReader(archivo)) {

            lexer  = new Lexer(reader);
            parser = new Parser(lexer);
            lexer.setParser(parser);

            System.out.println("\n  Ejecutando análisis léxico y sintáctico...");
            parser.parse();
            exitoSintactico = true;

        } catch (FileNotFoundException e) {
            System.err.println("[ERROR] Archivo no encontrado: " + rutaArchivo);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("[ERROR] Error de lectura: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            // Error sintáctico fatal — el parser ya lo acumuló en su lista
            // solo mostramos si hay algo extra no capturado
            if (e.getMessage() != null && !e.getMessage().contains("sintáctico")) {
                System.err.println("  ✗ " + e.getMessage());
            }
        }

        // ── Verificar que el Lexer se creó correctamente ──────────────────────
        if (lexer == null) return;

        List<Token>  tokens        = lexer.tokensReconocidos;
        List<Token>  erroresLex    = lexer.erroresLexicos;
        List<String> erroresSint   = (parser != null)
                ? parser.getErroresSintacticos()
                : new java.util.ArrayList<>();

        // ── Resultado general del análisis ────────────────────────────────────
        System.out.println(exitoSintactico && erroresSint.isEmpty()
                ? "\n  ✓ Análisis completado sin errores."
                : "\n  ✗ Análisis completado con errores.");

        // ── Tokens reconocidos ────────────────────────────────────────────────
        System.out.println("\n" + "=".repeat(70));
        System.out.println(" TOKENS RECONOCIDOS");
        System.out.println("=".repeat(70));
        System.out.println(Token.cabecera());
        System.out.println("-".repeat(70));
        tokens.stream()
                .filter(t -> !t.esEOF())
                .forEach(System.out::println);

        // ── Errores léxicos ───────────────────────────────────────────────────
        if (!erroresLex.isEmpty()) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println(" ERRORES LÉXICOS DETECTADOS");
            System.out.println("=".repeat(70));
            System.out.printf("%-6s %-8s %s%n", "Línea", "Columna", "Descripción");
            System.out.println("-".repeat(70));
            for (Token err : erroresLex) {
                System.out.printf("%-6d %-8d Carácter no reconocido: '%s'%n",
                        err.getLinea(), err.getColumna(), err.getValor());
            }
            System.out.println("=".repeat(70));
        }

        // ── Errores sintácticos ───────────────────────────────────────────────
        if (!erroresSint.isEmpty()) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println(" ERRORES SINTÁCTICOS DETECTADOS");
            System.out.println("=".repeat(70));
            for (String err : erroresSint) {
                System.out.println("  ✗ " + err);
            }
            System.out.println("=".repeat(70));
        }

        // ── Tabla de símbolos ─────────────────────────────────────────────────
        tabla.imprimir();

        // ── Resumen final ─────────────────────────────────────────────────────
        long validos = tokens.stream()
                .filter(t -> !t.esEOF() && !t.esError())
                .count();

        System.out.println("\n" + "=".repeat(70));
        System.out.println(" RESUMEN DEL ANÁLISIS");
        System.out.println("=".repeat(70));
        System.out.printf("  Archivo analizado       : %s%n", archivo.getName());
        System.out.printf("  Tokens reconocidos      : %d%n", validos);
        System.out.printf("  Errores léxicos         : %d%n", erroresLex.size());
        System.out.printf("  Errores sintácticos     : %d%n", erroresSint.size());
        System.out.printf("  Identificadores únicos  : %d%n", tabla.tamanio());
        System.out.println((exitoSintactico && erroresSint.isEmpty())
                ? "  Estado                  : ✓ Análisis exitoso"
                : "  Estado                  : ✗ Se encontraron errores");
        System.out.println("=".repeat(70));
    }
}