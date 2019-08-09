package com.fleetmgr.sdk.client;

import com.fleetmgr.interfaces.Location;
import com.fleetmgr.interfaces.facade.control.ClientMessage;
import com.fleetmgr.sdk.client.backend.ClientBackend;
import com.fleetmgr.sdk.client.event.input.Event;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.system.machine.StateMachine;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.ExecutorService;

/**
 * Created by: Bartosz Nawrot
 * Date: 04.09.2018
 * Description:
 */
public abstract class Client extends StateMachine<Event> {

    public interface Listener {
        void onEvent(FacadeEvent event);
        Location getLocation();
    }

    protected static final Logger logger = LoggerFactory.getLogger(Client.class);

    private final String name;
    protected ClientBackend backend;

    Client(ExecutorService executor, String configPath,
           Listener listener, String name) {
        this(executor, loadConfigurationProvider(configPath), listener, name);
    }

    Client(ExecutorService executor, ConfigurationProvider configuration,
           Listener listener, String name) {
        super(executor, null);
        this.name = name;
        this.backend = new ClientBackend(executor, configuration,this, listener);
    }

    public ClientMessage verifySending(ClientMessage message) {
        return message;
    }

    static ConfigurationProvider loadConfigurationProvider(String path) {
        ConfigFilesProvider configFilesProvider = () -> Collections.singletonList(
                Paths.get(new File(path).getAbsolutePath()));
        ConfigurationSource source = new FilesConfigurationSource(configFilesProvider);
        return new ConfigurationProviderBuilder()
                .withConfigurationSource(source)
                .build();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ":" + getStateName();
    }
}
