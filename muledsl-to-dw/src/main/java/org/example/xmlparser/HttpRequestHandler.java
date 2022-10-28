package org.example.xmlparser;

import org.example.DwAction;
import org.example.HttpRequestAction;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Optional;

public class HttpRequestHandler implements StepHandler{
    @Override
    public boolean handles(String name) {
        return "http:request".equals(name);
    }

    @Override
    public DwAction handle(Node item) {
        String url = item.getAttributes().getNamedItem("url").getNodeValue();

        Optional<Node> childNode = getChildNode(item, "http:headers");
        Optional<String> optionalHeader = childNode.map(node -> {
            return node.getChildNodes().item(0).getNodeValue();
        });


        String strValue;
        if(url == null) {
            strValue = "$";
        } else {
            strValue = url;
            if(strValue.startsWith("#[")) {
                String substring = strValue.substring(2);
                strValue = substring.substring(0, substring.length() - 1);
            } else {
                strValue = "\"" + strValue + "\"";
            }

            strValue = strValue.replace("payload", "$.payload").replace("attributes", "$.attributes");
        }
        return new HttpRequestAction(strValue, optionalHeader);
    }

    public static Optional<Node> getChildNode(Node node, String name) {
        NodeList childNodes = node.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {

            String nodeName = childNodes.item(i).getNodeName();
          //  System.out.println(nodeName);
            if(nodeName.equals(name)) {
                return Optional.of(childNodes.item(i));
            }
        }
        return Optional.empty();
    }
}
