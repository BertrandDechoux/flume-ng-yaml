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
package org.apache.flume.cli;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.flume.node.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * External entry point for Properties to YAML conversion.
 */
public class Properties2Yaml {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Properties2Yaml.class);

    /**
     * Convert a flume-ng configuration from a properties format to a YAML
     * format.
     * 
     * @throws IOException
     *             fail fast, no strategy on error
     */
    public static void main(String... args) throws IOException {
        if (args.length != 2) {
            LOGGER.error("Usage : <propertiesInputFile> <yamlOutputFile>");
            return;
        }
        String propertiesInputFile = args[0];
        String yamlOutputFile = args[1];
        Map<String, String> flatConf = ConfigurationLoader
                .propertiesAsConfiguration(propertiesInputFile);
        writeYaml(propertiesInputFile, yamlOutputFile, unflatten(flatConf));
    }

    /** Write the YAML to the provided path */
    private static void writeYaml(String propertiesInputFile,
            String yamlOutputFile, Object conf) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(yamlOutputFile));
            writeHeader(propertiesInputFile, writer);
            new Yaml().dump(conf, writer);
        } catch (Exception e) {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static void writeHeader(String propertiesInputFile,
            BufferedWriter writer) throws IOException {
        writer.write("#");
        writer.write("flume-ng YAML configuration generated from properties file : ");
        writer.newLine();
        writer.write("#");
        writer.write(propertiesInputFile);
        writer.newLine();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> unflatten(Map<String, String> flatConf) {
        Map<String, Object> conf = new TreeMap<String, Object>();
        for (Entry<String, String> entry : flatConf.entrySet()) {
            String[] path = entry.getKey().split("\\.");
            String value = entry.getValue();
            if (ConfigurationLoader.isAComponentList(path[path.length - 1])) {
                continue;
            }
            Map<String, Object> previous = conf;
            Map<String, Object> current = null;
            for (int i = 0; i < path.length - 1; i++) {
                String p = path[i];
                current = (Map<String, Object>) previous.get(p);
                if (current == null) {
                    current = new TreeMap<String, Object>();
                    previous.put(p, current);
                }
                previous = current;
            }
            previous.put(path[path.length - 1], value);
        }
        return conf;
    }

}
