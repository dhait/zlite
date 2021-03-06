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
parser grammar ZLiteParser;
options {tokenVocab = ZLiteLexer; }

// A zlite document consists of a series of blocks
root : block* EOF;

// A block can be regular text, a directive, a section header, or z notation
block  : text
       | zblock
       | zsection
       | directive
        ;

// In text mode (the default mode), and text is recognized.
text       : ( anyText | paragraph) +
           ;

anyText    : ANY_TEXT+ ;
paragraph  : PARAGRAPH ;

// A zblock starts with a zedBlock.  This puts the lexer into Z mode
zblock     : zedBlock  | axiomBlock | genBlock | schemaBlock ;

// Z mode rules
zedBlock   : BEGIN_ZED z_expression* END_ZED;
axiomBlock : BEGIN_AXIOM z_expression* END_AXIOM;
genBlock   : BEGIN_GEN z_expression*  END_GEN;
schemaBlock: BEGIN_SCHEMA z_expression* END_SCHEMA;

z_expression : z_expression Z_CARET z_expression
             | z_expression Z_UNDERSCORE z_expression
             | Z_LBRACE z_expression* Z_RBRACE
             | z_hardspace
             | Z_COMMAND
             | Z_ALPHANUM
             | Z_SYMBOLS
             ;

z_hardspace  : Z_INTERWORD_SPACE
             | Z_THIN_SPACE
             | Z_MEDIUM_SPACE
             | Z_THICK_SPACE
             | Z_NEWLINE
             | Z_TABSTOP
             | Z_ALSO
             | Z_NEWPAGE
             ;

// A sectionHeader begins with zSectionStart.  This puts the lexer into Section mode.
zsection   : BEGIN_SECTION S_SECTION S_NAME (S_PARENTS s_parents)?  END_SECTION;
s_parents    : S_NAME ( S_COMMA S_NAME)* ;

// a directive begins with %%.  This puts the lexer into Directive mode.
directive  : zinclude
           | zchar
           | zword
           ;

zinclude   : ZINCLUDE D_RESOURCE D_NL;

zchar      : (ZCHAR | ZINCHAR | ZPRECHAR | ZPOSTCHAR) D_COMMAND D_UNICODE D_NL
           ;

zword       : ZWORD D_COMMAND d_expression+ D_NL ;

d_expression : d_expression D_CARET d_expression
             | d_expression D_UNDERSCORE d_expression
             | D_LBRACE z_expression* D_RBRACE
             | d_hardspace
             | D_COMMAND
             | D_ALPHANUM

             ;

d_hardspace  : D_INTERWORD_SPACE
             | D_THIN_SPACE
             | D_MEDIUM_SPACE
             | D_THICK_SPACE
             | D_NEWLINE
             | D_TABSTOP
             | D_ALSO
             | D_NEWPAGE
             ;

