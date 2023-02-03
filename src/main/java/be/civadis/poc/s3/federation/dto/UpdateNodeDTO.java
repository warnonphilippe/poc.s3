package be.civadis.poc.s3.federation.dto;

import java.util.HashMap;
import java.util.Map;

public class UpdateNodeDTO {

    private String name;
    private Map<String, String> properties = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
