package com.tiffindaddy.app.tiffindaddy.BeanClasses;

import java.io.Serializable;

/**
 * Created by dx on 2/3/2016.
 */
public class TiffinCategories implements Serializable {

    private String categoryId,categoryName,categoryStartTime,categoryEndTime;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryStartTime() {
        return categoryStartTime;
    }

    public void setCategoryStartTime(String categoryStartTime) {
        this.categoryStartTime = categoryStartTime;
    }

    public String getCategoryEndTime() {
        return categoryEndTime;
    }

    public void setCategoryEndTime(String categoryEndTime) {
        this.categoryEndTime = categoryEndTime;
    }
}
