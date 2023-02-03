package be.civadis.poc.s3.federation.exception;

import java.util.HashMap;
import java.util.Map;

public class GpdocValidationException extends Exception {

    private final Map<String, String> context;

    public GpdocValidationException(String s) {
        super(s);
        this.context = new HashMap<>();
    }

    public GpdocValidationException(String s, Map<String, String> context) {
        super(s);
        this.context = context;
    }

    public GpdocValidationException(String s, Throwable throwable) {
        super(s, throwable);
        this.context = new HashMap<>();
    }
    public Map<String, String> getContext() {
        return context;
    }

    public void addToContext(String key, String value) {
        this.context.put(key, value);
    }
}
