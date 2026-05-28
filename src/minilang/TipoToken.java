package minilang;

public enum TipoToken {
    // Palabras reservadas
    PR_PROGRAMA, PR_NUM, PR_DECIMAL, PR_TEXTO,
    PR_LETRA, PR_LOGICO, PR_CIERTO, PR_FALSO,
    PR_MOSTRAR, PR_SI, PR_SINO, PR_MIENTRAS,

    // Delimitadores de bloque
    ABRE_BLOQUE,
    CIERRA_BLOQUE,

    // Operadores relacionales
    IGUAL_IGUAL, DIFERENTE, MAYOR_IGUAL, MENOR_IGUAL, MAYOR, MENOR,

    // Asignación
    ASIGNACION,

    // Operadores aritméticos
    SUMA, RESTA, MULT, DIV, POTENCIA,

    // Delimitadores auxiliares
    PAR_IZQ, PAR_DER, PUNTO_COMA, COMA,

    // Literales
    ENTERO_LITERAL, REAL_LITERAL, STRING_LITERAL, CHAR_LITERAL,

    // Identificador
    ID,

    // Especiales
    ERROR, EOF
}