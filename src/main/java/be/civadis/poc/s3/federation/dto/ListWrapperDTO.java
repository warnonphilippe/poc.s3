package be.civadis.poc.s3.federation.dto;

public abstract class ListWrapperDTO<T> {
    private PaginatedListDTO<T> list;

    protected ListWrapperDTO() {
    }

    protected ListWrapperDTO(PaginatedListDTO<T> list) {
        this.list = list;
    }

    public PaginatedListDTO<T> getList() {
        return list;
    }

    public void setList(PaginatedListDTO<T> list) {
        this.list = list;
    }
}
