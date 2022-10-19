package org.example.xmlparser;


import org.example.DwAction;
import org.example.DwCodeGenerator;
import org.example.HttpServer;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.example.Main.executeDwCode;

public class MuleAppParser {


    private final DocumentBuilder builder;
    private final List<MuleAppHandler> handlers = new ArrayList<>();

    public static void main(String... args) throws ParserConfigurationException, IOException, SAXException {
        MuleAppParser muleAppParser = new MuleAppParser();
        muleAppParser.parse();



    }

    public MuleAppParser() throws ParserConfigurationException {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        handlers.add(new FlowHandler());
        handlers.add(new HttpListenerConfigHandler());
    }

    public void parse() throws IOException, SAXException {
        InputStream input = new FileInputStream("/Users/estebanwasinger/IdeaProjects/muledsl-to-dw/src/main/resources/http-logger.xml");
        String build = generateMuleApp(input);
        System.out.println(build);

        executeDwCode(build);

    }

    public String generateMuleApp(InputStream input) throws SAXException, IOException {
        Document doc = builder.parse(input);
        Element root = doc.getDocumentElement();
        String tagName = root.getTagName();

        MuleAppStructure muleAppStructure = new MuleAppStructure();


        if (tagName.equals("mule")) {
            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                String nodeName = item.getNodeName();
                System.out.println(nodeName);
                Optional<MuleAppHandler> first = handlers.stream().filter(h -> h.handles(nodeName)).findFirst();
                first.ifPresent(muleAppHandler -> muleAppHandler.handle(item, muleAppStructure));
            }
        }

        DwCodeGenerator dwCodeGenerator = new DwCodeGenerator();

        Map<String, String> httpConfig = muleAppStructure.getHttpConfig();
        HttpServer httpServer = new HttpServer(httpConfig.get("host"), Integer.parseInt(httpConfig.get("port")));
        dwCodeGenerator.addComponent(httpServer);

        for (Map.Entry<String, List<DwAction>> stringListEntry : muleAppStructure.getFlows().entrySet()) {
            HttpServer.EndpointBuilder endpointBuilder = httpServer.withEndpoint(stringListEntry.getKey());
            stringListEntry.getValue().forEach(endpointBuilder::withAction);
        }

        String build = dwCodeGenerator.build();
        return build;
    }

    public String generateMuleApp(InputStream input, String host, String port) throws SAXException, IOException {
        Document doc = builder.parse(input);
        Element root = doc.getDocumentElement();
        String tagName = root.getTagName();

        MuleAppStructure muleAppStructure = new MuleAppStructure();


        if (tagName.equals("mule")) {
            NodeList childNodes = root.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                String nodeName = item.getNodeName();
                System.out.println(nodeName);
                Optional<MuleAppHandler> first = handlers.stream().filter(h -> h.handles(nodeName)).findFirst();
                first.ifPresent(muleAppHandler -> muleAppHandler.handle(item, muleAppStructure));
            }
        }

        DwCodeGenerator dwCodeGenerator = new DwCodeGenerator();

        HttpServer httpServer = new HttpServer(host, port);
        dwCodeGenerator.addComponent(httpServer);

        for (Map.Entry<String, List<DwAction>> stringListEntry : muleAppStructure.getFlows().entrySet()) {
            HttpServer.EndpointBuilder endpointBuilder = httpServer.withEndpoint(stringListEntry.getKey());
            stringListEntry.getValue().forEach(endpointBuilder::withAction);
        }

        String build = dwCodeGenerator.build();
        return build;
    }

}
