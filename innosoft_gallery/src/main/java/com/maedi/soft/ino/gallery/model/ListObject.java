package com.maedi.soft.ino.gallery.model;

import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

public class ListObject extends ArrayList<ListObject> {

    public String s1;
    public int i1;
    public int i2;
    public Bitmap bitmap1;

    public ListObject() { }

    public ListObject(String s1, int i1, int i2)
    {
        this.s1 = s1;
        this.i1 = i1;
        this.i2 = i2;
    }

    public ListObject(String s1)
    {
        this.s1 = s1;
    }

    public ListObject(Bitmap bitmap1) {
        super();
        this.bitmap1 = bitmap1;
    }

    public ListObject(String s1, Bitmap bitmap1) {
        super();
        this.s1 = s1;
        this.bitmap1 = bitmap1;
    }

    public static String[] MainMenuTabsTitle(FragmentActivity f){
        return new String[]{
                "GALLERY",
                "CAMERA"
        };
    }
}
