package com.project.smartcartapp.Model;

public class Category {
    private String catName;
    private String catImage;
    private String catId;

    public Category(String catName, String catImage, String catId) {
        this.catName = catName;
        this.catImage = catImage;
        this.catId = catId;
    }

    public Category(){

    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatImage() {
        return catImage;
    }

    public void setCatImage(String catImage) {
        this.catImage = catImage;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }
}
