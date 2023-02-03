package be.civadis.poc.s3.federation.dto;

public class TagBodyDTO {
    private String tag;

    public TagBodyDTO() {
    }

    public TagBodyDTO(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
