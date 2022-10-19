package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HttpServer implements DwGenerator {

    private final String host;
    private final String port;

    private final List<EndpointBuilder> endpointBuilders = new ArrayList<>();

    public HttpServer(String host, int port) {
        this.host = host;
        this.port = String.valueOf(port);
    }

    public HttpServer(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public EndpointBuilder withEndpoint(String endpoint) {
        EndpointBuilder endpointBuilder = new EndpointBuilder(endpoint);
        endpointBuilders.add(endpointBuilder);
        return endpointBuilder;
    }

    public class EndpointBuilder implements ActionChain<EndpointBuilder> {
        private String endpoint;
        private List<DwAction> actions = new ArrayList<>();

        public EndpointBuilder(String endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public EndpointBuilder withAction(DwAction action) {
            actions.add(action);
            return this;
        }

        public String getCode() {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("\"%s\": { GET: ((request) -> ", endpoint));
            for (int i = 0; i < actions.size(); i++) {
                DwAction dwAction = actions.get(i);
                if(i == 0) {
                    dwAction.addInputParam("request");
                    builder.append(dwAction.toCode());
                    builder.append("\n");
                } else {
                    dwAction.addInputParam("result");
                    builder.append(String.format("then ((result) -> %s)", dwAction.toCode()));
                    builder.append("\n");
                }

            }
            builder.append(") },");

            return builder.toString();
        }
    }

    @Override
    public List<String> getImports() {
        return Collections.singletonList("import * from dw::io::http::Server\nimport request as rq from dw::io::http::Client");
    }

    @Override
    public List<String> getVars() {
        List<String> list = new ArrayList<>();
        list.add(String.format("var serverConfig: {host: String, port: Number} = { host: \"%s\", port: %s }",host, port));
        list.add(" fun logAndReturn<T>(valueToLog: Any, valueToReturn: T) : T = log(valueToLog) then ((previousResult) -> valueToReturn)\n");
        return list;
    }

    @Override
    public String getCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("api(\n" +
                "  serverConfig, \n" +
                "  {\n" );
        endpointBuilders.forEach(e -> builder.append(e.getCode()));

        return builder.toString();
    }

}
