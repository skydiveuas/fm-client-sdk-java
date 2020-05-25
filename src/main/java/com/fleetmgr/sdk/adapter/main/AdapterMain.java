package com.fleetmgr.sdk.adapter.main;

import com.fleetmgr.sdk.adapter.configuration.AdapterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by: Bartosz Nawrot
 * Date: 17.07.2019
 * Description:
 */
public class AdapterMain {

    private static Representer representer = new Representer() {
        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
            // if value of property is null, ignore it.
            if (propertyValue == null) {
                return null;
            }
            else {
                return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
            }
        }
    };

    public static void main(String[] args) {
        if (System.getProperty("logfile") == null) {
            System.setProperty("logfile", "adapter");
        }
        Logger logger = LoggerFactory.getLogger(AdapterMain.class);

        String clientConfigPath;
        if (args.length == 1) {
            clientConfigPath = args[0];

        } else {
            logger.error("Config input unspecified");
            System.exit(-1);
            return;
        }

        logger.info("Using configuration path:\n{}", clientConfigPath);

        Constructor constructor = new Constructor(AdapterConfig.class);
        Yaml yaml = new Yaml(constructor, representer);

        AdapterConfig adapterConfig;
        try {
            InputStream input = Files.newInputStream(Paths.get(clientConfigPath));
            AdapterConfig yamlConfig = yaml.loadAs(input, AdapterConfig.class);
            adapterConfig = yamlConfig.validate();
        } catch (Exception e) {
            logger.error("", e);
            System.exit(-1);
            return;
        }

        logger.info("Configuration:\n{}", yaml.dump(adapterConfig));

        ExecutorService executor = Executors.newCachedThreadPool();
        AdapterController adapterController = new AdapterController(adapterConfig, executor);

        try {
            adapterController.start();
        } catch (Exception e) {
            logger.error("", e);
            executor.shutdownNow();
        }
    }
}
