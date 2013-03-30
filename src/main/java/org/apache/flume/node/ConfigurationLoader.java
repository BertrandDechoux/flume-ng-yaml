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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;

import com.google.common.base.Joiner;

/**
 * Read a file and provide an equivalent configuration to flume-ng, regardless
 * of the file format.
 */
public class ConfigurationLoader {
    private static final Yaml YAML = new Yaml();

    /**
     * Configuration from a YAML file.
     */
    public static Map<String, String> yamlAsConfiguration(String filename)
            throws IOException {
        BufferedReader reader = null;
        Map<String, String> configuration = Collections.emptyMap();
        try {
            reader = new BufferedReader(new FileReader(filename));
            configuration = yamlAsConfiguration(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return configuration;
    }

    /**
     * Configuration from a YAML reader.
     */
    public static Map<String, String> yamlAsConfiguration(BufferedReader reader) {
        Map<String, String> configuration = new LinkedHashMap<String, String>();
        Iterable<Event> events = YAML.parse(reader);
        List<String> keyFragments = new ArrayList<String>();
        List<String> composants = new ArrayList<String>();
        String keyFragment = null;
        for (Event event : events) {
            if (event instanceof ScalarEvent) {
                String value = ((ScalarEvent) event).getValue();
                if (keyFragment == null) {
                    keyFragment = value;
                    if (!keyFragments.isEmpty()) {
                        String lastFragment = last(keyFragments);
                        if (isAComponentList(lastFragment)) {
                            composants.add(keyFragment);
                        }
                    }
                } else {
                    String dottedKey = Joiner.on(".").join(keyFragments) + '.'
                            + keyFragment;
                    configuration.put(dottedKey, value);
                    keyFragment = null;
                }
            } else if (event instanceof MappingStartEvent) {
                if (keyFragment != null) {
                    keyFragments.add(keyFragment);
                    keyFragment = null;
                }
            } else if (event instanceof MappingEndEvent) {
                if (!keyFragments.isEmpty()) {
                    String lastFragment = last(keyFragments);
                    if (isAComponentList(lastFragment)) {
                        String dottedKey = Joiner.on(".").join(keyFragments);
                        String value = Joiner.on(' ').join(composants);
                        configuration.put(dottedKey, value);
                        composants.clear();
                    }
                    keyFragments.remove(keyFragments.size() - 1);
                }
            }
        }
        return configuration;
    }

    private static String last(List<String> list) {
        return list.get(list.size() - 1);
    }

    /**
     * Check that the {@link String} does not refer to a component list.
     * 
     * One of the redundant point of the flume-ng configuration is listing
     * components and declaring them at the same times. It is a legacy of using
     * a {@link Properties} based configuration.
     */
    public static boolean isAComponentList(String fragment) {
        return fragment.equals("sources") || fragment.equals("channels")
                || fragment.equals("sinks");
    }

    /**
     * Configuration from a Properties file.
     */
    public static Map<String, String> propertiesAsConfiguration(String filename)
            throws IOException {
        BufferedReader reader = null;
        Map<String, String> configuration = Collections.emptyMap();
        try {
            reader = new BufferedReader(new FileReader(filename));
            configuration = propertiesAsConfiguration(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return configuration;
    }

    /**
     * Configuration from a Properties file.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> propertiesAsConfiguration(
            BufferedReader reader) throws IOException {
        Properties properties = new Properties();
        properties.load(reader);
        // Properties are by definition Map<Object,Object> but sometimes
        // we know better.
        return (Map<String, String>) (Map<?, ?>) properties;
    }

}
