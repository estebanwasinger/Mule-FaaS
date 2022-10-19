package org.example.xmlparser;

import org.example.DwAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MuleAppStructure {

    Map<String, String> httpConfig;
    Map<String, List<DwAction>> flows = new HashMap<>();

    void setHttpConfig(Map<String, String> config) {
        httpConfig = config;
    }

    void addFlow(String endpoint, List<DwAction> steps) {
        flows.put(endpoint, steps);
    }

    public Map<String, String> getHttpConfig() {
        return httpConfig;
    }

    public Map<String, List<DwAction>> getFlows() {
        return flows;
    }
}
