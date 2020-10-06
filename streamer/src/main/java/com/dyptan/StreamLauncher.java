package com.dyptan;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class StreamLauncher {
    public static StreamTransformer transformer;
    public static Thread detatchedTransformer;
    public static int port = 8082;
    final static Logger logger = Logger.getLogger(StreamLauncher.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {
        logger.info("Starting Stream transformer.");
        StreamGateway.init();

        logger.info("Starting RPC server.");
        Server server = ServerBuilder
                .forPort(port)
                .addService(new StreamGateway()).build();
        server.start();

        logger.info("Server started, listening on " + port);
        server.awaitTermination();

    }
}
