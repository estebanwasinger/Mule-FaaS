package org.mule.faas.mulefaas;

public class AppCreationResponse {

    boolean ok;

    public AppCreationResponse(boolean ok) {
        this.ok = ok;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
