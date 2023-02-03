package be.civadis.poc.s3.federation.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NodeType {

    FOLDER("cm:folder"),
    CONTENT("cm:content");

    private String code;

    NodeType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
