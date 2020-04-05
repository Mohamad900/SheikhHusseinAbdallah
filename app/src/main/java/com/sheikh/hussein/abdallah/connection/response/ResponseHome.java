package com.sheikh.hussein.abdallah.connection.response;



import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.model.Tag;
import com.sheikh.hussein.abdallah.model.Video;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseHome implements Serializable {

    //public String status = "";
    public List<Video> featuredVideos = new ArrayList<>();
    public List<Video> recentVideos = new ArrayList<>();
    public List<Playlist> featuredCategories = new ArrayList<>();
    public List<Tag> tags = new ArrayList<>();

}
