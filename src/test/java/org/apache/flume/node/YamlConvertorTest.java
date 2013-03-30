/**
 *  Copyright 2013 Bertrand Dechoux
 *  
 *  This file is part of the flume-ng-yaml project.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.flume.node;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.flume.cli.Properties2Yaml;
import org.apache.flume.cli.Yaml2Properties;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class YamlConvertorTest {

    @Test
    public void shouldProduceTheSameConfiguration()
            throws IOException {
        String yamlFilename = "src/test/resources/flume-ng-conf.yaml";
        String propFilename = "src/test/resources/flume-ng-conf.properties";
        Map<String, String> yamlConf = ConfigurationLoader
                .yamlAsConfiguration(yamlFilename);
        Map<String, String> propConf = ConfigurationLoader
                .propertiesAsConfiguration(propFilename);
        assertThat(sort(yamlConf)).isEqualTo(sort(propConf));
    }
    
    @Test
    public void shouldCompleteProperties2Yaml() throws IOException {
        System.setProperty("line.separator", "\n");
        String propertiesInputFile = "src/test/resources/flume-ng-conf.properties";
        String yamlOutputFile = "target/run-flume-ng-conf.yaml";
        Properties2Yaml.main(propertiesInputFile,yamlOutputFile);
        assertSameContent("src/test/resources/run-flume-ng-conf.yaml", yamlOutputFile);
    }
    
    @Test
    public void shouldCompleteYaml2Properties() throws IOException {
        System.setProperty("line.separator", "\n");
        String yamlInputFile = "src/test/resources/flume-ng-conf.yaml";
        String propertiesOutputFile = "target/run-flume-ng-conf.properties";
        Yaml2Properties.main(yamlInputFile,propertiesOutputFile);
        assertSameContent("src/test/resources/run-flume-ng-conf.properties", propertiesOutputFile);
    }

    private Map<String, String> sort(Map<String, String> yamlConf) {
        return new TreeMap<String, String>(yamlConf);
    }
    
    private void assertSameContent(String file1, String file2) throws IOException {
        String content1 = Files.toString(new File(file1), Charsets.UTF_8);
        String content2 = Files.toString(new File(file2), Charsets.UTF_8);
        assertThat(content1).isEqualTo(content2);
    }

}
