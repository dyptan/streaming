package com.dyptan;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        StreamTransformer transformer = new StreamTransformer();
        Thread detatchedTransformer = new Thread(transformer);
        while (Files.notExists(Paths.get(transformer.MODEL_PATH))){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        detatchedTransformer.start();
    }
}
