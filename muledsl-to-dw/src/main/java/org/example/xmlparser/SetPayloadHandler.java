package org.example.xmlparser;

import org.example.DwAction;
import org.example.MapAction;
import org.w3c.dom.Node;

public class SetPayloadHandler implements StepHandler {
    @Override
    public boolean handles(String name) {
        return "set-payload".equals(name);
    }

    @Override
    public DwAction handle(Node item) {
        Node value = item.getAttributes().getNamedItem("value");
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
        return new MapAction(String.format(" { payload : %s, attributes : $.attributes }", strValue));
    }
}
