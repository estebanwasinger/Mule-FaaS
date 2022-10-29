package org.example;

import org.example.xmlparser.MuleAppParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
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

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        DwCodeGenerator dwCodeGenerator = new DwCodeGenerator();
        MuleAppParser parser = new MuleAppParser();
        String build = parser.generateMuleApp(new FileInputStream("/Users/estebanwasinger/Documents/Mule-FaaS/muledsl-to-dw/src/main/resources/http-logger.xml"), "0.0.0.0", "${http.port}");

        System.out.println(build);

        build = build.replace("${http.port}", "8081");

        executeDwCode(build);

    }

    public static void executeDwCode(String build) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "dw", "--version", build);
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
//        ProcessBuilder builder = new ProcessBuilder(
//                "/Users/ewasinger/.dw/bin/dw", "--eval", build);
        ProcessBuilder builder = new ProcessBuilder("dw", "--eval", build);
//        ProcessBuilder builder = new ProcessBuilder("echo", "$PATH");
        AtomicReference<Process> processRef = new AtomicReference<>(null);
        AtomicReference<Exception> exceptionReference = new AtomicReference<>(null);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            try {

                builder.redirectErrorStream(true);
                Process p = builder.start();
                processRef.set(p);
                Thread.sleep(500);
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