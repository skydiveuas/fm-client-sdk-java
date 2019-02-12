package com.fleetmgr.sdk.client;

import lombok.Builder;
import lombok.ToString;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by: Bartosz Nawrot
 * Date: 11.02.2019
 * Description:
 */
@Builder
@ToString
public class ClientConfig {

    public final String apiKey;
    public final String coreAddress;
    public final String facadeCertPath;

    public static ClientConfig load(String path) throws IOException {
        Properties config = new Properties();
        config.load(new FileInputStream(path));

        // load from file
        String apiKey = config.getProperty("apiKey");
        String coreAddress = config.getProperty("coreAddress");
        String facadeCertPath = config.getProperty("facadeCertPath");

        // override with environmental variables
        String val = System.getenv("COM_FLEETMGR_EDGE_DISCOVERY_PORT");
        if (val != null) apiKey = val;
        val = System.getenv("COM_FLEETMGR_EDGE_DISCOVERY_PORT");
        if (val != null) coreAddress = val;
        val = System.getenv("COM_FLEETMGR_EDGE_DISCOVERY_PORT");
        if (val != null) facadeCertPath = val;

        // check if all parameters are set
        if (apiKey == null
                || coreAddress == null
                || facadeCertPath == null) {
            throw new IOException("Not all configuration parameters defined");
        }

        return ClientConfig.builder()
                .apiKey(apiKey)
                .coreAddress(coreAddress)
                .facadeCertPath(facadeCertPath)
                .build();
    }
}
