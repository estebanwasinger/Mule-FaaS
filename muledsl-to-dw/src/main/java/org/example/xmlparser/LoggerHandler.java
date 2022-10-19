package org.example.xmlparser;

import org.example.DwAction;
import org.example.LoggerAction;
import org.w3c.dom.Node;

public class LoggerHandler implements StepHandler {
    @Override
    public boolean handles(String name) {
        return "logger".equals(name);
    }

    @Override
    public DwAction handle(Node item) {
        Node value = item.getAttributes().getNamedItem("message");
        String strValue;
        if(value == null) {
            strValue = "$";
        } else {
            strValue = value.getNodeValue();
            if(strValue.startsWith("#[")) {
                String substring = strValue.substring(2);
                strValue = substring.substring(0, substring.length() - 1);
            } else {
                strValue = "'" + strValue + "'";
            }

            strValue = strValue.replace("payload", "$.payload").replace("attributes", "$.attributes");
        }

        return new LoggerAction(strValue);
    }
}
