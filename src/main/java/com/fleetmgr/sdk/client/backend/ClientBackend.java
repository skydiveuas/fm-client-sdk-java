package com.fleetmgr.sdk.client.backend;

import com.fleetmgr.interfaces.AttachResponse;
import com.fleetmgr.interfaces.Location;
import com.fleetmgr.interfaces.OperateResponse;
import com.fleetmgr.interfaces.facade.control.ClientMessage;
import com.fleetmgr.interfaces.facade.control.ControlMessage;
import com.fleetmgr.interfaces.facade.control.FacadeServiceGrpc;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.core.CoreClient;
import com.fleetmgr.sdk.client.event.input.connection.ConnectionEvent;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.stub.StreamObserver;
import org.cfg4j.provider.ConfigurationProvider;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by: Bartosz Nawrot
 * Date: 07.12.2018
 * Description:
 */
public class ClientBackend implements StreamObserver<ControlMessage> {

    private final ExecutorService executor;
    private final ConfigurationProvider configuration;

    private Client client;
    private Client.Listener clientListener;

    private CoreClient core;

    private HeartbeatHandler heartbeatHandler;
    private ChannelsHandler channelsHandler;

    private ManagedChannel channel;
    private StreamObserver<ClientMessage> toFacade;

    public ClientBackend(ExecutorService executor,
                         ConfigurationProvider configuration,
                         Client client,
                         Client.Listener clientListener,
                         CoreClient core) {
        this.executor = executor;
        this.configuration = configuration;

        this.client = client;
        this.clientListener = clientListener;

        this.core = core;

        this.heartbeatHandler = new HeartbeatHandler(client, this);
        this.channelsHandler = new ChannelsHandler(client, executor);
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public ConfigurationProvider getConfiguration() {
        return configuration;
    }

    public CoreClient getCore() {
        return core;
    }

    public HeartbeatHandler getHeartbeatHandler() {
        return heartbeatHandler;
    }

    public ChannelsHandler getChannelsHandler() {
        return channelsHandler;
    }

    Location getLocation() {
        return clientListener.getLocation();
    }

    public void openFacadeConnection(AttachResponse attachResponse) throws SSLException {
        openFacadeConnection(
                attachResponse.getHost(),
                attachResponse.getUnsafePort(),
                attachResponse.getTlsPort(),
                configuration.getProperty("facade.useTls", Boolean.class));
    }

    public void openFacadeConnection(OperateResponse operateResponse) throws SSLException {
        openFacadeConnection(
                operateResponse.getHost(),
                operateResponse.getUnsafePort(),
                operateResponse.getTlsPort(),
                configuration.getProperty("facade.useTls", Boolean.class));
    }

    private void openFacadeConnection(String ip, int unsafePort, int tlsPort, boolean useTls) throws SSLException {
        if (useTls) {
            SslContext sslContext =
                    buildSslContext(
                            configuration.getProperty("facadeCertPath", String.class),
                            null,
                            null);

            channel = NettyChannelBuilder
                    .forAddress(ip, tlsPort)
                    .negotiationType(NegotiationType.TLS)
                    .sslContext(sslContext)
                    .overrideAuthority("localhost")
                    .build();
            trace(Level.INFO, "Started TLS gRPC channel");
        } else {
            channel = NettyChannelBuilder
                    .forAddress(ip, unsafePort)
                    .negotiationType(NegotiationType.PLAINTEXT)
                    .build();
            trace(Level.INFO, "Started Unsafe gRPC channel");
        }

        FacadeServiceGrpc.FacadeServiceStub stub = FacadeServiceGrpc.newStub(channel);
        toFacade = stub.control(this);
    }

    protected void closeFacadeConnection() {
        toFacade.onCompleted();
    }

    public void closeFacadeChannel() {
        try {
            channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void send(ClientMessage message) {
        ClientMessage verified = client.verifySending(message);
        if (verified != null) {
            trace(Level.INFO, "Sending:\n" + message + "@ " + client.getStateName());
            toFacade.onNext(message);
        }
    }

    @Override
    public void onNext(ControlMessage message) {
        client.notifyEvent(new Received(message));
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
        client.notifyEvent(new ConnectionEvent(ConnectionEvent.Type.ERROR));
    }

    @Override
    public void onCompleted() {
        client.notifyEvent(new ConnectionEvent(ConnectionEvent.Type.CLOSED));
    }

    public void trace(Level level, String message) {
        client.log(level, message);
    }

    @SuppressWarnings("SameParameterValue")
    private static SslContext buildSslContext(String trustCertCollectionFilePath,
                                              String clientCertChainFilePath,
                                              String clientPrivateKeyFilePath) throws SSLException {
        SslContextBuilder builder = GrpcSslContexts.forClient();
        if (trustCertCollectionFilePath != null) {
            builder.trustManager(new File(trustCertCollectionFilePath));
        }
        if (clientCertChainFilePath != null && clientPrivateKeyFilePath != null) {
            builder.keyManager(new File(clientCertChainFilePath), new File(clientPrivateKeyFilePath));
        }
        return builder.build();
    }
}
