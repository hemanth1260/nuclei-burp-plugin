/*
 * MIT License
 *
 * Copyright (c) 2021 ProjectDiscovery, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.projectdiscovery.nuclei.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

class UtilsTest {

    @Test
    void testCommandSplitToChunks() {
        final Map<String, String[]> testCases = Map.of(
                "nuclei -t ~/nuclei-templates/my-template.yaml -u http://localhost:8080", new String[]{"nuclei", "-t", "~/nuclei-templates/my-template.yaml", "-u", "http://localhost:8080"},
                "nuclei -t \"/tmp/dir space/template.yaml\" -u \"/users/directory with space/\"", new String[]{"nuclei", "-t", "/tmp/dir space/template.yaml", "-u", "/users/directory with space/"},
                "\"c:/program files/nuclei.exe\" -t \"template.yaml\" -u \"c:/users/directory with space/\" -nc", new String[]{"c:/program files/nuclei.exe", "-t", "template.yaml", "-u", "c:/users/directory with space/", "-nc"}
        );

        testCases.forEach((key, value) -> Assertions.assertArrayEquals(value, Utils.stringCommandToChunks(key)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"nuclei -t \"c:/directory name with space/another one/something.yaml\" -u http://localhost",
                            "nuclei -t 'c:/directory name with space/another one/something.yaml' -u http://localhost",
                            "nuclei -t 'c:/temp/something.yaml' -u http://localhost"})
    void testNucleiTemplateParameterPattern(String testCase) {
        Assertions.assertEquals("nuclei -t test.yaml -u http://localhost", Utils.replaceTemplatePathInCommand(testCase, "test.yaml"));
    }

    @Test
    void testTemplateNormalization() {
        final String yamlTemplate = "id: template-id\n" +
                                    "info:\n" +
                                    "  name: Template Name\n" +
                                    "  author: istvan\n" +
                                    "  severity: info\n" +
                                    "requests:\n" +
                                    "- raw:\n" +
                                    "  - |+\n" +
                                    "    GET / HTTP/1.1\n" +
                                    "    Host: http://localhost:8080\n" +
                                    "  matchers-condition: and\n" +
                                    "  matchers:\n" +
                                    "  - type: word\n" +
                                    "    part: body\n" +
                                    "    condition: or\n" +
                                    "    words:\n" +
                                    "    - f=\"bin.bin\">bin.bin</a></li>\n" +
                                    "    - <li><a href=\"dns.yaml\">dns.yaml</a></li>";

        final String expected = "id: template-id\n" +
                                "\n" +
                                "info:\n" +
                                "  name: Template Name\n" +
                                "  author: istvan\n" +
                                "  severity: info\n" +
                                "\n" +
                                "requests:\n" +
                                "- raw:\n" +
                                "  - |+\n" +
                                "    GET / HTTP/1.1\n" +
                                "    Host: http://localhost:8080\n" +
                                "\n" +
                                "  matchers-condition: and\n" +
                                "  matchers:\n" +
                                "  - type: word\n" +
                                "    part: body\n" +
                                "    condition: or\n" +
                                "    words:\n" +
                                "    - f=\"bin.bin\">bin.bin</a></li>\n" +
                                "    - <li><a href=\"dns.yaml\">dns.yaml</a></li>";

        Assertions.assertEquals(expected, Utils.normalizeTemplate(yamlTemplate));
    }
}