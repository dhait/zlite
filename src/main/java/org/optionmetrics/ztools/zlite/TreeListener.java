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

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

public class TreeListener extends ZLiteParserBaseListener {

    private String filename;
    private List<Section> sections = new ArrayList<>();
    private Section currentSection = new Section();
    private String sectionName;
    private List<String> parents = new ArrayList<>();

    public TreeListener(String filename) {
        this.filename = filename;
    }

    @Override
    public void exitZinclude(ZLiteParser.ZincludeContext ctx) {

        String resource = ctx.D_RESOURCE().getText();
        System.out.println(resource);
    }

    @Override
    public void exitS_parents(ZLiteParser.S_parentsContext ctx) {
        parents = new ArrayList<>();
        for (TerminalNode node : ctx.S_NAME()) {
            parents.add(node.getText());
        }
    }

    @Override
    public void exitZsection(ZLiteParser.ZsectionContext ctx) {
        sectionName = ctx.S_NAME().getText();
        currentSection = new Section(sectionName, parents);
        sections.add(currentSection);
        System.out.println("Section " + sectionName);
    }

    @Override
    public void exitText(ZLiteParser.TextContext ctx) {
        System.out.println("Text");
    }

    @Override
    public void exitZblock(ZLiteParser.ZblockContext ctx) {
        System.out.println("Z");
    }

    @Override
    public void exitSchemaBlock(ZLiteParser.SchemaBlockContext ctx) {
        System.out.println("Schema");
    }

}
