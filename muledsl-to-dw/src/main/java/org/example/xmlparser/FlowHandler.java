package org.example.xmlparser;

import org.example.DwAction;
import org.example.MapAction;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlowHandler implements MuleAppHandler {

    List<StepHandler> stepHandlerList = new ArrayList<>();
    @Override
    public boolean handles(String name) {
        stepHandlerList.add(new LoggerHandler());
        stepHandlerList.add(new SetPayloadHandler());
        stepHandlerList.add(new HttpRequestHandler());
        return name.equals("flow");
    }

    @Override
    public void handle(Node item, MuleAppStructure muleAppStructure) {
        String path = "";
        List<DwAction> dwActions = new ArrayList<>();
        NodeList childNodes = item.getChildNodes();
        dwActions.add(new MapAction("{ payload : request.body , attributes : request - 'body' }"));
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item1 = childNodes.item(i);
            String nodeName = item1.getNodeName();
            System.out.println(nodeName);
            if(nodeName.equals("http:listener")) {
                path = new HttpListenerHandler().getPath(item1);
            } else {
                Optional<StepHandler> optionalStepHandler = stepHandlerList.stream().filter(h -> h.handles(nodeName)).findAny();
                if(optionalStepHandler.isPresent()) {
                    dwActions.add(optionalStepHandler.get().handle(item1));
                } else {
                    if(!nodeName.equals("#text")) {
                        throw new RuntimeException("Unable to parse " + nodeName);
                    }
                }
            }
        }
        dwActions.add(new MapAction("{ body : $.payload }"));
        muleAppStructure.addFlow(path, dwActions);
    }
}
