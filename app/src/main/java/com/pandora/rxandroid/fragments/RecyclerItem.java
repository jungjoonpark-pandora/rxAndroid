package com.pandora.rxandroid.fragments;

import android.graphics.drawable.Drawable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
class RecyclerItem {
    private Drawable image;
    private String title;
}
