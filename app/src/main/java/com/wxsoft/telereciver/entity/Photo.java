package com.wxsoft.telereciver.entity;

import java.io.Serializable;

public class Photo implements Serializable{

    private String localPath;
    private String category;

    public Photo(String localPath, String category) {
        this.localPath = localPath;
        this.category = category;
    }

    public String getLocalPath() {
        return localPath;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
