package org.example.xmlparser;

import org.example.DwAction;
import org.w3c.dom.Node;

import java.util.HashMap;

public class HttpListenerHandler {

    String getPath(Node item) {
        return item.getAttributes().getNamedItem("path").getNodeValue();
    }
}
