package com.project.mainprojectprm231.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StoreLocationData {
    private List<StoreLocation> content;
    @SerializedName("page-no")
    private int pageNo;
    @SerializedName("page-size")
    private int pageSize;
    @SerializedName("total-element")
    private int totalElement;
    @SerializedName("total-page")
    private int totalPage;
    @SerializedName("is-last-page")
    private boolean isLastPage;
    @SerializedName("is-first-page")
    private boolean isFirstPage;

    public List<StoreLocation> getContent() {
        return content;
    }
}
