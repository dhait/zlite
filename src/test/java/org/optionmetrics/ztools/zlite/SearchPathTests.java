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

package org.optionmetrics.ztools.zlite;import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.optionmetrics.ztools.zlite.SearchPath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

public class SearchPathTests {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void findResourceTest() throws IOException {
        SearchPath searchPath = new SearchPath();
        searchPath.addItem(SearchPath.SourceType.RESOURCE_PATH, "");
        InputStream stream = searchPath.find("birthday.zlt");
        assertNotNull(stream);
    }

    @Test
    public void missingResourceTest() throws IOException {
        SearchPath searchPath = new SearchPath();
        searchPath.addItem(SearchPath.SourceType.RESOURCE_PATH, "");
        InputStream stream = searchPath.find("apple.zlt");
        assertNull(stream);
    }

    @Test
    public void findFileTest() throws IOException {

        File tempFile = testFolder.newFile("file.zlt");
        SearchPath searchPath = new SearchPath();
        searchPath.addItem(SearchPath.SourceType.DIRECTORY, testFolder.getRoot().getAbsolutePath());
        InputStream stream = searchPath.find("file.zlt");
        assertNotNull(stream);
    }

    @Test
    public void missingFileTest() throws IOException {

        File tempFile = testFolder.newFile("file.zlt");
        SearchPath searchPath = new SearchPath();
        searchPath.addItem(SearchPath.SourceType.DIRECTORY, testFolder.getRoot().getAbsolutePath());
        InputStream stream = searchPath.find("nofile.zlt");
        assertNull(stream);
    }
}
