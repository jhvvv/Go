package com.jh.go;

import android.graphics.Bitmap;

public class GridItem {
    private String imageURL;
    Bitmap photo;

    public GridItem(Bitmap photo) {
        this.photo = photo;
    }

    public Bitmap getPhoto() {
        return photo;
    }
    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getItemString() {
        return this.imageURL;
    }
}