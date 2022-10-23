package org.example;

import java.util.Optional;

public class HttpRequestAction implements DwAction {
    private String url;
    private Optional<String> headers;

    public HttpRequestAction(String url, Optional<String> headers) {
        this.url = url;
        this.headers = headers;
    }

    @Override
    public String toCode() {
//        return String.format("rq(\"GET\", %s).body", url);
        url = url.replace("$(", "#(");
        url = url.replace("$", "result");
        url = url.replace("#(", "$(");
        return String.format("rq(\"GET\", %s, %s) then ((response) -> { payload : response.body, attributes : response - 'body'})", url, getHeaders());
    }

    @Override
    public void addInputParam(String request) {

    }

    private String getHeaders() {
        if(headers.isPresent()) {
            String strHeaders = headers.get();
            String content = strHeaders.split("---")[1];
            content = content.substring(0, content.length() -1);

            return String.format(" { headers : %s }", content);
        } else {
            return "{}";
        }
    }
}
