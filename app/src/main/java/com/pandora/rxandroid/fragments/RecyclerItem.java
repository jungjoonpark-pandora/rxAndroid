package com.pandora.rxandroid.fragments;

import android.graphics.drawable.Drawable;

public class RecyclerItem {

    Drawable image;
    String title;

    public RecyclerItem(Drawable image, String title) {
        this.image = image;
        this.title = title;
    }

    public Drawable getImage() { return image; }
    public String getTitle() { return title; }
}
