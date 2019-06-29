package com.fleetmgr.sdk.client.backend;

import com.fleetmgr.interfaces.AttachResponse;
import com.fleetmgr.interfaces.Location;
import com.fleetmgr.interfaces.OperateResponse;
import com.fleetmgr.interfaces.facade.control.ClientMessage;
import com.fleetmgr.interfaces.facade.control.ControlMessage;
import com.fleetmgr.interfaces.facade.control.FacadeServiceGrpc;
import com.fleetmgr.interfaces.facade.control.SetupResponse;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.core.CoreClient;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.stub.StreamObserver;
import org.cfg4j.provider.ConfigurationProvider;
import org.slf4j.Logger;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Bartosz Nawrot
 * Date: 07.12.2018
 * Description:
 */
public class ClientBackend implements StreamObserver<ControlMessage> {

    private final Logger logger;
    private final ExecutorService executor;
    private final ConfigurationProvider configuration;

    private Client client;
    private Client.Listener clientListener;

    private CoreClient core;

    private SetupResponse setupResponse;

    private HeartbeatHandler heartbeatHandler;
    private ChannelsHandler channelsHandler;

    private ManagedChannel channel;
    private StreamObserver<ClientMessage> toFacade;

    public ClientBackend(ExecutorService executor,
                         ConfigurationProvider configuration,
                         Client client,
                         Client.Listener clientListener) {
        this.logger = client.getLogger();
        this.executor = executor;
        this.configuration = configuration;

        this.client = client;
        this.clientListener = clientListener;

        this.core = new CoreClient(configuration, logger);

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

    public void setSetupResponse(SetupResponse setupResponse) {
        this.setupResponse = setupResponse;
    }

    public SetupResponse getSetupResponse() {
        return setupResponse;
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
                            configuration.getProperty("facade.certPath", String.class),
                            null,
                            null);

            channel = NettyChannelBuilder
                    .forAddress(ip, tlsPort)
                    .negotiationType(NegotiationType.TLS)
                    .sslContext(sslContext)
                    .overrideAuthority("localhost")
                    .build();
            logger.info("Started TLS gRPC channel");
        } else {
            channel = NettyChannelBuilder
                    .forAddress(ip, unsafePort)
                    .negotiationType(NegotiationType.PLAINTEXT)
                    .build();
            logger.info("Started Unsafe gRPC channel");
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
        } catch (InterruptedException ignored) {
        }
    }

    public void send(ClientMessage message) {
        ClientMessage verified = client.verifySending(message);
        if (verified != null) {
            logger.info(": {}: Sending:\n{}", client.getStateName(), message);
            toFacade.onNext(message);
        }
    }

    @Override
    public void onNext(ControlMessage message) {
        client.notifyEvent(new Received(message));
    }

    @Override
    public void onError(Throwable t) {
        logger.warn("Facade connection failure: ", t);
    }

    @Override
    public void onCompleted() {
        logger.info("Facade connection closed");
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
