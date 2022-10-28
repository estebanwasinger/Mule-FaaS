package org.example.xmlparser;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class HttpListenerConfigHandler implements MuleAppHandler{

    @Override
    public boolean handles(String name) {
        return name.equals("http:listener-config");
    }

    @Override
    public void handle(Node item, MuleAppStructure muleAppStructure) {
        Map<String, String> values = new HashMap<>();
       // System.out.println(item);
        NodeList childNodes = item.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item1 = childNodes.item(i);
            if(item1.getNodeName().equals("http:listener-connection")) {
                values.put("host", item1.getAttributes().getNamedItem("host").getNodeValue());
                values.put("port", item1.getAttributes().getNamedItem("port").getNodeValue());
            }
        }
        values.put("configName", item.getAttributes().getNamedItem("name").getNodeValue());

        muleAppStructure.setHttpConfig(values);
    }


}
