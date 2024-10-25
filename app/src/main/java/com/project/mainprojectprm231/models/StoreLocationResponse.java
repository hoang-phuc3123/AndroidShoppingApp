package com.project.mainprojectprm231.models;

public class StoreLocationResponse {
    private StoreLocationData data;
    private boolean isSuccess;
    private String message;
    private String status;

    public StoreLocationData getData() {
        return data;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}

