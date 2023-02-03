package be.civadis.poc.s3.federation.dto;

public class ErrorWrapperDTO {
    private ErrorDTO error;

    public ErrorDTO getError() {
        return error;
    }

    public void setError(ErrorDTO error) {
        this.error = error;
    }
}
