package com.ops.cli.observer;

public class ProgressUpdateEvent {

    Integer currentCount;
    Integer totalCount;
    String message;

    public ProgressUpdateEvent(Integer currentRecord, Integer totalRecords) {
        this(currentRecord, totalRecords, null);
    }

    public ProgressUpdateEvent(Integer currentRecord, Integer totalRecords, String message) {
        this.currentCount = currentRecord;
        this.totalCount = totalRecords;
        this.message = message;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
