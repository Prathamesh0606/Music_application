package com.phoenix.music_application;


import java.io.Serializable;

public class Audio implements Serializable {

    private String path;
    private String title;
    private String album;
    private String artist;
    private String duration;
    private byte[] albumArt;
    private String genre;


    public byte[] getalbumArt() {
        return albumArt;
    }

    public void setalbumArt(byte[] albumArt) {
        this.albumArt = albumArt;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }


    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}

