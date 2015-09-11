package com.yo.draggablebanner.bean;

import android.graphics.Bitmap;

/**
 * Created by Kaming on 2015/9/7.
 */
public class BannerItem {
    private String title;
    private Bitmap image;

    public BannerItem(String title, Bitmap image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "BannerItem{" +
                "title='" + title + '\'' +
                ", image=" + image +
                '}';
    }
}
