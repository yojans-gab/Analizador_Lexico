package minilang;

public class Token {
    private final TipoToken tipo;
    private final String    lexema;
    private final int       linea;
    private final int       columna;

    public Token(TipoToken tipo, String lexema, int linea, int columna) {
        this.tipo    = tipo;
        this.lexema  = lexema;
        this.linea   = linea;
        this.columna = columna;
    }

    public TipoToken getTipo()    { return tipo; }
    public String    getLexema()  { return lexema; }
    public String    getValor()   { return lexema; } // alias para compatibilidad con Main
    public int       getLinea()   { return linea; }
    public int       getColumna() { return columna; }

    public boolean esError() { return tipo == TipoToken.ERROR; }
    public boolean esEOF()   { return tipo == TipoToken.EOF; }

    public static String cabecera() {
        return String.format("%-25s %-20s %-8s %-8s",
                "TOKEN", "LEXEMA", "LÍNEA", "COLUMNA");
    }

    @Override
    public String toString() {
        return String.format("%-25s %-20s %-8d %-8d",
                tipo, lexema, linea, columna);
    }
}