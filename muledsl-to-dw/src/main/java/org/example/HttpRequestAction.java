package org.example;

public class HttpRequestAction implements DwAction {
    private String url;

    public HttpRequestAction(String url) {
        this.url = url;
    }

    @Override
    public String toCode() {
//        return String.format("rq(\"GET\", %s).body", url);
        return String.format("rq(\"GET\", %s) then ((response) -> { payload : response.body, attributes : response - 'body'})", url);
    }

    @Override
    public void addInputParam(String request) {

    }
}
