package org.example;

public class MapAction implements DwAction {

    private String value;
    private String inputParam;

    public MapAction(String value) {
        this.value = value;
    }

    @Override
    public String toCode() {
        value = value.replace("$(", "###");
        value = value.replace("$", inputParam);
        value = value.replace("###", "$(");
        return value;
    }

    @Override
    public void addInputParam(String inputParam) {
        this.inputParam = inputParam;
    }
}
