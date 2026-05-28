package minilang;

public class Token extends java_cup.runtime.Symbol {

    private final TipoToken tipo;
    private final String    lexema;
    private final int       linea;
    private final int       columna;

    public Token(TipoToken tipo, String lexema, int linea, int columna) {
        super(tipoToSym(tipo), lexema);  // ← usa sym en lugar de ordinal
        this.tipo    = tipo;
        this.lexema  = lexema;
        this.linea   = linea;
        this.columna = columna;
    }

    private static int tipoToSym(TipoToken tipo) {
        switch (tipo) {
            case PR_PROGRAMA:    return minilang.sym.PR_PROGRAMA;
            case PR_NUM:         return minilang.sym.PR_NUM;
            case PR_DECIMAL:     return minilang.sym.PR_DECIMAL;
            case PR_TEXTO:       return minilang.sym.PR_TEXTO;
            case PR_LETRA:       return minilang.sym.PR_LETRA;
            case PR_LOGICO:      return minilang.sym.PR_LOGICO;
            case PR_CIERTO:      return minilang.sym.PR_CIERTO;
            case PR_FALSO:       return minilang.sym.PR_FALSO;
            case PR_MOSTRAR:     return minilang.sym.PR_MOSTRAR;
            case PR_SI:          return minilang.sym.PR_SI;
            case PR_SINO:        return minilang.sym.PR_SINO;
            case PR_MIENTRAS:    return minilang.sym.PR_MIENTRAS;
            case ABRE_BLOQUE:    return minilang.sym.ABRE_BLOQUE;
            case CIERRA_BLOQUE:  return minilang.sym.CIERRA_BLOQUE;
            case IGUAL_IGUAL:    return minilang.sym.IGUAL_IGUAL;
            case DIFERENTE:      return minilang.sym.DIFERENTE;
            case MAYOR_IGUAL:    return minilang.sym.MAYOR_IGUAL;
            case MENOR_IGUAL:    return minilang.sym.MENOR_IGUAL;
            case MAYOR:          return minilang.sym.MAYOR;
            case MENOR:          return minilang.sym.MENOR;
            case ASIGNACION:     return minilang.sym.ASIGNACION;
            case SUMA:           return minilang.sym.SUMA;
            case RESTA:          return minilang.sym.RESTA;
            case MULT:           return minilang.sym.MULT;
            case DIV:            return minilang.sym.DIV;
            case POTENCIA:       return minilang.sym.POTENCIA;
            case PAR_IZQ:        return minilang.sym.PAR_IZQ;
            case PAR_DER:        return minilang.sym.PAR_DER;
            case PUNTO_COMA:     return minilang.sym.PUNTO_COMA;
            case COMA:           return minilang.sym.COMA;
            case ENTERO_LITERAL: return minilang.sym.ENTERO_LITERAL;
            case REAL_LITERAL:   return minilang.sym.REAL_LITERAL;
            case STRING_LITERAL: return minilang.sym.STRING_LITERAL;
            case CHAR_LITERAL:   return minilang.sym.CHAR_LITERAL;
            case ID:             return minilang.sym.ID;
            default:             return minilang.sym.EOF;
        }
    }

    public TipoToken getTipo()    { return tipo; }
    public String    getLexema()  { return lexema; }
    public String    getValor()   { return lexema; }
    public int       getLinea()   { return linea; }
    public int       getColumna() { return columna; }

    public boolean esError() { return tipo == TipoToken.ERROR; }
    public boolean esEOF()   { return tipo == TipoToken.EOF;   }

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