package be.civadis.poc.s3.federation.exception;

public class NodeNotFoundException extends Exception {

    private final String detail;

    public NodeNotFoundException(String message, String detail) {
        super(message);
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }
}
