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

import org.apache.flume.node.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * External entry point for YAML to Properties conversion.
 */
public class Yaml2Properties {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Yaml2Properties.class);

    /**
     * Convert a flume-ng configuration from a YAML format to a properties
     * format.
     * 
     * @throws IOException
     *             fail fast, no strategy on error
     */
    public static void main(String... args) throws IOException {
        if (args.length != 2) {
            LOGGER.error("Usage : <yamlInputFile> <propertiesOutputFile>");
            return;
        }
        String yamlInputFile = args[0];
        String propertiesOutputFile = args[1];
        writeAsProperties(yamlInputFile, propertiesOutputFile,
                ConfigurationLoader.yamlAsConfiguration(yamlInputFile));
    }

    /** Write the configuration the provided path using a Properties format */
    private static void writeAsProperties(String yamlInputFile,
            String propertiesOutputFile, Map<String,String> configuration)
            throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(propertiesOutputFile));
            writeHeader(yamlInputFile, writer);
            writeContent(configuration, writer);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static void writeContent(Map<String, String> configuration,
            BufferedWriter writer) throws IOException {
        for (Entry<String, String> entry : configuration.entrySet()) {
            writer.write(entry.getKey());
            writer.write("=");
            writer.write(entry.getValue());
            writer.newLine();
        }
    }

    private static void writeHeader(String yamlInputFile, BufferedWriter writer)
            throws IOException {
        writer.write("#");
        writer.write("flume-ng properties configuration generated from YAML file : ");
        writer.newLine();
        writer.write("#");
        writer.write(yamlInputFile);
        writer.newLine();
    }
}
