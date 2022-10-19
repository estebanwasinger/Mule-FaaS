package org.example;

import java.util.ArrayList;
import java.util.List;

public class DwCodeGenerator {

    public static final String LINE_JUMP = "\n";
    List<DwGenerator> components = new ArrayList<>();
    public DwCodeGenerator addComponent(DwGenerator generator) {
        components.add(generator);
        return this;
    }

    public String build() {
        StringBuilder stringBuilder = new StringBuilder();
        components.forEach(c -> {
            c.getImports().forEach(i -> {
                stringBuilder.append(i);
                stringBuilder.append(LINE_JUMP);
            });
            c.getVars().forEach(v -> {
                stringBuilder.append(v);
                stringBuilder.append(LINE_JUMP);
            });
        });
        stringBuilder.append("---");
        stringBuilder.append(LINE_JUMP);
        stringBuilder.append(components.get(0).getCode());
        stringBuilder.append("})");

        return stringBuilder.toString();
    }


}
