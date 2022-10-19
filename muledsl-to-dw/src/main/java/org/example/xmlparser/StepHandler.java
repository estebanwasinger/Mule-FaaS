package org.example.xmlparser;

import org.example.DwAction;
import org.w3c.dom.Node;

public interface StepHandler {
        boolean handles(String name);

        DwAction handle(Node item);
}
