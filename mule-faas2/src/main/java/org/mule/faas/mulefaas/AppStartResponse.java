package org.mule.faas.mulefaas;

public class AppStartResponse {

    boolean ok;
    AppStatus app;

    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AppStartResponse(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public AppStartResponse(boolean ok, AppStatus app, String message) {
        this.ok = ok;
        this.app = app;
        this.message = message;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public AppStatus getApp() {
        return app;
    }

    public void setApp(AppStatus app) {
        this.app = app;
    }
}
