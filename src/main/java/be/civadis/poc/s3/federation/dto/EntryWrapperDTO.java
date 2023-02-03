package be.civadis.poc.s3.federation.dto;

public class EntryWrapperDTO<T> {
    private T entry;

    public EntryWrapperDTO() {
    }

    public EntryWrapperDTO(T entry) {
        this.entry = entry;
    }

    public T getEntry() {
        return entry;
    }

    public void setEntry(T entry) {
        this.entry = entry;
    }
}
