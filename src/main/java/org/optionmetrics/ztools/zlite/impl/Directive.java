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

package org.optionmetrics.ztools.zlite.impl;

import org.optionmetrics.ztools.zlite.Paragraph;

import static org.optionmetrics.ztools.zlite.impl.Directive.DirectiveType.*;

public class Directive extends Paragraph {

    enum DirectiveType {
        CHAR,
        PRECHAR,
        INCHAR,
        POSTCHAR,
        WORD,
        PREWORD,
        INWORD,
        POSTWORD
    }

    private DirectiveType type;
    private String name;
    private String value;

    public Directive(String typeText, String name, String value) {
        DirectiveType type;
        switch (typeText) {
            case "%%Zchar" : type = CHAR;
            case "%%Zprechar" : type = PRECHAR;
            case "%%Zinchar" : type = INCHAR;
            case "%%Zpostchar" : type = POSTCHAR;
            case "%%Zword" : type = WORD;
            case "%%Zpreword" : type = PREWORD;
            case "%%Zinword" : type = INWORD;
            case "%%Zpostword" : type = POSTWORD;
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    protected Type type() {
        return Type.DIRECTIVE;
    }
}
