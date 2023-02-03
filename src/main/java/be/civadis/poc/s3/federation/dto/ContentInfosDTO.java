package be.civadis.poc.s3.federation.dto;

public class ContentInfosDTO {

    private String mimeType;
    private String mimeTypeName;
    private Long sizeInBytes;
    private String encoding;
    private String mimeTypeGroup;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeTypeName() {
        return mimeTypeName;
    }

    public void setMimeTypeName(String mimeTypeName) {
        this.mimeTypeName = mimeTypeName;
    }

    public Long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(Long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getMimeTypeGroup() {
        return mimeTypeGroup;
    }

    public void setMimeTypeGroup(String mimeTypeGroup) {
        this.mimeTypeGroup = mimeTypeGroup;
    }
}
