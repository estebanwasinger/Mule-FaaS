package org.mule.faas.mulefaas;

public class PublicApp {

    String name;
    String app;
    boolean isRunning;
    String url;

    public PublicApp(String name, String app, boolean isRunning, String url) {
        this.name = name;
        this.app = app;
        this.isRunning = isRunning;
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
}
