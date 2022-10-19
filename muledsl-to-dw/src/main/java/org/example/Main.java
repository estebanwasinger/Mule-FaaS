package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class Main {

    static long time = System.currentTimeMillis();

    public static void printTime() {
        System.out.println(System.currentTimeMillis() - time);
    }

    public static void printTime(String msg) {
        System.out.println(msg + " " + (System.currentTimeMillis() - time));
    }

    public static void main(String[] args) throws IOException {
        DwCodeGenerator dwCodeGenerator = new DwCodeGenerator();
        HttpServer httpServer = new HttpServer("0.0.0.0", 8081);
        httpServer
                .withEndpoint("/")
                .withAction(new LoggerAction("$"))
                .withAction(new MapAction("{body : { root : { msg : 'hi!' } } }"))
                .withAction(new LoggerAction("$ ++ { headers : { 'content-type' : 'application/xml'}}"));


        String build = dwCodeGenerator.addComponent(httpServer).build();
        System.out.println(build);

        executeDwCode(build);

    }

    public static void executeDwCode(String build) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "dw", "--eval", build);
        builder.redirectErrorStream(true);
//        printTime("Start process");
        Process p = builder.start();
//        printTime("Process Started");
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;

        while (true) {
//            printTime("Read line");
            line = r.readLine();
            if (line == null) { break; }
//            printTime("Print line");
            System.out.println(line);
        }
    }

    public static ExecutionStatus startDwCode(String build) throws InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(
                "dw", "--eval", build);
        AtomicReference<Process> processRef = new AtomicReference<>(null);
        AtomicReference<Exception> exceptionReference = new AtomicReference<>(null);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            try {

                builder.redirectErrorStream(true);
                Process p = builder.start();
                processRef.set(p);
                countDownLatch.countDown();
                BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;

                while (true) {
                    line = r.readLine();
                    if (line == null) {
                        break;
                    }
                    System.out.println(line);
                }
            } catch (Exception e) {
                exceptionReference.set(e);
            }
        }).start();
        countDownLatch.await();
        return new ExecutionStatus(processRef.get(), exceptionReference.get());

    }
}