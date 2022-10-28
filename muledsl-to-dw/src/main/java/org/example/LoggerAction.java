package org.example;

public class LoggerAction implements DwAction {

    private String value;
    private String inputParam;

    public LoggerAction(String value) {
        this.value = value;
    }

    @Override
    public String toCode() {
        value = value.replace("$(", "###");
        value = value.replace("$", inputParam);
        value = value.replace("###", "$(");
        return String.format("%s logAndReturn %s",value, inputParam);
    }

    @Override
    public void addInputParam(String inputParam) {
        this.inputParam = inputParam;
    }
}
