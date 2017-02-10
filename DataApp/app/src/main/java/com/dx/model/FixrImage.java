package com.dx.model;

/**
 * Created by dx on 21/04/2015.
 */
public class FixrImage {
    int imageId;

    public FixrImage() {
    }

    public FixrImage(String imageName,String imageUri ) {
        this.imageUri = imageUri;
        this.imageName = imageName;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    String imageName;
    String imageUri;
}
