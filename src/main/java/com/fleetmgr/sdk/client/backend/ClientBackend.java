package com.fleetmgr.sdk.client.backend;

import com.fleetmgr.interfaces.Location;
import com.fleetmgr.interfaces.facade.control.ClientMessage;
import com.fleetmgr.interfaces.facade.control.ControlMessage;
import com.fleetmgr.interfaces.facade.control.FacadeServiceGrpc;
import com.fleetmgr.interfaces.facade.control.SetupResponse;
import com.fleetmgr.sdk.client.Client;
import com.fleetmgr.sdk.client.core.CoreClient;
import com.fleetmgr.sdk.client.core.model.FacadeResponse;
import com.fleetmgr.sdk.client.event.input.connection.Received;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.Setter;
import org.cfg4j.provider.ConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by: Bartosz Nawrot
 * Date: 07.12.2018
 * Description:
 */
public class ClientBackend implements StreamObserver<ControlMessage> {

    private static final Logger logger = LoggerFactory.getLogger(ClientBackend.class);

    @Getter
    private final ExecutorService executor;
    @Getter
    private final ConfigurationProvider configuration;

    @Getter
    private final Client client;
    @Getter
    private final Client.Listener clientListener;

    @Getter
    private final CoreClient core;

    @Setter
    @Getter
    private SetupResponse setupResponse;

    @Getter
    private final HeartbeatHandler heartbeatHandler;
    @Getter
    private final ChannelsHandler channelsHandler;

    private ManagedChannel channel;
    private StreamObserver<ClientMessage> toFacade;

    public ClientBackend(ExecutorService executor,
                         ConfigurationProvider configuration,
                         Client client,
                         Client.Listener clientListener) {
        this.executor = executor;
        this.configuration = configuration;

        this.client = client;
        this.clientListener = clientListener;

        this.core = new CoreClient(configuration);

        this.heartbeatHandler = new HeartbeatHandler(this);
        this.channelsHandler = new ChannelsHandler(this);
    }

    Location getLocation() {
        return clientListener.getLocation();
    }

    public void openFacadeConnection(FacadeResponse facadeResponse) throws IOException {
        openFacadeConnection(
                facadeResponse.getHost(),
                facadeResponse.getUnsafePort(),
                facadeResponse.getTlsPort(),
                configuration.getProperty("facade.useTls", Boolean.class));
    }

    private void openFacadeConnection(String ip, int unsafePort, int tlsPort, boolean useTls) throws IOException {
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
            logger.info("{}: Started TLS gRPC channel", client);
        } else {
            channel = NettyChannelBuilder
                    .forAddress(ip, unsafePort)
                    .negotiationType(NegotiationType.PLAINTEXT)
                    .build();
            logger.info("{}: Started Unsafe gRPC channel", client);
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
            logger.debug("{}: Sending:\n{}", client, message);
            toFacade.onNext(message);
        }
    }

    @Override
    public void onNext(ControlMessage message) {
        client.notifyEvent(new Received(message));
    }

    @Override
    public void onError(Throwable t) {
        logger.warn("{}: Facade connection failure: ", client, t);
    }

    @Override
    public void onCompleted() {
        logger.info("{}: Facade connection closed", client);
    }

    @SuppressWarnings("SameParameterValue")
    private static SslContext buildSslContext(String trustCertCollectionFilePath,
                                              String clientCertChainFilePath,
                                              String clientPrivateKeyFilePath) throws IOException {
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
