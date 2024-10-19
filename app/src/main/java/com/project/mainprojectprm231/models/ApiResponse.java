package com.project.mainprojectprm231.models;

import java.util.List;

public class ApiResponse {
    private Data data;
    private boolean isSuccess;
    private String message;
    private String status;

    public static class Data {
        private List<Product> content;
        private int pageNo;
        private int pageSize;
        private int totalElement;
        private int totalPage;
        private boolean isLastPage;
        private boolean isFirstPage;

        // Getters and setters
        public List<Product> getContent() {
            return content;
        }

        public void setContent(List<Product> content) {
            this.content = content;
        }

        // Add getters and setters for other fields as needed
    }

    // Getters and setters
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}