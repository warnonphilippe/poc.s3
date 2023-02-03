package be.civadis.poc.s3.federation.dto;

import java.util.ArrayList;
import java.util.List;

public class PaginatedListDTO<T> {
    private PaginationDTO pagination = new PaginationDTO();
    private List<EntryWrapperDTO<T>> entries = new ArrayList<>();

    public PaginatedListDTO() {
    }

    public PaginatedListDTO(T entry) {
        pagination.setCount(1);
        pagination.setTotalItems(1);
        entries.add(new EntryWrapperDTO<>(entry));
    }

    public PaginatedListDTO(PaginationDTO pagination, List<EntryWrapperDTO<T>> entries) {
        this.pagination = pagination;
        this.entries = entries;
    }

    public PaginationDTO getPagination() {
        return pagination;
    }

    public void setPagination(PaginationDTO pagination) {
        this.pagination = pagination;
    }

    public List<EntryWrapperDTO<T>> getEntries() {
        return entries;
    }

    public void setEntries(List<EntryWrapperDTO<T>> entries) {
        this.entries = entries;
    }

    public void setPaginationInfos(int count) {
        this.getPagination().setTotalItems(count);
        this.getPagination().setCount(count);
        this.getPagination().setMaxItems(0);
        this.getPagination().setSkipCount(0);
    }

    public void addEntry(T obj) {
        this.getEntries().add(new EntryWrapperDTO<>(obj));
    }

    public void addEntryList(List<T> list) {
        if (list != null) {
            for (T item : list) {
                addEntry(item);
            }
        }
    }
}
