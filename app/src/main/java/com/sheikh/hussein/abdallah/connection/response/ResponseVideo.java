package com.sheikh.hussein.abdallah.connection.response;


import com.sheikh.hussein.abdallah.model.Video;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseVideo implements Serializable {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public List<Video> videos = new ArrayList<>();

}
