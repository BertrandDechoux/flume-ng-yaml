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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.flume.conf.FlumeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Acts like a {@link PropertiesFileConfigurationProvider} but reads a YAML file
 * instead of a properties file.
 */
public class YamlFileConfigurationProvider extends
        AbstractConfigurationProvider {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PropertiesFileConfigurationProvider.class);

    private final File file;

    public YamlFileConfigurationProvider(String agentName, File file) {
        super(agentName);
        this.file = file;
    }

    @Override
    public FlumeConfiguration getFlumeConfiguration() {
        BufferedReader reader = null;
        try {
            return new FlumeConfiguration(
                    ConfigurationLoader.yamlAsConfiguration(file
                            .getAbsolutePath()));
        } catch (IOException ex) {
            LOGGER.error("Unable to load file:" + file
                    + " (I/O failure) - Exception follows.", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    LOGGER.warn(
                            "Unable to close file reader for file: " + file, ex);
                }
            }
        }
        return new FlumeConfiguration(new HashMap<String, String>());
    }
}