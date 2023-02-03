package be.civadis.poc.s3.federation.dto;

public class PagingDTO {

    private Integer maxItems;
    private Integer skipCount;

    public Integer getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }

    public Integer getSkipCount() {
        return skipCount;
    }

    public void setSkipCount(Integer skipCount) {
        this.skipCount = skipCount;
    }
}
