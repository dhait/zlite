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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.optionmetrics.ztools.zlite.impl.Directive;
import org.optionmetrics.ztools.zlite.impl.SectionHeader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class Processor {

    private SearchPath searchPath;
    private List<Section> sections;

    public Processor(SearchPath searchPath) {
        this.searchPath = searchPath;
    }

    public SearchPath getSearchPath() {
        return searchPath;
    }
    public List<Section> getSections() {
        return sections;
    }

    public void process2(String name) throws IOException {
        String fileName = name;
        fileName = fileName + ".zlt";
        InputStream inputStream = searchPath.find(fileName);
        CharStream stream = CharStreams.fromStream(inputStream);

        ZLiteLexer lexer = new ZLiteLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ZLiteParser parser = new ZLiteParser(tokens);
        parser.removeErrorListeners();
        ErrorListener errorListener = new ErrorListener(fileName);
        parser.addErrorListener(errorListener);

        ParserRuleContext tree = parser.root();

        TreeListener listener = new TreeListener(fileName, searchPath);

        if (errorListener.getErrorCount() > 0) {
            System.err.println(errorListener.getErrorCount() + " errors in file " + fileName);
        } else {
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener,tree);
        }
        //return listener.getParagraphs();
    }

    public String process(String name) throws IOException, CircularDependencyException {

        // parser and order the sections
        sortSections(name);
        // expand all the z directive definitions, toolkit by toolkit
        expandDefinitions();

        // section paragraphs contains informals
        StringBuilder sb = new StringBuilder();
        for (Section s : sections) {
            for (Paragraph p : s.getParagraphs()) {
                if (p.type() == Paragraph.Type.SECTION
                        || p.type() == Paragraph.Type.TEXTBLOCK
                        || p.type() == Paragraph.Type.ZBLOCK)
                {
                    sb.append(p).append("\n");
                }
            }
        }
        return sb.toString();
    }

    // load a list of paragraphs (including section headers) from a file
    private List<Paragraph> load(InputStream fileStream, String fileName) throws IOException {

        CharStream stream = CharStreams.fromStream(fileStream);
        ZLiteLexer lexer = new ZLiteLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ZLiteParser parser = new ZLiteParser(tokens);
        parser.removeErrorListeners();
        ErrorListener errorListener = new ErrorListener(fileName);
        parser.addErrorListener(errorListener);

        ParserRuleContext tree = parser.root();

        TreeListener listener = new TreeListener(fileName, searchPath);

        if (errorListener.getErrorCount() > 0) {
            System.err.println(errorListener.getErrorCount() + " errors in file " + fileName);
        } else {
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener,tree);
        }
        //return listener.getParagraphs();
        return null;
    }

    // for a collection of sections, return the set of parents of
    // those sections
    private Set<String> sectionsToParents(Collection<Section> sections) {
        Set<String> parents = new HashSet<>();
        for (Section s : sections) {
            parents.addAll(s.getParents());
        }
        return parents;
    }

    // Search for a file and read it into its paragraphs
    private List<Paragraph> filenameToParagraphs(String name) throws IOException {
        List<Paragraph> paragraphs;
        String fname = name;
        fname = fname + ".zlt";
        InputStream inputStream = searchPath.find(fname);
        if (inputStream != null) {
            paragraphs = load(inputStream, fname);
        }
        else {
            throw new IOException("File: " + fname + " not found!");
        }
        return paragraphs;
    }

    // Given a file name, read the paragraphs in that file and
    // add a section header if there is one needed at the beginning.
    private List<Paragraph> addHeader(String name) throws IOException {

        List<Paragraph> ps = filenameToParagraphs(name);

        // split this list of paragraphs into two parts --
        // the paragraphs before the first SectionBlock, and those after (inclusive)
        List<Paragraph> prefix = new ArrayList<>();
        List<Paragraph> suffix = new ArrayList<>(ps);
        while (!suffix.isEmpty() && !(suffix.get(0).type() == Paragraph.Type.SECTION)) {
            prefix.add(suffix.get(0));
            suffix.remove(0);
        }

        if (prefix.isEmpty()) {
            // the first paragraph was a section header, nothing before it
            if (!suffix.isEmpty()) {
                SectionHeader h = (SectionHeader) suffix.get(0);
                if (h.getName().equals(name)) {
                    // add an extra section header, representing this file (different name)
                    ps.add(0, new SectionHeader(name, null));
                }
            }

        } else if (prefix.stream().anyMatch( p-> (p.type() == Paragraph.Type.ZBLOCK))) {
            // there is at least one Z block
            List<String> parents = Collections.singletonList("standard_toolkit");
            ps.add(0, new SectionHeader(name, parents));

        } else {  // everything is in sections except some initial informals
            String sname = name + "_informal";
            ps.add(0, new SectionHeader(sname, null));
        }
        return ps;
    }

    // for a given name of a file (including parent files)
    // read all the sections in that file
    private List<Section> filenameToSections(String name) throws IOException {
        List<Section> sections = new ArrayList<>();
        List<Paragraph> ps = addHeader(name);
        if (ps.size() == 0)
            return sections;
        Section s = null;
        for (Paragraph p : ps) {
            if (p.type() == Paragraph.Type.SECTION) {
                if (s != null)
                    sections.add(s);
                SectionHeader h = ((SectionHeader)p);
                s = new Section(h.getName(),h.getParents());
            }
            assert s != null;
            s.getParagraphs().add(p);
        }
        if (s.getParagraphs().size() > 0)
            sections.add(s);
        return sections;
    }


    // recursively read the specification and its dependencies
    private Set<Section> readSpec(Set<String> names, Set<Section> sections) throws IOException {
        Set<Section> ss = new HashSet<>();
        if (sections != null)
            ss.addAll(sections);
        if (names.isEmpty())
            return ss;

        Set<Section> ss2 = new HashSet<>();
        for (String n : names) {
            Set<Section> f2s = new HashSet<>(filenameToSections(n));
            ss2.addAll(f2s);
        }

        //prepare first param
        Set<String> firstParam = sectionsToParents(ss2);
        List<String> snames = ss.stream().map(Section::getName).collect(Collectors.toList());
        firstParam.removeAll(snames);
        firstParam.removeAll(names);

        //second param
        ss2.addAll(ss);

        return readSpec(firstParam, ss2);
    }

    // Sort the sections in parental dependency order
    private List<Section> orderSections(Set<Section> sections) throws CircularDependencyException {

        List<Section>  sorted = new ArrayList<>();

        boolean done = false;
        while (!done) {
            Optional<Section> n = sections.stream().filter(s->s.mark==Section.Mark.NONE).findFirst();
            if (n.isPresent()) {
                n.get().visit(sections, sorted);
            } else {
                done = true;
            }
        }
        Optional<Section> prelude = sorted.stream().filter(s->s.getName().equals("prelude"))
                .findFirst();
        prelude.ifPresent(section -> {
            sorted.remove(section);
            sorted.add(0, section);
        });
        return sorted;
    }

    // order the sections after adding "prelude" to the collection.  We make sure that
    // "prelude" comes first in the order.
    private void sortSections(String name) throws IOException, CircularDependencyException {
        Set<String> names = new HashSet<>();
        names.add(name);
        names.add("prelude");
        Set<Section> sectionSet = readSpec(names, null);
        sections = orderSections(sectionSet);
    }

    private void expandDefinitions() {
        Map<String, Map<String, Directive> > definesBySection = new HashMap<>();
        Map<String, Map<String, Directive> > cumulativeDefines = new HashMap<>();
        // first collect definitions
        for (Section s : sections) {
            Map<String, Directive> definitions = s.collectDefinitions();
            definesBySection.put(s.getName(), definitions);
            cumulativeDefines.put(s.getName(), definitions);
        }
        // now cascade down
        for (Section s : sections) {
            Map<String, Directive> cumulativeDefine = cumulativeDefines.get(s.getName());
            if (definesBySection.containsKey("prelude"))
                cumulativeDefine.putAll(definesBySection.get("prelude"));
            for (String p : s.getParents()) {
                if (cumulativeDefines.containsKey(p))
                    cumulativeDefine.putAll(cumulativeDefines.get(p));
            }
            cumulativeDefines.put(s.getName(), cumulativeDefine);

            // now expand all the definitions
            //s.expandDefinitions(cumDef);
        }
    }
}
