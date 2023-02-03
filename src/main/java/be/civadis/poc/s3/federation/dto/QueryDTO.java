package be.civadis.poc.s3.federation.dto;

public class QueryDTO {
    private String language;
    private String query;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
