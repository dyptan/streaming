package com.dyptan;

import com.dyptan.gen.proto.ApplyRequest;
import com.dyptan.gen.proto.StatusReply;
import com.dyptan.gen.proto.StreamerGatewayGrpc;
import io.grpc.stub.StreamObserver;
import org.apache.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;

public class StreamGateway extends StreamerGatewayGrpc.StreamerGatewayImplBase {
    final static Logger logger = Logger.getLogger(StreamGateway.class.getName());
    public static StreamTransformer transformer;
    public static Thread detatchedTransformer;

    @Override
    public void applyNewModel(ApplyRequest request, StreamObserver<StatusReply> responseObserver) {
        logger.info("Stopping Streamer with Id: "+transformer.id);
        detatchedTransformer.interrupt();
        logger.info("Streamer with Id: "+transformer.id+" applied new model.");
        init(request.getPath());

        StatusReply status = StatusReply.newBuilder().setReply("Model applied").build();
        responseObserver.onNext(status);
        responseObserver.onCompleted();
    }

    public static void init(String modelPath) {
        while (Files.notExists(Paths.get(modelPath))){
            try {
                Thread.sleep(1000);
                logger.warn("No model found in "+modelPath);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        transformer = new StreamTransformer(1);
        transformer.applyNewModel(modelPath);

        detatchedTransformer = new Thread(transformer);
        detatchedTransformer.start();
    }

    public static void init(){
        transformer = new StreamTransformer(1);
        while (Files.notExists(Paths.get(transformer.MODEL_PATH))){
            try {
                Thread.sleep(1000);
                logger.warn("No model found in "+transformer.MODEL_PATH);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        detatchedTransformer = new Thread(transformer);
        detatchedTransformer.start();
    }
}
