package com.fleetmgr.sdk.client.configuration;

import com.google.common.base.CaseFormat;
import lombok.ToString;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by: Bartosz Nawrot
 * Date: 13.12.2018
 * Description:
 */
@ToString
public class Configuration {

    private HashMap<String, String> values;

    public static Configuration load(String path, String envTag, List<String> required) throws IOException {
        try (FileInputStream fis = new FileInputStream(path)) {
            Properties p = new Properties();
            p.load(fis);
            return new Configuration(p, envTag, required);

        } catch (IOException e) {
            System.out.println("Could not load config file: " + path);
            return new Configuration(null, envTag, required);
        }
    }

    private Configuration(Properties properties, String envTag, List<String> required) throws IOException {
        values = new HashMap<>();
        for (String key : required) {
            String envKey = envTag + CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, key);
            String value = System.getenv(envKey);
            if (value == null && properties != null) {
                value = properties.getProperty(key);
            }
            if (value == null) {
                throw new IOException("Could not load required configuration parameter: " + key);
            }
            values.put(key, key);
        }
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public String getString(String key) {
        return values.get(key);
    }

    public int getInteger(String key) {
        return Integer.valueOf(values.get(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.valueOf(key);
    }
}
