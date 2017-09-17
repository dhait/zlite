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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of paths (directories or resource paths) in which to search for Ztext files.
 */
public class SearchPath {

    /**
     * The type of the path
     */
    public enum SourceType {
        RESOURCE_PATH,
        DIRECTORY
    }

    /**
     * A single entry in the search path list
     */
    class Source {
        SourceType type;
        String path;
        Source(SourceType type, String path) {
            this.type = type;
            this.path = path;
        }
    }

    private List<Source> sources = new ArrayList<>();


    public void addItem(SourceType type, String path ) {
        sources.add(new Source(type, path));
    }

    InputStream find(String name) throws IOException {
        for (Source s : sources) {
            if (s.type == SourceType.RESOURCE_PATH) {
                URL url = this.getClass().getResource(s.path + File.separatorChar + name);
                if (url != null) {
                    return url.openStream();
                }
            }
            else if (s.type == SourceType.DIRECTORY) {
                File f = new File(s.path, name);
                if (f.exists()) {
                    URL url = f.toURI().toURL();
                    return url.openStream();
                }
            }
        }
        // not found
        return null;
    }
}
