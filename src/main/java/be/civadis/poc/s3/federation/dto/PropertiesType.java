package be.civadis.poc.s3.federation.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PropertiesType {

    VERSION_LABEL("cm:versionLabel"),
    TITLE("cm:title"),
    DESCRIPTION("cm:description"),
    AUTHOR("cm:author"),
    NAME("cm:name");

    private String code;

    PropertiesType(String code) {
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
