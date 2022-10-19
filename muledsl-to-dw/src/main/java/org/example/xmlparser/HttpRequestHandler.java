package org.example.xmlparser;

import org.example.DwAction;
import org.example.HttpRequestAction;
import org.w3c.dom.Node;

public class HttpRequestHandler implements StepHandler{
    @Override
    public boolean handles(String name) {
        return "http:request".equals(name);
    }

    @Override
    public DwAction handle(Node item) {
        String url = item.getAttributes().getNamedItem("url").getNodeValue();

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
        return new HttpRequestAction(strValue);
    }
}
