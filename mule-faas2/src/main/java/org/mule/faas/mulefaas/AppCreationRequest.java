package org.mule.faas.mulefaas;

public class AppCreationRequest {

    public String name;
    public String app;

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

    @Override
    public String toString() {
        return "AppCreationRequest{" +
                "name='" + name + '\'' +
                ", app='" + app + '\'' +
                '}';
    }
}
