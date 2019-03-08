package com.fleetmgr.sdk.client.configuration;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by: Bartosz Nawrot
 * Date: 11.02.2019
 * Description:
 */
public class ClientConfig {
    public static Configuration load(String path) throws IOException {
        LinkedList<String> required = new LinkedList<>();
        required.add("apiKey");
        required.add("coreHost");
        required.add("corePort");
        required.add("facadeCertPath");
        return Configuration.load(path, "COM_FLEETMGR_CLIENT_", required);
    }
}