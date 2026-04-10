package minilang;

%%

%class Lexer
%public
%unicode
%line
%column
%type Token

%{
    private TablaSimbolos tabla = TablaSimbolos.getInstance();

    private Token token(TipoToken tipo) {
        return new Token(tipo, yytext(), yyline + 1, yycolumn + 1);
    }
%}

/* ── Macros ─────────────────────────────────────────── */
LETRA   = [a-zA-Z]
DIGITO  = [0-9]
ID      = {LETRA}({LETRA}|{DIGITO}|_)*
ENTERO  = {DIGITO}+
REAL    = {DIGITO}+"."{DIGITO}+
CHAR    = \'([^\'\\]|\\.)+\'
STRING  = \"([^\"\\]|\\.)*\"
ESPACIO = [ \t\r]+
SALTO   = \n

%%

/* ── Espacios ────────────────────────────────────────── */
{ESPACIO}   { /* ignorar */ }
{SALTO}     { /* ignorar */ }

/* ── Palabras reservadas ─────────────────────────────── */
"programa"  { return token(TipoToken.PR_PROGRAMA); }
"num"       { return token(TipoToken.PR_NUM); }
"decimal"   { return token(TipoToken.PR_DECIMAL); }
"texto"     { return token(TipoToken.PR_TEXTO); }
"letra"     { return token(TipoToken.PR_LETRA); }
"logico"    { return token(TipoToken.PR_LOGICO); }
"cierto"    { return token(TipoToken.PR_CIERTO); }
"falso"     { return token(TipoToken.PR_FALSO); }
"mostrar"   { return token(TipoToken.PR_MOSTRAR); }
"sino"      { return token(TipoToken.PR_SINO); }   // sino ANTES que si
"si"        { return token(TipoToken.PR_SI); }
"mientras"  { return token(TipoToken.PR_MIENTRAS); }

/* ── Delimitadores de bloque ─────────────────────────── */
"<<"        { return token(TipoToken.INICIO_BLOQUE_PRINCIPAL); }
">>"        { return token(TipoToken.FIN_BLOQUE_PRINCIPAL); }
"??"        { return token(TipoToken.INICIO_BLOQUE_SI); }
"!!"        { return token(TipoToken.FIN_BLOQUE_SI); }
"::"        { return token(TipoToken.BLOQUE_MIENTRAS); }

/* ── Operadores relacionales (dobles primero) ────────── */
"=="        { return token(TipoToken.IGUAL_IGUAL); }
"!="        { return token(TipoToken.DIFERENTE); }
">="        { return token(TipoToken.MAYOR_IGUAL); }
"<="        { return token(TipoToken.MENOR_IGUAL); }
">"         { return token(TipoToken.MAYOR); }
"<"         { return token(TipoToken.MENOR); }

/* ── Asignación ──────────────────────────────────────── */
"="         { return token(TipoToken.ASIGNACION); }

/* ── Operadores aritméticos ──────────────────────────── */
"+"         { return token(TipoToken.SUMA); }
"-"         { return token(TipoToken.RESTA); }
"*"         { return token(TipoToken.MULT); }
"/"         { return token(TipoToken.DIV); }
"^"         { return token(TipoToken.POTENCIA); }

/* ── Delimitadores auxiliares ────────────────────────── */
"("         { return token(TipoToken.PAR_IZQ); }
")"         { return token(TipoToken.PAR_DER); }
";"         { return token(TipoToken.PUNTO_COMA); }
","         { return token(TipoToken.COMA); }

/* ── Literales (REAL antes que ENTERO) ───────────────── */
{REAL}      { return token(TipoToken.REAL_LITERAL); }
{ENTERO}    { return token(TipoToken.ENTERO_LITERAL); }
{CHAR}      { return token(TipoToken.CHAR_LITERAL); }
{STRING}    { return token(TipoToken.STRING_LITERAL); }

/* ── Identificadores ─────────────────────────────────── */
{ID}        {
                Token t = token(TipoToken.ID);
                tabla.agregar(yytext(), t);
                return t;
            }

/* ── Fin de archivo ──────────────────────────────────── */
<<EOF>>     { return new Token(TipoToken.EOF, "<EOF>", yyline + 1, yycolumn + 1); }

/* ── Error léxico ────────────────────────────────────── */
.           { return new Token(TipoToken.ERROR, yytext(), yyline + 1, yycolumn + 1); }