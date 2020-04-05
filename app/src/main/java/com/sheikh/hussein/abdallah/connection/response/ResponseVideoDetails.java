package com.sheikh.hussein.abdallah.connection.response;



import com.sheikh.hussein.abdallah.model.Playlist;
import com.sheikh.hussein.abdallah.model.Tag;
import com.sheikh.hussein.abdallah.model.Video;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseVideoDetails implements Serializable {

    public String status = "";
    public Video video = new Video();
    public List<Playlist> categories = new ArrayList<>();
    public List<Tag> tags = new ArrayList<>();
    public List<Video> related_video = new ArrayList<>();

}
