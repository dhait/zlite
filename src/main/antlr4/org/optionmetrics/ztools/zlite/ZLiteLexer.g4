/*
 * Copyright (c) 2017, David J Hait.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
lexer grammar ZLiteLexer;


// Default mode -- this is used for text tokens
BEGIN_ZED    : '\\begin{zed}' -> mode(Z) ;
BEGIN_SCHEMA : '\\begin{schema}' -> mode(Z) ;
BEGIN_AXIOM  : '\\begin{axdef}' -> mode(Z) ;
BEGIN_GEN    : '\\begin{gendef}' -> mode(Z) ;

BEGIN_SECTION: '\\begin{zsection}' -> mode(Section);

ZCHAR      : '%%Zchar'      -> mode(Directive);
ZINCHAR    : '%%Zinchar'    -> mode(Directive);
ZPRECHAR   : '%%Zprechar'   -> mode(Directive);
ZPOSTCHAR  : '%%Zpostchar'  -> mode(Directive);
ZWORD      : '%%Zword'      -> mode(Directive);
ZINWORD    : '%%Zinword'    -> mode(Directive);
ZPREWORD   : '%%Zpreword'   -> mode(Directive);
ZPOSTWORD  : '%%Zpostword'  -> mode(Directive);

COMMENT_START : '%' ~'%' -> channel(HIDDEN), mode(Comment);

PARAGRAPH  : T_NL T_NL+;
ANY_TEXT   : T_COMMAND | OTHER | BACKSLASH | PERCENT | T_NL;

fragment T_COMMAND  :   '\\' ~[ \t\r\n_^{}\\]+ ;
fragment T_NL : '\r'? '\n';
fragment BACKSLASH  : '\\';
fragment PERCENT    : '%';
fragment OTHER      : ~[\\%\r\n]+;

// Comments
mode Comment;
COMMENT_END  : [\r\n]   -> channel(HIDDEN), mode(DEFAULT_MODE);
COMMENT_TEXT : ~[\r\n]+? -> channel(HIDDEN);

// Directive mode
mode Directive;

D_NL   : [\r\n] -> mode(DEFAULT_MODE);
D_WS   : [\t ] -> skip;

D_COMMAND  :   '\\' ~[ \t\r\n_^{}\\]+ ;

D_UNICODE : 'U+' HEX HEX HEX HEX
        | 'U-' HEX HEX HEX HEX HEX HEX HEX HEX
        ;

D_LBRACE     : '{' ;
D_RBRACE     : '}' ;
D_CARET      : '^' ;
D_UNDERSCORE : '_' ;
D_ALPHANUM : (D_LETTER | D_DIGIT)+;

D_INTERWORD_SPACE : '~' | ('\\' ' ') ;
D_THIN_SPACE : '\\,' ;
D_MEDIUM_SPACE : '\\:' ;
D_THICK_SPACE : '\\;' ;
D_NEWLINE : '\\\\' ;
D_TABSTOP : '\\t' D_DIGIT;
D_ALSO    : '\\also' ;
D_NEWPAGE : '\\znewpage';

fragment HEX : D_DIGIT | HEXLETTER;
fragment HEXLETTER : [a-fA-F];

fragment D_DIGIT : [0-9];
fragment D_LETTER : [a-zA-Z];

// Section header mode
mode Section;

END_SECTION : '\\end{zsection}' -> mode(DEFAULT_MODE);

S_SECTION     : '\\SECTION' ;
S_PARENTS     : '\\parents' ;
S_NAME        : WORD STROKE? ;
WORD        : WORDPART+
            | [a-zA-Z] [0-9a-zA-Z]* WORDPART*
            ;

fragment WORDPART    : WORDGLUE [0-9a-zA-Z]* ;
fragment WORDGLUE    : '\\_';
fragment STROKE      : ('\'' | '?' | '!');

S_COMMA       : ',';
S_NL        : [\r\n] -> skip;
S_WS        : [ \t]  -> skip;

// Z
mode Z;

END_ZED     : '\\end{zed}' -> mode(DEFAULT_MODE);
END_SCHEMA  : '\\end{schema}' -> mode(DEFAULT_MODE);
END_AXIOM   : '\\end{axdef}' -> mode(DEFAULT_MODE);
END_GEN     : '\\end{gendef}' -> mode(DEFAULT_MODE);

Z_LBRACE     : '{' ;
Z_RBRACE     : '}' ;
Z_CARET      : '^' ;
Z_UNDERSCORE : '_' ;

Z_INTERWORD_SPACE : '~' | ('\\' ' ') ;
Z_THIN_SPACE : '\\,' ;
Z_MEDIUM_SPACE : '\\:' ;
Z_THICK_SPACE : '\\;' ;
Z_NEWLINE : '\\\\' ;
Z_TABSTOP : '\\t' Z_DIGIT;
Z_ALSO    : '\\also' ;
Z_NEWPAGE : '\\znewpage';

Z_COMMAND  :   '\\' ~[ \t\r\n_^{}\\]+ ;

Z_ALPHANUM : (Z_LETTER | Z_DIGIT)+;
Z_SYMBOLS : .+?;
Z_NL   : [\r\n] -> skip;
Z_WS : [ \t]  -> skip;

fragment Z_DIGIT : [0-9];
fragment Z_LETTER : [a-zA-Z];


