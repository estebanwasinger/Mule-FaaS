package org.example.xmlparser;

import org.w3c.dom.Node;

import java.util.Map;

public interface MuleAppHandler {

    boolean handles(String name);

    void handle(Node item, MuleAppStructure muleAppStructure);
}
