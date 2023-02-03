package be.civadis.poc.s3.federation.dto;

import java.util.List;

public class QueryWrapperDTO {
    private QueryDTO query;
    private PagingDTO paging;
    private List<String> fields;
    private String include;

    public QueryWrapperDTO() {
    }

    public QueryWrapperDTO(QueryDTO query) {
        this.query = query;
    }

    public QueryWrapperDTO(QueryDTO query, PagingDTO paging, List<String> fields, String include) {
        this.query = query;
        this.paging = paging;
        this.fields = fields;
        this.include = include;
    }

    public QueryDTO getQuery() {
        return query;
    }

    public void setQuery(QueryDTO query) {
        this.query = query;
    }

    public PagingDTO getPaging() {
        return paging;
    }

    public void setPaging(PagingDTO paging) {
        this.paging = paging;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }
}
