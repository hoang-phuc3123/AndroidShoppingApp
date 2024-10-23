package com.project.mainprojectprm231.models;

import java.util.List;

public class ApiCartResponse {
    private Data data;
    private boolean isSuccess;
    private String message;
    private String status;

    public static class Data {
        private List<CartItem> content; // List of cart items
        private int pageNo;
        private int pageSize;
        private int totalElement;
        private int totalPage;
        private boolean isLastPage;
        private boolean isFirstPage;

        // Getters and setters for Data class
        public List<CartItem> getContent() {
            return content;
        }

        public void setContent(List<CartItem> content) {
            this.content = content;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalElement() {
            return totalElement;
        }

        public void setTotalElement(int totalElement) {
            this.totalElement = totalElement;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public boolean isLastPage() {
            return isLastPage;
        }

        public void setLastPage(boolean lastPage) {
            isLastPage = lastPage;
        }

        public boolean isFirstPage() {
            return isFirstPage;
        }

        public void setFirstPage(boolean firstPage) {
            isFirstPage = firstPage;
        }
    }

    // Getters and setters for ApiCartResponse class
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
