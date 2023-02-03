package be.civadis.poc.s3.federation.dto;

public class TagDTO {
    private String id;
    private String tag;

    public TagDTO() {
    }

    public TagDTO(String id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
