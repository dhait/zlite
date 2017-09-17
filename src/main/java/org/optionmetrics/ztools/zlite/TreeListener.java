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

package org.optionmetrics.ztools.zlite;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.optionmetrics.ztools.zlite.impl.Directive;
import org.optionmetrics.ztools.zlite.impl.SectionHeader;
import org.optionmetrics.ztools.zlite.impl.TextParagraph;
import org.optionmetrics.ztools.zlite.impl.ZParagraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class TreeListener {

    private List<Paragraph> paragraphs = new ArrayList<>();
    private List<String> parents = new ArrayList<>();
    private String name = "";
    private final String file;

    public TreeListener(String file) {
        this.file = file;
    }
    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void exitTextBlockType(ZLiteParser.TextBlockTypeContext ctx) {
        // informal
        paragraphs.add(new TextParagraph(ctx));
    }

    public void exitZBlockType(ZLiteParser.ZBlockTypeContext ctx) {
        paragraphs.add(new ZParagraph(ctx));
    }

    public void exitDirectiveBlockType(ZLiteParser.DirectiveBlockTypeContext ctx) {
        // process directives
        ZLiteParser.DirectiveContext dctx = ctx.directive();
        if (dctx.zchar() != null) {
            String name = dctx.zchar().D_COMMAND().getText();
            String value = dctx.zchar().D_UNICODE().getText();
            String command = dctx.zchar().ZCHAR().getText();
            paragraphs.add(new Directive(command, name, value));
        }
        else if (dctx.zword() != null) {
            String name = dctx.zword().D_COMMAND().getText();
            //String value = dctx.zword().d_expression().getText();
            String command = dctx.zword().ZWORD().getText();
            //paragraphs.add(new Directive(command, name, value));
        }
    }

    public void exitS_parents(ZLiteParser.S_parentsContext ctx) {
        parents.clear();
        for (TerminalNode t : ctx.S_NAME()) {
            parents.add(t.getText());
        }
    }

    public void exitZsection(ZLiteParser.ZsectionContext ctx) {
        name = ctx.S_NAME().getText();
    }

    public void exitZSectionBlockType(ZLiteParser.ZSectionBlockTypeContext ctx) {
        // create new section with correct name
        paragraphs.add(new SectionHeader(name, parents));
    }

}
