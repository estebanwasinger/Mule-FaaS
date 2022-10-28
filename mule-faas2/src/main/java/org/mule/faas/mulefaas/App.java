package org.mule.faas.mulefaas;

import org.example.ExecutionStatus;

public class App {
    String name;
    String app;
    boolean isRunning;
    String url;
    private String port;
    private long lastExecution;
    private ExecutionStatus executionStatus;

    public App(String name, String app) {
        this.name = name;
        this.app = app;
        this.isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public void setLastExecution(long lastExecution) {
        this.lastExecution = lastExecution;
    }

    public long getLastExecution() {
        return lastExecution;
    }

    public void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }
}
