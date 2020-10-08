package com.dyptan;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;

public class StreamLauncher {

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

        logger.info("RPC server started to listen on port " + port);
        server.awaitTermination();

    }
}
