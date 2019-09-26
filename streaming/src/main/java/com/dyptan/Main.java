package com.dyptan;

public class Main {
    public static void main(String[] args) {
        StreamTransformer transformer = new StreamTransformer();
        Thread detatchedTransformer = new Thread(transformer);
        detatchedTransformer.start();
    }
}
