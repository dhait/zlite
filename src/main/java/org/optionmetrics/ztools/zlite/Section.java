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

import org.optionmetrics.ztools.zlite.impl.Directive;

import java.util.*;

public class Section {

    private String name;
    private List<String> parents = new ArrayList<>();
    private List<Paragraph> paragraphs = new ArrayList<>();

    public Section() {

    }
    public Section(String name, List<String> parents) {
        this.name = name;
        this.parents = parents;
    }
    public enum Mark {
        NONE,
        TEMP,
        PERM
    }
    Mark mark = Mark.NONE;

    public String getName() {
        return name;
    }

    public List<String> getParents() {
        return parents;
    }

    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    // This function is used to sort the sections
    void visit(Set<Section> sections, List<Section> sorted) throws CircularDependencyException {
        if (mark == Mark.PERM)
            return;
        if (mark == Mark.TEMP) {
            throw new CircularDependencyException("Circular dependency present");
        }
        mark = Mark.TEMP;
        List<String> parents = getParents();
        for (String p : parents) {
            Optional<Section> ps = sections.stream()
                    .filter(s->s.getName().equals(p)).findFirst();
            if (ps.isPresent())
                ps.get().visit(sections, sorted);
        }
        mark = Mark.PERM;
        sorted.add(this);
    }

    // Get all the definitions in this section
    Map<String, Directive> collectDefinitions() {

        Map<String, Directive> definitions = new HashMap<>();
        for (Paragraph p : paragraphs) {
            if (p.type()== Paragraph.Type.DIRECTIVE) {
                Directive d = (Directive) p;
                // need type also
                definitions.put(d.getName(), d);
            }
        }
        return definitions;
    }
}
