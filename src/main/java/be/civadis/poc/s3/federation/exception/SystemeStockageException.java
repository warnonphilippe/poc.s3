package be.civadis.poc.s3.federation.exception;

import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class SystemeStockageException extends Exception {
    //TODO: Should be changed to 2048 but wait that the database is checked first(message_erreur size)
    public static final int MAX_SIZE_ERROR_MESSAGE = 255;
    private final int statusCode;
    private final String alfrescoSummary;

    private static final String METHOD_KEY = "methodKey";
    private static final String ERROR_KEY = "errorKey";

    private final Map<String, String> context = new HashMap<>();

    public SystemeStockageException() {
        this(null, "", HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, null);
    }

    public SystemeStockageException(String message) {
        this(null, message, HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, null);

    }

    public SystemeStockageException(String message, Throwable cause) {
        this(null, message, HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, cause);
    }

    public SystemeStockageException(String methodKey, String message) {
        this(methodKey, message, HttpStatus.INTERNAL_SERVER_ERROR.value(), null, null, null);
    }

    public SystemeStockageException(String methodKey, String message, int statusCode, String alfrescoErrorKey, String alfrescoSummary) {
        this(methodKey, message, statusCode, alfrescoErrorKey, alfrescoSummary, null);
    }

    public SystemeStockageException(String methodKey, String message, int statusCode, String alfrescoErrorKey, String alfrescoSummary, Throwable cause) {
        super(message.concat(StringUtils.isNotBlank(alfrescoSummary) ? " " + alfrescoSummary : ""), cause);
        this.statusCode = statusCode;
        this.alfrescoSummary = alfrescoSummary;
        initContext(methodKey, alfrescoErrorKey);
    }

    private void initContext(String methodKey, String errorKey) {
        context.put(METHOD_KEY, methodKey);
        context.put(ERROR_KEY, errorKey);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getAlfrescoSummary() {
        return alfrescoSummary;
    }

    public Map<String, String> getContext() {
        return context;
    }

    @Override
    public String getMessage() {
        return StringUtils.isNotBlank(super.getMessage()) && super.getMessage().length() > MAX_SIZE_ERROR_MESSAGE ? super.getMessage().substring(0, MAX_SIZE_ERROR_MESSAGE): super.getMessage();
    }
}
