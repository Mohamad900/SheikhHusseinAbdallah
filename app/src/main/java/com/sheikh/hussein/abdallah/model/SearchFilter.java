package com.sheikh.hussein.abdallah.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class SearchFilter implements Serializable {
    public Playlist playlist = new Playlist();
    public Set<Tag> tags = new HashSet<>();

    public boolean isDefault() {
        return (playlist.CategoryId == null || playlist.CategoryId.equals(""))  && tags.size() == 0;
    }
}
