package com.fleetmgr.sdk.client;

import com.fleetmgr.interfaces.Location;
import com.fleetmgr.interfaces.facade.control.ClientMessage;
import com.fleetmgr.sdk.client.backend.ClientBackend;
import com.fleetmgr.sdk.client.core.CoreClient;
import com.fleetmgr.sdk.client.event.input.Event;
import com.fleetmgr.sdk.client.event.output.facade.FacadeEvent;
import com.fleetmgr.sdk.system.machine.StateMachine;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

/**
 * Created by: Bartosz Nawrot
 * Date: 04.09.2018
 * Description:
 */
public abstract class Client extends StateMachine<Event> {

    public interface Listener {
        Location getLocation();
        void onEvent(FacadeEvent event);
        void log(Level level, String message);
    }

    private Listener listener;

    protected ClientBackend backend;

    Client(ExecutorService executor, String configPath, Listener listener) {
        this(executor, loadConfigurationProvider(configPath), listener);
    }

    Client(ExecutorService executor, ConfigurationProvider configuration, Listener listener) {
        super(executor, null);
        this.listener = listener;

        CoreClient coreClient = new CoreClient(configuration, this::log);

        this.backend = new ClientBackend(executor, configuration,this, listener, coreClient);
    }

    @Override
    public void log(Level level, String message) {
        listener.log(level, message);
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
}
