package com.maedi.soft.ino.gallery.model;

import java.util.ArrayList;

public class AlbumGallery {

    private int id;

    private String name;

    private String coverUri;

    private ArrayList<PhotoGallery> albumPhotos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }

    public ArrayList<PhotoGallery> getAlbumPhotos() {
        return albumPhotos;
    }

    public void setAlbumPhotos(ArrayList<PhotoGallery> albumPhotos) {
        this.albumPhotos = albumPhotos;
    }
}
