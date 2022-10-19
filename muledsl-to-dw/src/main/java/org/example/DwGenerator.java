package org.example;

import java.util.List;

public interface DwGenerator {

    List<String> getImports();

    List<String> getVars();

    String getCode();
}
